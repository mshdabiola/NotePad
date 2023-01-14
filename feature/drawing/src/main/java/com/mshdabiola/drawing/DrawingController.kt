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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.toImmutableMap
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
    lateinit var density: Density
    var width = 0.dp
    var heigth = 0.dp


    var listOfPathData by mutableStateOf(ListOfPathData())
    //val drawingPaths = listOfPathData

    private val redoPaths = HashMap<PathData, List<Offset>>()
    private val _canUndo = mutableStateOf(false)
    val canUndo: State<Boolean> = _canUndo

    private val _canRedo = mutableStateOf(false)
    val canRedo: State<Boolean> = _canRedo


    fun getColor(index: Int) = colors[index]
    fun getCap(index: Int) = lineCaps[index]
    fun getLineJoin(index: Int) = lineJoins[index]

    var xx = 0f
    var yy = 0f
    var pathData = PathData()
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
                    val paths = listOfPathData.paths2.toMutableMap()
                    val path =
                        paths.filter { entry -> entry.value.any { rect.contains(it.x, it.y) } }
                    path.forEach { p ->
                        paths.remove(p.key)
                        redoPaths[p.key] = p.value
                    }
                    listOfPathData = listOfPathData.copy(paths2 = paths.toImmutableMap())
                }
            }

            else -> {
                when (mode) {
                    MODE.DOWN -> {
                        pathData = PathData(
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
                        val paths2 = listOfPathData.paths2.toMutableMap()
                        val list = emptyList<Offset>().toMutableList()

                        list.add(Offset(x, y))
                        paths2[pathData] = list
                        listOfPathData = listOfPathData.copy(paths2 = paths2.toImmutableMap())

                    }

                    MODE.MOVE -> {
                        val paths2 = listOfPathData.paths2.toMutableMap()
                        val list = paths2[pathData]!!.toMutableList()

                        list.add(Offset(x, y))
                        paths2[pathData] = list
                        listOfPathData = listOfPathData.copy(paths2 = paths2.toImmutableMap())
                    }

                    MODE.UP -> {

                    }
                }


            }
        }
        setDoUnDo()
    }

    fun setPathData(pathDatas: Map<PathData, List<Offset>>) {
        val paths = listOfPathData.paths2.toMutableMap()
        paths.putAll(pathDatas)
        listOfPathData = listOfPathData.copy(paths2 = paths.toImmutableMap())
    }

    fun clearRedoPath() {
        redoPaths.clear()
    }

    fun undo() {
        if (canUndo.value) {
            val paths = listOfPathData.paths2.toMutableMap()
            val lastKey = paths.keys.last()
            redoPaths[lastKey] = paths.remove(lastKey)!!
            listOfPathData = listOfPathData.copy(paths2 = paths.toImmutableMap())
            setDoUnDo()
        }
    }

    private fun setDoUnDo() {
        _canUndo.value = listOfPathData.paths2.isNotEmpty()
        _canRedo.value = redoPaths.isNotEmpty()
    }

    fun redo() {
        if (canRedo.value) {

            val paths = listOfPathData.paths2.toMutableMap()
            val lastKey = redoPaths.keys.last()
            paths[lastKey] = redoPaths.remove(lastKey)!!
            listOfPathData = listOfPathData.copy(paths2 = paths.toImmutableMap())

            setDoUnDo()
            // listOfPathData.value.add(redoPaths.removeLast())
        }
    }

//    fun toggleEraseMode() = run {
//
//        // isEraseMode = !isEraseMode
//
//
//    }

    fun getPathAndData(): List<Pair<Path, PathData>> {
        var prevOff = Offset.Zero

        val p = listOfPathData
            .paths2
            .map {
                val yPath = Path()
                it.value.forEachIndexed { index, offset ->
                    prevOff = if (index == 0) {
                        yPath.moveTo(offset.x, offset.y)
                        offset
                    } else {
                        yPath.quadraticBezierTo(
                            prevOff.x, prevOff.y,
                            (prevOff.x + offset.x) / 2,
                            (prevOff.y + offset.y) / 2
                        )
                        offset
                    }

                }
                Pair(yPath, it.key)
            }
        return p
    }

    fun clearPath() {
        val paths = listOfPathData.paths2.toMutableMap()
        paths.clear()
        redoPaths.clear()
        listOfPathData = listOfPathData.copy(paths2 = paths.toImmutableMap())
        setDoUnDo()
    }

    fun getBitMap(): ImageBitmap {
        val h = with(density) { heigth.roundToPx() }
        val w = with(density) { width.roundToPx() }
        val bitmap = ImageBitmap(w, h, ImageBitmapConfig.Argb8888)
        val canvas = Canvas(bitmap)

        canvas.drawPath(Path(), Paint())
        getPathAndData().forEach {
            val paint = Paint()
            paint.color = colors[it.second.color]
            paint.alpha = it.second.colorAlpha
            paint.strokeWidth = with(density) {
                (it.second.lineWidth.dp).roundToPx().toFloat()
            }//(it.second.lineWidth.dp).roundToPx().toFloat()
            paint.strokeCap = lineCaps[it.second.lineCap]
            paint.strokeJoin = lineJoins[it.second.lineJoin]
            canvas.drawPath(it.first, paint)

        }

        return bitmap
    }

}

@Composable
fun rememberDrawingController(): DrawingController {
    var density = LocalDensity.current
    val configuration = LocalConfiguration.current
    return remember {
        DrawingController().apply {
            this.density = density
            width = configuration.screenHeightDp.dp
            heigth = configuration.screenHeightDp.dp
        }
    }
}


