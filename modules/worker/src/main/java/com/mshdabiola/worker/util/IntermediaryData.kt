package com.mshdabiola.worker.util

import android.graphics.Bitmap
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
import com.mshdabiola.model.Coordinate
import com.mshdabiola.model.DrawPath
import com.mshdabiola.model.PathData
import kotlinx.serialization.Serializable

@Serializable
data class DrawPathPojo(
    val imageId: Long,
    val pathId: Int,
    val color: Int,
    val width: Int,
    val join: Int,
    val alpha: Float,
    val cap: Int,
    val paths: String,
)

fun DrawPath.toDrawPathPojo() =
    DrawPathPojo(imageId, pathId, color, width, join, alpha, cap, paths)

fun DrawPathPojo.toDrawPath() = DrawPath(imageId, pathId, color, width, join, alpha, cap, paths)
fun getBitMap(list: List<Pair<Path, PathData>>, width: Int, heigth: Int, density: Float): Bitmap {
    val he = heigth - (50 * density)
    val bitmap2 = Bitmap.createBitmap(width, he.toInt(), Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap2.asImageBitmap())

    val paint = Paint()
    canvas.drawRect(
        Rect(0f, 0f, width.toFloat(), he),
        paint.apply { this.color = Color.White },
    )
    list.forEach {
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

fun changeToPathAndData(map: Map<PathData, List<Coordinate>>): List<Pair<Path, PathData>> {
    var prevOff = Coordinate.Zero

    val p = map
        .toSortedMap(comparator = compareBy { it.id })
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

