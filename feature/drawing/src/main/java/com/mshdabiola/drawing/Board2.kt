package com.mshdabiola.drawing

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Minimize
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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
 */
data class DrawingPath(val path: Path, val color: Color, val strokeWidth: Float)

/**
 * The main screen composable for the drawing application.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawingScreen() {
    // State to hold all the drawn paths
    val drawingPaths = remember { mutableStateListOf<DrawingPath>() }

    // State for the currently selected drawing tool
    var currentTool by remember { mutableStateOf(DrawingTool.DRAW) }

    // State for the currently selected drawing color
    var currentColor by remember { mutableStateOf(Color.Black) }

    // State for the current stroke width
    var currentStrokeWidth by remember { mutableStateOf(10f) }

    // State for the current path being drawn (for continuous drawing)
    var currentPath by remember { mutableStateOf(Path()) }

    // State for the start point of a drag gesture (used for drawing and selection)
    var startDragPoint by remember { mutableStateOf(Offset.Unspecified) }

    // State for the current selection rectangle (for the SELECT tool)
    var selectionRect by remember { mutableStateOf<Pair<Offset, Offset>?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Compose Drawing App") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            // Toolbar for selecting drawing tools and properties
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Draw Tool Button
                IconToggleButton(
                    checked = currentTool == DrawingTool.DRAW,
                    onCheckedChange = { if (it) currentTool = DrawingTool.DRAW }
                ) {
                    Icon(
                        Icons.Filled.Create,
                        contentDescription = "Draw Tool",
                        tint = if (currentTool == DrawingTool.DRAW) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Erase Tool Button
                IconToggleButton(
                    checked = currentTool == DrawingTool.ERASE,
                    onCheckedChange = { if (it) currentTool = DrawingTool.ERASE }
                ) {
                    Icon(
                        Icons.Filled.Minimize, // Using Minimize for erase, could be a custom icon
                        contentDescription = "Erase Tool",
                        tint = if (currentTool == DrawingTool.ERASE) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Selection Tool Button
                IconToggleButton(
                    checked = currentTool == DrawingTool.SELECT,
                    onCheckedChange = { if (it) currentTool = DrawingTool.SELECT }
                ) {
                    Icon(
                        Icons.Filled.SelectAll, // Using SelectAll for selection
                        contentDescription = "Selection Tool",
                        tint = if (currentTool == DrawingTool.SELECT) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Color Picker Button
                ColorPickerButton(
                    selectedColor = currentColor,
                    onColorSelected = { color -> currentColor = color }
                )

                // Stroke Width Slider
                Slider(
                    value = currentStrokeWidth,
                    onValueChange = { currentStrokeWidth = it },
                    valueRange = 1f..50f, // Define min and max stroke width
                    modifier = Modifier.width(120.dp)
                )

                // Clear Canvas Button
                IconButton(onClick = {
                    drawingPaths.clear()
                    selectionRect = null // Also clear any active selection
                }) {
                    Icon(Icons.Filled.Clear, contentDescription = "Clear Canvas")
                }
            }
        }
    ) { paddingValues ->
        // Drawing Canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White) // Background color of the canvas
                .pointerInput(currentTool) { // Key the pointerInput to currentTool for recomposition
                    detectDragGestures(
                        onDragStart = { offset ->
                            startDragPoint = offset
                            currentPath = Path().apply { moveTo(offset.x, offset.y) }
                            selectionRect = null // Clear selection when new drawing/erase starts
                        },
                        onDrag = { change, dragAmount ->
                            val newX = startDragPoint.x + dragAmount.x
                            val newY = startDragPoint.y + dragAmount.y

                            when (currentTool) {
                                DrawingTool.DRAW, DrawingTool.ERASE -> {
                                    currentPath.lineTo(change.position.x, change.position.y)
                                }
                                DrawingTool.SELECT -> {
                                    selectionRect = Pair(startDragPoint, change.position)
                                }
                            }
                            // Consume the change to prevent other gestures from interfering
                            change.consume()
                        },
                        onDragEnd = {
                            when (currentTool) {
                                DrawingTool.DRAW -> {
                                    drawingPaths.add(DrawingPath(currentPath, currentColor, currentStrokeWidth))
                                }
                                DrawingTool.ERASE -> {
                                    // When erasing, we add a path with the background color
                                    // This simulates erasing by drawing over
                                    drawingPaths.add(DrawingPath(currentPath, Color.White, currentStrokeWidth))
                                }
                                DrawingTool.SELECT -> {
                                    // Selection is visual, nothing permanent added to drawingPaths
                                }
                            }
                        }
                    )
                }
        ) {
            // Draw all existing paths
            drawingPaths.forEach { drawingPath ->
                drawPath(
                    path = drawingPath.path,
                    color = drawingPath.color,
                    style = Stroke(width = drawingPath.strokeWidth)
                )
            }

            // Draw the current path being drawn (for visual feedback during drag)
            if (currentTool == DrawingTool.DRAW) {
                drawPath(
                    path = currentPath,
                    color = currentColor,
                    style = Stroke(width = currentStrokeWidth)
                )
            } else if (currentTool == DrawingTool.ERASE) {
                // When erasing, draw the current path with the background color
                // and a larger stroke for a more effective erase
                drawPath(
                    path = currentPath,
                    color = Color.White, // Use canvas background color for erasing
                    style = Stroke(width = currentStrokeWidth * 1.5f) // Make erase wider
                )
            }

            // Draw the selection rectangle if active
            selectionRect?.let { (start, end) ->
                drawRect(
                    color = Color.Blue.copy(alpha = 0.5f), // Semi-transparent blue
                    topLeft = Offset(minOf(start.x, end.x), minOf(start.y, end.y)),
                    size = Size(
                        width = (end.x - start.x).coerceAtLeast(0f).coerceAtMost(size.width),
                        height = (end.y - start.y).coerceAtLeast(0f).coerceAtMost(size.height)
                    ),
                    style = Stroke(width = 2.dp.toPx()),
                )
            }
        }
    }
}

/**
 * A simple composable for selecting colors.
 *
 * @param selectedColor The currently selected color.
 * @param onColorSelected Callback when a new color is selected.
 */
@Composable
fun ColorPickerButton(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    IconButton(onClick = { showDialog = true }) {
        Icon(
            Icons.Filled.ColorLens,
            contentDescription = "Select Color",
            tint = selectedColor
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
                        Color.Yellow, Color.Cyan, Color.Magenta, Color.Gray
                    )
                    colors.chunked(4).forEach { rowColors ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            rowColors.forEach { color ->
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .padding(4.dp)
                                        .background(color, RoundedCornerShape(8.dp))
                                        .border(2.dp, if (selectedColor == color) MaterialTheme.colorScheme.primary else Color.Transparent, RoundedCornerShape(8.dp))
                                        .clickable {
                                            onColorSelected(color)
                                            showDialog = false
                                        }
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
            }
        )
    }
}

/**
 * Preview for the DrawingScreen.
 */
@Preview(showBackground = true)
@Composable
fun DrawingScreenPreview2() {
        DrawingScreen()

}
