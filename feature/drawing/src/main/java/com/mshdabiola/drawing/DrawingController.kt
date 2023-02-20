package com.mshdabiola.drawing

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.RectF
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableMap
typealias ImmutablePath = ImmutableMap<PathData, List<Offset>>

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
    private var lineJoin = 0
    var color = 1
    var draw_mode = DRAW_MODE.PEN
    var colorAlpha = 1f

    private var _unCompletePathData = mutableStateOf(emptyMap<PathData, List<Offset>>().toImmutableMap())
    val unCompletePathData: State<ImmutablePath> = _unCompletePathData

    private var _completePathData = mutableStateOf(emptyMap<PathData, List<Offset>>().toImmutableMap())
    val completePathData: State<ImmutablePath> = _completePathData

    private val redoPaths = HashMap<PathData, List<Offset>>()
    private val _canUndo = mutableStateOf(false)
    val canUndo: State<Boolean> = _canUndo

    private val _canRedo = mutableStateOf(false)
    val canRedo: State<Boolean> = _canRedo

    fun getColor(index: Int) = colors[index]

    private var xx = 0f
    private var yy = 0f
    var pathData = PathData()
    fun setPathData(x: Float, y: Float, mode: MODE) {
        when (draw_mode) {
            DRAW_MODE.ERASE -> {
                when(mode){
                    MODE.DOWN->{
                        xx = x
                        yy = y
                    }
                    MODE.MOVE->{
                        val rect = RectF(minOf(xx, x), minOf(y, yy), maxOf(xx, x), maxOf(y, yy))
                        val paths = _unCompletePathData.value.toMutableMap()
                        val path =
                            paths.filter { entry -> entry.value.any { rect.contains(it.x, it.y) } }
                        if (path.isNotEmpty()){
                            path.forEach { p ->
                                paths.remove(p.key)
                                redoPaths[p.key] = p.value
                            }
                            //rearrange id
                            val newPaths= HashMap<PathData,List<Offset>>()
                           paths
                                .toList()
                                .forEachIndexed { index, pair ->
                                    val newdata=pair.first.copy(id=index)
                                    newPaths[newdata] = pair.second
                                }


                                _unCompletePathData.value=newPaths.toImmutableMap()
                        }

                    }
                    MODE.UP->{
                        setCompleteList()
                    }
                }
//                if (mode == MODE.DOWN) {
//                    xx = x
//                    yy = y
//                }
//                if (mode == MODE.MOVE) {
//                    val rect = RectF(minOf(xx, x), minOf(y, yy), maxOf(xx, x), maxOf(y, yy))
//                    val paths = _listOfPathData.value.toMutableMap()
//                    val path =
//                        paths.filter { entry -> entry.value.any { rect.contains(it.x, it.y) } }
//                    path.forEach { p ->
//                        paths.remove(p.key)
//                        redoPaths[p.key] = p.value
//                    }
//                    _listOfPathData.value=paths.toImmutableMap()
//                }
                //finish move, delete data
            }

            else -> {
                when (mode) {
                    MODE.DOWN -> {
                        val id = _unCompletePathData.value.keys.size
                        pathData = PathData(
                            id = id,
                            color = color,
                            lineWidth = lineWidth,
                            lineCap = lineCap,
                            lineJoin = lineJoin,
                            colorAlpha = colorAlpha,
                        )
                        //  id++
                        val paths2 = _unCompletePathData.value.toMutableMap()
                        val list = emptyList<Offset>().toMutableList()

                        list.add(Offset(x, y))
                        paths2[pathData] = list
                       _unCompletePathData.value= paths2.toImmutableMap()
                    }

                    MODE.MOVE -> {
                        val paths2 = _unCompletePathData.value.toMutableMap()
                        val list = paths2[pathData]!!.toMutableList()

                        list.add(Offset(x, y))
                        paths2[pathData] = list
                       _unCompletePathData.value = paths2.toImmutableMap()
                    }

                    MODE.UP -> {
                        //save data
                        setCompleteList()
                    }
                }
            }
        }
        setDoUnDo()
    }

//    fun setListData(listOfPathDa: ListOfPathData) {
//        _listOfPathData.value = listOfPathDa
//    }

    fun setPathData(pathDatas: Map<PathData, List<Offset>>) {
        val paths = _unCompletePathData.value.toMutableMap()
        paths.putAll(pathDatas)
        //  id = pathDatas.size
        _unCompletePathData.value= paths.toImmutableMap()
        _completePathData.value=paths.toImmutableMap()
    }

    fun undo() {
        if (canUndo.value) {
            val paths = _unCompletePathData.value.toMutableMap()
            val lastKey = paths.keys.last()
            redoPaths[lastKey] = paths.remove(lastKey)!!
            _unCompletePathData.value= paths.toImmutableMap()
            setDoUnDo()
        }
    }

    private fun setDoUnDo() {
        _canUndo.value = _unCompletePathData.value.isNotEmpty()
        _canRedo.value = redoPaths.isNotEmpty()

        //save data
        setCompleteList()
    }

    fun redo() {
        if (canRedo.value) {
            val paths = _unCompletePathData.value.toMutableMap()
            val lastKey = redoPaths.keys.last()
            paths[lastKey] = redoPaths.remove(lastKey)!!
            _unCompletePathData.value= paths.toImmutableMap()

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

        val p = _unCompletePathData
            .value
            .map {
                val yPath = Path()
                it.value.forEachIndexed { index, offset ->
                    prevOff = if (index == 0) {
                        yPath.moveTo(offset.x, offset.y)
                        offset
                    } else {
                        yPath.quadraticBezierTo(
                            prevOff.x,
                            prevOff.y,
                            (prevOff.x + offset.x) / 2,
                            (prevOff.y + offset.y) / 2,
                        )
                        offset
                    }
                }
                Pair(yPath, it.key)
            }
        return p
    }

    fun clearPath() {
        val paths = _unCompletePathData.value.toMutableMap()
        paths.clear()
        redoPaths.clear()
        _unCompletePathData.value = paths.toImmutableMap()
        setDoUnDo()
        //save data
    }

    private fun setCompleteList(){
        _completePathData.value=unCompletePathData.value

    }

    fun getBitMap(width: Int, heigth: Int, density: Float): Bitmap {
        val he = heigth - (50 * density)
        val bitmap2 = Bitmap.createBitmap(width, he.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap2.asImageBitmap())

        val paint = Paint()
        canvas.drawRect(
            Rect(0f, 0f, width.toFloat(), he),
            paint.apply { this.color = Color.White },
        )
        getPathAndData().forEach {
            paint.color = colors[it.second.color]
            paint.alpha = it.second.colorAlpha
            paint.strokeWidth = it.second.lineWidth * density
            // (it.second.lineWidth.dp).roundToPx().toFloat()
            paint.strokeCap = lineCaps[it.second.lineCap]
            paint.strokeJoin = lineJoins[it.second.lineJoin]
            paint.blendMode = DrawScope.DefaultBlendMode
            paint.style = PaintingStyle.Stroke

            canvas.drawPath(it.first, paint)
        }

        return bitmap2
    }
}

@Composable
fun rememberDrawingController(): DrawingController {
    return remember {
        DrawingController()
    }
}
