package com.mshdabiola.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerInputChange
import com.mshdabiola.model.Coordinate
import com.mshdabiola.model.DrawingPath
import com.mshdabiola.model.DrawingProperties
import kotlin.math.max
import kotlin.math.min

val colors = arrayOf(
    Color.Black,
    Color.Red,
    Color.Green,
    Color.Blue,
    Color.Magenta,
    Color.Cyan,
    Color.Yellow,
    Color(0xFF651FFF),
    Color(0xFFD500F9),
    Color(0xFFFFEA00),
    Color(0xFF1DE9B6),
    Color(0xFFF50057),
    Color(0xFFFF3D00),

)

val lineCaps = arrayOf(StrokeCap.Round, StrokeCap.Butt, StrokeCap.Square)
val lineJoins = arrayOf(StrokeJoin.Round, StrokeJoin.Bevel, StrokeJoin.Miter)

enum class DrawingTool {
    DRAW,
    ERASE,
    SELECT,
}

// Todo( add mutableList,new undo, rotate,scale)
val DrawingPath.paths
    get() = this.coordinates.map { Offset(it.x, it.y) }
val DrawingPath.path
    get() = Path().apply {
        if (paths.isNotEmpty()) {
            moveTo(paths.first().x, paths.first().y)
            paths.drop(1).forEach { offset ->
                lineTo(offset.x, offset.y)
            }
        }
    }

val DrawingPath.color
    get() = colors[drawingProperties.colorIndex].copy(alpha = drawingProperties.colorAlphaIndex)

val DrawingPath.strokeWidth
    get() = Stroke(
        width = drawingProperties.lineWidth.toFloat(),
        cap = lineCaps[drawingProperties.lineCapIndex],
        join = lineJoins[drawingProperties.lineJoinIndex],
    )

open class DrawingController {
    val drawingPaths = mutableStateListOf<DrawingPath>()

    // private set
    var canUndo by mutableStateOf(drawingPaths.isNotEmpty())
    var redo = mutableStateListOf<DrawingPath>()
        private set
    var canRedo by mutableStateOf(redo.isNotEmpty())

    var currentTool by mutableStateOf(DrawingTool.DRAW)
    var currentDrawingProperties by mutableStateOf(DrawingProperties())
    var currentPath by mutableStateOf(
        DrawingPath(
            //            id = 0,
        ),
    ) // For ongoing drawing/erasing
    var startDragPoint by mutableStateOf(Offset.Unspecified)
    var selectionRect by mutableStateOf<Rect?>(null) // Visual cue for selection drag
    var collectiveSelectedPathsBounds by mutableStateOf<Rect?>(null) // Highlight for all selected

    open fun redo() {
        if (canRedo) {
            val lastIndex = redo.lastIndex

            drawingPaths.add(redo.removeAt(lastIndex))
        }
        setRedoUndo()
    }

    open fun undo() {
        if (canUndo) {
            val lastIndex = drawingPaths.lastIndex
            redo.add(drawingPaths.removeAt(lastIndex))
        }
        setRedoUndo()
    }

    fun clearCanvas() {
        clearPathSelections()

        redo.clear()
        redo.addAll(drawingPaths)
        drawingPaths.clear()
        selectionRect = null
    }

    private fun setRedoUndo() {
        canUndo = drawingPaths.isNotEmpty()
        canRedo = redo.isNotEmpty()
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
            currentPath = DrawingPath(
                drawingProperties = currentDrawingProperties,
            )
            // Path().apply { moveTo(offset.x, offset.y) } // Reset for new line
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
            DrawingTool.DRAW -> {
                val newPath = currentPath.paths + change.position
                currentPath = currentPath.copy(coordinates = newPath.map { Coordinate(it.x, it.y) })
            }

            DrawingTool.ERASE -> {
                val end = change.position

                val rect2 = Rect(
                    minOf(startDragPoint.x, end.x),
                    minOf(end.y, startDragPoint.y),
                    maxOf(startDragPoint.x, end.x),
                    maxOf(end.y, startDragPoint.y),
                )

                val index = drawingPaths.indexOfFirst { it.paths.any { rect2.contains(it) } }
                if (index != -1) {
                    redo.add(drawingPaths.removeAt(index))
                }
            }

            DrawingTool.SELECT -> {
                selectionRect = Rect(startDragPoint, change.position)
            }
        }

        setRedoUndo()
        change.consume()
    }

    fun onDragEnd() {
        when (currentTool) {
            DrawingTool.DRAW -> {
                if (currentPath.paths.isNotEmpty()) {
                    // Create a *new* Path object from the segments of currentPath
                    // and add that to the list.

                    drawingPaths.add(currentPath)
                    currentPath = DrawingPath(
                        drawingProperties = currentDrawingProperties,
                    )
                }
            }

            DrawingTool.ERASE -> {
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
        setRedoUndo()
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
