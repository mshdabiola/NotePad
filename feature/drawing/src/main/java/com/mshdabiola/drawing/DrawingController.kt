package com.mshdabiola.drawing

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import kotlinx.collections.immutable.toImmutableList
import java.util.Stack

@SuppressLint("MutableCollectionMutableState")
class DrawingController {
    val colors = arrayOf(Color.Black, Color.Red, Color.Green, Color.Blue)
    val lineCaps = arrayOf(StrokeCap.Round, StrokeCap.Butt, StrokeCap.Round)
    val lineJoins = arrayOf(StrokeJoin.Round, StrokeJoin.Bevel, StrokeJoin.Miter)

    var lineWidth = 10f
    var lineCap = 0
    var lineJoin = 0
    var color = 0
    var isEraseMode = false

    var listOfPathData by mutableStateOf(ListOfPathData())
    //val drawingPaths = listOfPathData

    private val redoPaths = Stack<PathData>()
    private val _canUndo = mutableStateOf(false)
    val canUndo: State<Boolean> = _canUndo

    private val _canRedo = mutableStateOf(false)
    val canRedo: State<Boolean> = _canRedo


    fun getColor(index: Int) = colors[index]
    fun getCap(index: Int) = lineCaps[index]
    fun getLineJoin(index: Int) = lineJoins[index]

    fun setPathData(x: Float, y: Float, mode: MODE) {
        Log.e("canvas ", "PathData(x = ${x}f, ${y}f,mode=MODE.${mode}),")
        listOfPathData = if (mode == MODE.UP) {
            val paths = listOfPathData.paths.toMutableList()
            val last = paths.last()
            paths.add(last.copy().copy(mode = mode))
            listOfPathData.copy(paths = paths.toImmutableList())
        } else {
            val paths = listOfPathData.paths.toMutableList()
            paths.add(
                PathData(
                    x = x,
                    y = y,
                    mode = mode,
                    color = color,
                    lineWidth = lineWidth,
                    lineCap = lineCap,
                    lineJoin = lineJoin,
                    isErase = isEraseMode
                )
            )
            listOfPathData.copy(paths = paths.toImmutableList())
        }
    }

    fun setPathData(pathDatas: List<PathData>) {
        val paths = listOfPathData.paths.toMutableList()
        paths.addAll(pathDatas)
        listOfPathData = listOfPathData.copy(paths = paths.toImmutableList())
    }

    fun clearRedoPath() {
        redoPaths.clear()
    }

    fun undo() {
        if (canUndo.value) {
            val paths = listOfPathData.paths.toMutableList()
            redoPaths.push(paths.removeLast())
            listOfPathData = listOfPathData.copy(paths = paths.toImmutableList())
            setDoUnDo()
        }
    }

    private fun setDoUnDo() {
        _canRedo.value = listOfPathData.paths.isNotEmpty()
        _canRedo.value = redoPaths.isNotEmpty()
    }

    fun redo() {
        if (canRedo.value) {
            val paths = listOfPathData.paths.toMutableList()
            paths.add(redoPaths.pop())
            listOfPathData = listOfPathData.copy(paths = paths.toImmutableList())
            setDoUnDo()
            // listOfPathData.value.add(redoPaths.removeLast())
        }
    }

    fun toggleEraseMode() = run { isEraseMode = !isEraseMode }

    fun clearPath() {
        val paths = listOfPathData.paths.toMutableList()
        paths.clear()
        redoPaths.clear()
        listOfPathData = listOfPathData.copy(paths = paths.toImmutableList())
        setDoUnDo()
    }

    fun getBitMap(): ImageBitmap {
        val bitmap = ImageBitmap(100, 100, ImageBitmapConfig.Argb8888)
        val canvas = Canvas(bitmap)
        canvas.drawPath(Path(), Paint())

        return bitmap
    }

}

@Composable
fun rememberDrawingController(): DrawingController {
    return remember {
        DrawingController()
    }
}


