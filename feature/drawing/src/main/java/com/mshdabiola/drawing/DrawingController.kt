package com.mshdabiola.drawing

import android.annotation.SuppressLint
import android.graphics.RectF
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
import kotlin.math.roundToInt

@SuppressLint("MutableCollectionMutableState")
class DrawingController {
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
    val lineCaps = arrayOf(StrokeCap.Round, StrokeCap.Butt, StrokeCap.Round)
    val lineJoins = arrayOf(StrokeJoin.Round, StrokeJoin.Bevel, StrokeJoin.Miter)

    var lineWidth = 8
    var lineCap = 0
    var lineJoin = 0
    var color = 1
    var draw_mode = DRAW_MODE.PEN
    var id = 0
    var colorAlpha = 1f

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

    var xx = 0f
    var yy = 0f
    fun setPathData(x: Float, y: Float, mode: MODE) {
        Log.e("canvas ", "PathData(x = ${x}f, ${y}f,mode=MODE.${mode}),")

        when (draw_mode) {
            DRAW_MODE.ERASE -> {
                if (mode == MODE.DOWN) {
                    xx = x
                    yy = y
                }
                if (mode == MODE.MOVE) {
                    val rect = RectF(minOf(xx, x), minOf(y, yy), maxOf(xx, x), maxOf(y, yy))
                    val paths = listOfPathData.paths.toMutableList()
                    val path = paths.filter { rect.contains(it.x, it.y) }.distinctBy { it.id }
                    path.forEach { p ->
                        paths.removeIf { it.id == p.id }
                    }
                    listOfPathData = listOfPathData.copy(paths = paths.toImmutableList())
                }
            }

            else -> {
                listOfPathData = if (mode == MODE.UP) {
                    val paths = listOfPathData.paths.toMutableList()
                    val last = paths.last()
                    paths.add(last.copy().copy(mode = mode))
                    id++
                    listOfPathData.copy(paths = paths.toImmutableList())

                } else {
                    val paths = listOfPathData.paths.toMutableList()
                    paths.add(
                        PathData(
                            x = x,
                            y = y.roundToInt().toFloat(),
                            mode = mode,
                            color = color,
                            lineWidth = lineWidth,
                            lineCap = lineCap,
                            lineJoin = lineJoin,
                            colorAlpha = colorAlpha,
                            id = id
                        )
                    )
                    listOfPathData.copy(paths = paths.toImmutableList())
                }
            }
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

    fun toggleEraseMode() = run {

        // isEraseMode = !isEraseMode


    }

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


