package com.mshdabiola.drawing

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.PointerInputChange
import kotlin.math.max
import kotlin.math.min

enum class DrawingTool {
    DRAW,
    ERASE,
    SELECT
}

data class DrawingPath(
    val path: Path,
    val color: Color,
    val strokeWidth: Float,
    var isSelected: Boolean = false,
)

class NewDrawingController {
    val drawingPaths = mutableStateListOf<DrawingPath>()
    var currentTool by mutableStateOf(DrawingTool.DRAW)
    var currentColor by mutableStateOf(Color.Black)
    var currentStrokeWidth by mutableStateOf(10f)
    var currentPath by mutableStateOf(Path()) // For ongoing drawing/erasing
    var startDragPoint by mutableStateOf(Offset.Unspecified)
    var selectionRect by mutableStateOf<Rect?>(null) // Visual cue for selection drag
    var collectiveSelectedPathsBounds by mutableStateOf<Rect?>(null) // Highlight for all selected

    fun clearCanvas() {
        drawingPaths.clear()
        selectionRect = null
        clearPathSelections() // This also nullifies collectiveSelectedPathsBounds
    }

    fun clearPathSelections() {
        var didDeselect = false
        drawingPaths.forEachIndexed { index, path ->
            if (path.isSelected) {
                drawingPaths[index] = path.copy(isSelected = false)
                didDeselect = true
            }
        }
        if (didDeselect) {
            collectiveSelectedPathsBounds = null
        }
    }

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
                Rect(
                    left = min(current.left, pathBounds.left),
                    top = min(current.top, pathBounds.top),
                    right = max(current.right, pathBounds.right),
                    bottom = max(current.bottom, pathBounds.bottom),
                )
            } ?: pathBounds
        }
        collectiveSelectedPathsBounds = newBounds
    }

    fun onDragStart(offset: Offset) {
        startDragPoint = offset
        if (currentTool == DrawingTool.DRAW || currentTool == DrawingTool.ERASE) {
            currentPath = Path().apply { moveTo(offset.x, offset.y) } // Reset for new line
            if (drawingPaths.any { it.isSelected }) {
                clearPathSelections()
            }
            selectionRect = null
        } else if (currentTool == DrawingTool.SELECT) {
            val clickedOnSelectedArea = collectiveSelectedPathsBounds?.contains(offset) ?: false
            if (!clickedOnSelectedArea) {
                clearPathSelections()
            }
            selectionRect = Rect(offset, offset)
        }
    }

    fun onDrag(change: PointerInputChange, dragAmount: Offset) {
        when (currentTool) {
            DrawingTool.DRAW, DrawingTool.ERASE -> {
                currentPath.lineTo(change.position.x, change.position.y)
            }

            DrawingTool.SELECT -> {
                selectionRect = Rect(startDragPoint, change.position)
            }
        }
        change.consume()
    }

    fun onDragEnd() {
        when (currentTool) {
            DrawingTool.DRAW -> {
                if (!currentPath.isEmpty) {
                    // Create a *new* Path object from the segments of currentPath
                    // and add that to the list.
                    val pathToSave = Path()
                    pathToSave.addPath(currentPath) // Copies the path segments

                    drawingPaths.add(
                        DrawingPath(
                            path = pathToSave, // Add the copied path
                            color = currentColor,
                            strokeWidth = currentStrokeWidth,
                        ),
                    )
                }
            }

            DrawingTool.ERASE -> {
                if (!currentPath.isEmpty) {
                    // Create a *new* Path object from the segments of currentPath
                    val pathToSave = Path()
                    pathToSave.addPath(currentPath) // Copies the path segments

                    drawingPaths.add(
                        DrawingPath(
                            path = pathToSave, // Add the copied path
                            color = Color.White, // Eraser color (background)
                            strokeWidth = currentStrokeWidth,
                        ),
                    )
                }
            }

            DrawingTool.SELECT -> {
                var anySelectedThisDrag = false
                selectionRect?.let { rect ->
                    val normalizedRect = Rect(
                        left = min(rect.left, rect.right),
                        top = min(rect.top, rect.bottom),
                        right = max(rect.left, rect.right),
                        bottom = max(rect.top, rect.bottom),
                    )
                    drawingPaths.forEachIndexed { index, drawingPath ->
                        if (normalizedRect.overlaps(drawingPath.path.getBounds())) {
                            if (!drawingPaths[index].isSelected) {
                                drawingPaths[index] = drawingPath.copy(isSelected = true)
                            }
                            anySelectedThisDrag = true
                        }
                    }
                }
                if (anySelectedThisDrag || drawingPaths.any { it.isSelected }) {
                    updateCollectiveSelectedBounds()
                } else {
                    collectiveSelectedPathsBounds = null
                }
                selectionRect = null // Clear the visual drag selection rectangle
            }
        }
        // Reset current path for DRAW and ERASE tools after adding it or if it was empty
        if (currentTool == DrawingTool.DRAW || currentTool == DrawingTool.ERASE) {
            currentPath = Path() // This creates a new, empty Path for the next drawing operation
        }
    }

    fun setDrawingTool(tool: DrawingTool) {
        currentTool = tool
        if (tool == DrawingTool.DRAW || tool == DrawingTool.ERASE) {
            clearPathSelections()
            selectionRect = null
        }
        // For SELECT tool, we don't immediately clear selections when the tool is chosen,
        // selection clearing happens on drag start outside an existing selection.
    }
}