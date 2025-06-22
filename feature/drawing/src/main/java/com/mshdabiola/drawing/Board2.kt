import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Minimize
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

enum class DrawingTool {
    DRAW,    // For freehand drawing
    ERASE,   // For "erasing" by drawing with background color
    SELECT   // For visually selecting an area
}

/**
 * Represents a single drawn path on the canvas.
 *
 * @property path The Path object representing the stroke.
 * @property color The color of the stroke.
 * @property strokeWidth The width of the stroke.
 * @property isSelected Whether this path is currently selected.
 */
data class DrawingPath(
    val path: Path,
    val color: Color,
    val strokeWidth: Float,
    var isSelected: Boolean = false, // Added for selection highlighting
)

/**
 * The main screen composable for the drawing application.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawingScreen() {
    val drawingPaths = remember { mutableStateListOf<DrawingPath>() }
    var currentTool by remember { mutableStateOf(DrawingTool.DRAW) }
    var currentColor by remember { mutableStateOf(Color.Black) }
    var currentStrokeWidth by remember { mutableStateOf(10f) }
    var currentPath by remember { mutableStateOf(Path()) }
    var startDragPoint by remember { mutableStateOf(Offset.Unspecified) }
    var selectionRect by remember { mutableStateOf<Rect?>(null) } // For drawing the drag selection area

    // New state for the collective bounding box of all selected paths
    var collectiveSelectedPathsBounds by remember { mutableStateOf<Rect?>(null) }

    fun clearPathSelections() {
        var didDeselect = false
        drawingPaths.forEachIndexed { index, path ->
            if (path.isSelected) {
                drawingPaths[index] = path.copy(isSelected = false)
                didDeselect = true
            }
        }
        if (didDeselect) { // Only update if something was actually deselected
            collectiveSelectedPathsBounds = null
        }
    }

    // Function to calculate and update the collective bounding box
    fun updateCollectiveSelectedBounds() {
        val selected = drawingPaths.filter { it.isSelected }
        if (selected.isEmpty()) {
            collectiveSelectedPathsBounds = null
            return
        }

        var newBounds: Rect? = null
        selected.forEach { drawingPath ->
            val pathBounds = drawingPath.path.getBounds()
            newBounds = newBounds?.let { current ->
                // Expand current bounds to include pathBounds
                Rect(
                    left = minOf(current.left, pathBounds.left),
                    top = minOf(current.top, pathBounds.top),
                    right = maxOf(current.right, pathBounds.right),
                    bottom = maxOf(current.bottom, pathBounds.bottom),
                )
            }
                ?: pathBounds // If newBounds is null, initialize with the first selected path's bounds
        }
        collectiveSelectedPathsBounds = newBounds
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Compose Drawing App") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconToggleButton(
                    // DRAW
                    checked = currentTool == DrawingTool.DRAW,
                    onCheckedChange = {
                        if (it) {
                            currentTool = DrawingTool.DRAW
                            clearPathSelections()
                            selectionRect = null
                        }
                    },
                ) {
                    Icon(
                        Icons.Filled.Create,
                        contentDescription = "Draw Tool",
                        tint = if (currentTool == DrawingTool.DRAW) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                IconToggleButton(
                    // ERASE
                    checked = currentTool == DrawingTool.ERASE,
                    onCheckedChange = {
                        if (it) {
                            currentTool = DrawingTool.ERASE
                            clearPathSelections()
                            selectionRect = null
                        }
                    },
                ) {
                    Icon(
                        Icons.Filled.Minimize, // Consider a more erase-like icon
                        contentDescription = "Erase Tool",
                        tint = if (currentTool == DrawingTool.ERASE) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                IconToggleButton(
                    // SELECT
                    checked = currentTool == DrawingTool.SELECT,
                    onCheckedChange = {
                        if (it) currentTool = DrawingTool.SELECT
                        // Do not clear individual selectionRect here, only collective
                    },
                ) {
                    Icon(
                        Icons.Filled.SelectAll,
                        contentDescription = "Selection Tool",
                        tint = if (currentTool == DrawingTool.SELECT) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                ColorPickerButton(
                    selectedColor = currentColor,
                    onColorSelected = { color -> currentColor = color },
                )
                Slider(
                    value = currentStrokeWidth,
                    onValueChange = { currentStrokeWidth = it },
                    valueRange = 1f..50f,
                    modifier = Modifier.width(100.dp), // Adjusted width
                )
                IconButton(
                    onClick = { // CLEAR CANVAS
                        drawingPaths.clear()
                        selectionRect = null
                        clearPathSelections() // This will also nullify collectiveSelectedPathsBounds
                    },
                ) {
                    Icon(Icons.Filled.Clear, contentDescription = "Clear Canvas")
                }
            }
        },
    ) { paddingValues ->
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .pointerInput(currentTool) { // Key on currentTool to re-trigger pointer input setup if tool changes
                    detectDragGestures(
                        onDragStart = { offset ->
                            startDragPoint = offset
                            if (currentTool == DrawingTool.DRAW || currentTool == DrawingTool.ERASE) {
                                currentPath = Path().apply { moveTo(offset.x, offset.y) }
                                if (drawingPaths.any { it.isSelected }) { // Only clear if there's a selection
                                    clearPathSelections()
                                }
                                selectionRect = null // Clear visual selection drag rectangle
                            } else if (currentTool == DrawingTool.SELECT) {
                                // Check if the click is on an existing collective bounding box
                                val clickedOnSelectedArea =
                                    collectiveSelectedPathsBounds?.contains(offset) ?: false

                                if (!clickedOnSelectedArea) {
                                    clearPathSelections() // Clear previous collective selection if clicking outside
                                }
                                // Always start a new visual selection drag rectangle
                                selectionRect = Rect(offset, offset)
                            }
                        },
                        onDrag = { change, _ ->
                            when (currentTool) {
                                DrawingTool.DRAW, DrawingTool.ERASE -> {
                                    currentPath.lineTo(change.position.x, change.position.y)
                                }

                                DrawingTool.SELECT -> {
                                    selectionRect = Rect(startDragPoint, change.position)
                                }
                            }
                            change.consume()
                        },
                        onDragEnd = {
                            when (currentTool) {
                                DrawingTool.DRAW -> {
                                    drawingPaths.add(
                                        DrawingPath(
                                            currentPath,
                                            currentColor,
                                            currentStrokeWidth,
                                        ),
                                    )
                                }

                                DrawingTool.ERASE -> {
                                    drawingPaths.add(
                                        DrawingPath(
                                            currentPath,
                                            Color.White,
                                            currentStrokeWidth,
                                        ),
                                    )
                                }

                                DrawingTool.SELECT -> {
                                    var anySelectedThisDrag = false
                                    selectionRect?.let { rect ->
                                        val normalizedRect = Rect(
                                            left = minOf(rect.left, rect.right),
                                            top = minOf(rect.top, rect.bottom),
                                            right = maxOf(rect.left, rect.right),
                                            bottom = maxOf(rect.top, rect.bottom),
                                        )
                                        drawingPaths.forEachIndexed { index, drawingPath ->
                                            if (normalizedRect.overlaps(drawingPath.path.getBounds())) {
                                                // If not already selected, select it
                                                if (!drawingPaths[index].isSelected) {
                                                    drawingPaths[index] =
                                                        drawingPath.copy(isSelected = true)
                                                }
                                                anySelectedThisDrag = true
                                            }
                                            // Paths outside the current drag are not automatically deselected here,
                                            // that happens in onDragStart if clicking outside an existing selection.
                                        }
                                    }
                                    if (anySelectedThisDrag || drawingPaths.any { it.isSelected }) {
                                        updateCollectiveSelectedBounds()
                                    } else {
                                        // If nothing was selected in this drag AND nothing was previously selected, clear bounds.
                                        collectiveSelectedPathsBounds = null
                                    }
                                    selectionRect =
                                        null // Clear the visual drag selection rectangle
                                }
                            }
                            if (currentTool == DrawingTool.DRAW || currentTool == DrawingTool.ERASE) {
                                currentPath = Path()
                            }
                        },
                    )
                },
        ) {
            drawingPaths.forEach { drawingPath ->
                drawPath(
                    path = drawingPath.path,
                    color = drawingPath.color,
                    style = Stroke(width = drawingPath.strokeWidth),
                )
                // Individual path highlighting is now removed in favor of collectiveSelectedPathsBounds
            }

            // Draw current drawing/erasing path
            if (currentTool == DrawingTool.DRAW) {
                drawPath(
                    path = currentPath,
                    color = currentColor,
                    style = Stroke(width = currentStrokeWidth),
                )
            } else if (currentTool == DrawingTool.ERASE) {
                drawPath(
                    path = currentPath,
                    color = Color.White,
                    style = Stroke(width = currentStrokeWidth * 1.5f),
                ) // Make eraser a bit thicker
            }

            // Draw the visual selection rectangle during drag
            selectionRect?.let { rect ->
                val normalizedRect = Rect(
                    left = minOf(rect.left, rect.right),
                    top = minOf(rect.top, rect.bottom),
                    right = maxOf(rect.left, rect.right),
                    bottom = maxOf(rect.top, rect.bottom),
                )
                drawRect(
                    color = Color.Blue.copy(alpha = 0.3f), // Visual cue for selection area
                    topLeft = normalizedRect.topLeft,
                    size = normalizedRect.size,
                    style = Stroke(width = 1.dp.toPx()),
                )
            }

            // Draw the collective bounding box for ALL selected paths
            collectiveSelectedPathsBounds?.let { bounds ->
                drawRect(
                    color = Color.Magenta.copy(alpha = 0.5f), // Distinct color for the collective highlight
                    topLeft = bounds.topLeft,
                    size = bounds.size,
                    style = Stroke(width = 2.dp.toPx()),
                )
            }
        }
    }
}

@Composable
fun ColorPickerButton(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }

    IconButton(onClick = { showDialog = true }) {
        Icon(
            Icons.Filled.ColorLens,
            contentDescription = "Select Color",
            tint = selectedColor, // Display the currently selected drawing color
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Select Color") },
            text = {
                Column {
                    val colors = listOf(
                        Color.Black, Color.Red, Color.Green, Color.Blue,
                        Color.Yellow, Color.Cyan, Color.Magenta, Color.Gray,
                    )
                    colors.chunked(4).forEach { rowColors ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround,
                        ) {
                            rowColors.forEach { color ->
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .padding(4.dp)
                                        .background(color, RoundedCornerShape(8.dp))
                                        .border(
                                            2.dp,
                                            // Highlight the border if this color is the currently selected drawing color
                                            if (selectedColor == color) MaterialTheme.colorScheme.primary else Color.Transparent,
                                            RoundedCornerShape(8.dp),
                                        )
                                        .clickable {
                                            onColorSelected(color)
                                            showDialog = false
                                        },
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Close")
                }
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DrawingScreenPreview2() { // Renamed Preview to avoid conflict if you have another one
    MaterialTheme { // Added MaterialTheme for preview to provide default styling
        DrawingScreen()
    }
}