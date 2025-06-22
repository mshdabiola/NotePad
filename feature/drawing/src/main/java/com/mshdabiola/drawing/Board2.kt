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
import androidx.compose.material.icons.filled.Delete // Assuming you might use this later
import androidx.compose.material.icons.filled.Minimize
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
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
 * @property isSelected Whether this path is currently selected.
 */
data class DrawingPath(
    val path: Path,
    val color: Color,
    val strokeWidth: Float,
    var isSelected: Boolean = false // Added for selection highlighting
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
    var selectionRect by remember { mutableStateOf<Rect?>(null) } // Changed to Rect for easier intersection checks

    // Function to clear all current selections
    fun clearPathSelections() {
        drawingPaths.forEachIndexed { index, path ->
            if (path.isSelected) {
                drawingPaths[index] = path.copy(isSelected = false)
            }
        }
    }

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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconToggleButton(
                    checked = currentTool == DrawingTool.DRAW,
                    onCheckedChange = {
                        if (it) {
                            currentTool = DrawingTool.DRAW
                            clearPathSelections() // Clear selection when switching to draw
                            selectionRect = null
                        }
                    }
                ) {
                    Icon(
                        Icons.Filled.Create,
                        contentDescription = "Draw Tool",
                        tint = if (currentTool == DrawingTool.DRAW) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconToggleButton(
                    checked = currentTool == DrawingTool.ERASE,
                    onCheckedChange = {
                        if (it) {
                            currentTool = DrawingTool.ERASE
                            clearPathSelections() // Clear selection when switching to erase
                            selectionRect = null
                        }
                    }
                ) {
                    Icon(
                        Icons.Filled.Minimize,
                        contentDescription = "Erase Tool",
                        tint = if (currentTool == DrawingTool.ERASE) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconToggleButton(
                    checked = currentTool == DrawingTool.SELECT,
                    onCheckedChange = { if (it) currentTool = DrawingTool.SELECT }
                ) {
                    Icon(
                        Icons.Filled.SelectAll,
                        contentDescription = "Selection Tool",
                        tint = if (currentTool == DrawingTool.SELECT) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                ColorPickerButton(
                    selectedColor = currentColor,
                    onColorSelected = { color -> currentColor = color }
                )
                Slider(
                    value = currentStrokeWidth,
                    onValueChange = { currentStrokeWidth = it },
                    valueRange = 1f..50f,
                    modifier = Modifier.width(120.dp)
                )
                IconButton(onClick = {
                    drawingPaths.clear()
                    selectionRect = null
                    clearPathSelections() // Ensure isSelected flags are also cleared
                }) {
                    Icon(Icons.Filled.Clear, contentDescription = "Clear Canvas")
                }
            }
        }
    ) { paddingValues ->
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .pointerInput(currentTool) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            startDragPoint = offset
                            if (currentTool == DrawingTool.DRAW || currentTool == DrawingTool.ERASE) {
                                currentPath = Path().apply { moveTo(offset.x, offset.y) }
                                clearPathSelections() // Clear selection when starting to draw/erase
                                selectionRect = null
                            } else if (currentTool == DrawingTool.SELECT) {
                                // If not clicking on an already selected path, clear previous selections
                                val clickedOnSelectedPath = drawingPaths.any {
                                    it.isSelected && it.path.getBounds().contains(offset)
                                }
                                if (!clickedOnSelectedPath) {
                                    clearPathSelections()
                                }
                                selectionRect = Rect(offset, offset) // Initialize selection rect
                            }
                        },
                        onDrag = { change, _ -> // dragAmount not directly used for SELECT as we use change.position
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
                                    drawingPaths.add(DrawingPath(currentPath, currentColor, currentStrokeWidth))
                                }
                                DrawingTool.ERASE -> {
                                    drawingPaths.add(DrawingPath(currentPath, Color.White, currentStrokeWidth))
                                }
                                DrawingTool.SELECT -> {
                                    selectionRect?.let { rect ->
                                        // It's important to normalize the rect so topLeft is actually top-left
                                        val normalizedRect = Rect(
                                            left = minOf(rect.left, rect.right),
                                            top = minOf(rect.top, rect.bottom),
                                            right = maxOf(rect.left, rect.right),
                                            bottom = maxOf(rect.top, rect.bottom)
                                        )
                                        drawingPaths.forEachIndexed { index, drawingPath ->
                                            // Check if path bounds overlap with the selection rectangle
                                            // Path.getBounds() gives the bounding box of the path.
                                            // We use intersects for a more accurate check than just contains.
                                            if (normalizedRect.overlaps(drawingPath.path.getBounds())) {
                                                drawingPaths[index] = drawingPath.copy(isSelected = true)
                                            }
                                            // else { // Optionally deselect if not in current rect, if that's the desired behavior
                                            //    if (drawingPaths[index].isSelected) drawingPaths[index] = drawingPath.copy(isSelected = false)
                                            // }
                                        }
                                    }
                                    // Keep selectionRect visible until a new action clears it
                                }
                            }
                            // Reset currentPath for next drawing operation only if it was a drawing tool
                            if (currentTool == DrawingTool.DRAW || currentTool == DrawingTool.ERASE) {
                                currentPath = Path() // Prepare for next path
                            }
                        }
                    )
                }
        ) {
            drawingPaths.forEach { drawingPath ->
                drawPath(
                    path = drawingPath.path,
                    color = drawingPath.color,
                    style = Stroke(width = drawingPath.strokeWidth)
                )
                // Highlight selected paths
                if (drawingPath.isSelected) {
                    drawPath(
                        path = drawingPath.path,
                        color = Color.Red.copy(alpha = 0.7f), // Highlight color
                        style = Stroke(width = drawingPath.strokeWidth + 4.dp.toPx()) // Slightly thicker
                    )
                }
            }

            if (currentTool == DrawingTool.DRAW) {
                drawPath(path = currentPath, color = currentColor, style = Stroke(width = currentStrokeWidth))
            } else if (currentTool == DrawingTool.ERASE) {
                drawPath(path = currentPath, color = Color.White, style = Stroke(width = currentStrokeWidth * 1.5f))
            }

            selectionRect?.let { rect ->
                // Normalize the rect for consistent drawing regardless of drag direction
                val normalizedRect = Rect(
                    left = minOf(rect.left, rect.right),
                    top = minOf(rect.top, rect.bottom),
                    right = maxOf(rect.left, rect.right),
                    bottom = maxOf(rect.top, rect.bottom)
                )
                drawRect(
                    color = Color.Blue.copy(alpha = 0.3f),
                    topLeft = normalizedRect.topLeft,
                    size = normalizedRect.size,
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }
    }
}


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
            tint = selectedColor // Display the currently selected drawing color
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
                                        .border(
                                            2.dp,
                                            // Highlight the border if this color is the currently selected drawing color
                                            if (selectedColor == color) MaterialTheme.colorScheme.primary else Color.Transparent,
                                            RoundedCornerShape(8.dp)
                                        )
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


@Preview(showBackground = true)
@Composable
fun DrawingScreenPreview2() {
    DrawingScreen()
}