package com.mshdabiola.drawing // Ensure this matches your package

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.min

// If DrawingTool and DrawingPath are defined in NewDrawingController.kt,
// you might need to import them or ensure they are accessible.
// For this example, let's assume they are now part of the NewDrawingController file or accessible.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawingScreen2(controller: NewDrawingController = remember { NewDrawingController() }) {
    // All state is now held within the controller.
    // We access it via controller.propertyName

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Compose Drawing App") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(
                        enabled = controller.canRedo,
                        onClick = { controller.redo() }) {
                        Icon(Icons.AutoMirrored.Filled.Redo, contentDescription = "Redo")
                    }
                    IconButton(
                        enabled = controller.canUndo,
                        onClick = { controller.undo() }) {
                        Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo")
                    }


                }
            )
        },
        bottomBar = {
            DrawingBar2(
                controller = controller
            )

//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .navigationBarsPadding()
//
//                .background(MaterialTheme.colorScheme.surfaceVariant)
//                    .padding(8.dp),
//                horizontalArrangement = Arrangement.SpaceAround,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                IconToggleButton(
//                    checked = controller.currentTool == DrawingTool.DRAW,
//                    onCheckedChange = { if (it) controller.setDrawingTool(DrawingTool.DRAW) }
//                ) {
//                    Icon(
//                        Icons.Filled.Create,
//                        contentDescription = "Draw Tool",
//                        tint = if (controller.currentTool == DrawingTool.DRAW) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
//                    )
//                }
//                IconToggleButton(
//                    checked = controller.currentTool == DrawingTool.ERASE,
//                    onCheckedChange = { if (it) controller.setDrawingTool(DrawingTool.ERASE) }
//                ) {
//                    Icon(
//                        Icons.Filled.Minimize,
//                        contentDescription = "Erase Tool",
//                        tint = if (controller.currentTool == DrawingTool.ERASE) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
//                    )
//                }
//                IconToggleButton(
//                    checked = controller.currentTool == DrawingTool.SELECT,
//                    onCheckedChange = { if (it) controller.setDrawingTool(DrawingTool.SELECT) }
//                ) {
//                    Icon(
//                        Icons.Filled.SelectAll,
//                        contentDescription = "Selection Tool",
//                        tint = if (controller.currentTool == DrawingTool.SELECT) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
//                    )
//                }
////                ColorPickerButton( // This Composable can remain as is or be adapted
////                    selectedColor = controller.currentColor,
////                    onColorSelected = { color -> controller.currentColor = color }
////                )
//                Slider(
//                    value = controller.currentStrokeWidth,
//                    onValueChange = { controller.currentStrokeWidth = it },
//                    valueRange = 1f..50f,
//                    modifier = Modifier.width(100.dp)
//                )
//                IconButton(onClick = { controller.clearCanvas() }) {
//                    Icon(Icons.Filled.Clear, contentDescription = "Clear Canvas")
//                }
//            }
        }
    ) { paddingValues ->
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .pointerInput(controller.currentTool) { // Re-key on tool if necessary
                    detectDragGestures(
                        onDragStart = { offset -> controller.onDragStart(offset) },
                        onDrag = { change, dragAmount -> controller.onDrag(change, dragAmount) },
                        onDragEnd = { controller.onDragEnd() },
                        onDragCancel = { /* Optional: Handle cancellation */ }
                    )
                }
        ) {
            // Draw existing paths
            controller.drawingPaths.forEach { drawingPath ->
                drawPath(
                    path = drawingPath.path,
                    color = drawingPath.color,
                    style = drawingPath.strokeWidth
                )
            }

            // Draw current drawing/erasing path (the one actively being drawn)
            if (controller.currentTool == DrawingTool.DRAW) {
                drawPath(
                    path = controller.currentPath.path,
                    color = controller.currentPath.color,
                    style = controller.currentPath.strokeWidth
                )
            }

            // Draw the visual selection rectangle during drag (for SELECT tool)
            controller.selectionRect?.let { rect ->
                val normalizedRect = androidx.compose.ui.geometry.Rect( // Explicitly use compose.ui.geometry.Rect
                    left = min(rect.left, rect.right),
                    top = min(rect.top, rect.bottom),
                    right = max(rect.left, rect.right),
                    bottom = max(rect.top, rect.bottom)
                )
                drawRect(
                    color = Color.Blue.copy(alpha = 0.3f),
                    topLeft = normalizedRect.topLeft,
                    size = normalizedRect.size,
                    style = Stroke(width = 1.dp.toPx())
                )
            }

            // Draw the collective bounding box for ALL selected paths
            controller.collectiveSelectedPathsBounds?.let { bounds ->
                drawRect(
                    color = Color.Magenta.copy(alpha = 0.5f),
                    topLeft = bounds.topLeft,
                    size = bounds.size,
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }
    }
}

// ColorPickerButton Composable (can be kept in Board2.kt or moved to a common UI file)
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
                                        .border(
                                            2.dp,
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
    MaterialTheme {
        DrawingScreen2(remember { NewDrawingController() }) // Pass a remembered controller for preview
    }
}