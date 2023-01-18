package com.mshdabiola.drawing

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mshdabiola.drawing.gesture.dragMotionEvent

@Composable
fun Board(
    modifier: Modifier = Modifier,
    drawingController: DrawingController = rememberDrawingController()
) {


    val onPointChange = { offset: Offset, mode: MODE ->
        drawingController.setPathData(offset.x, offset.y, mode)
    }


    val p2 = remember(drawingController.listOfPathData) {
        drawingController.getPathAndData()
    }


    Canvas(
        modifier = modifier
            .dragMotionEvent(
                onDragStart = { onPointChange(it.position, MODE.DOWN) },
                onDrag = { onPointChange(it.position, MODE.MOVE) },
                onDragEnd = { onPointChange(Offset.Zero, MODE.UP) }
            )
    ) {


        //  drawPath(cPath,Color.Black)
        p2.forEach {
            drawPath(
                color = drawingController.colors[it.second.color].copy(alpha = it.second.colorAlpha),
                path = it.first,
                style = Stroke(
                    width = (it.second.lineWidth.dp).roundToPx().toFloat(),
                    cap = drawingController.lineCaps[it.second.lineCap],
                    join = drawingController.lineJoins[it.second.lineJoin]
                ),
                blendMode = DrawScope.DefaultBlendMode
            )
        }


    }


}


@SuppressLint("MutableCollectionMutableState")
@Preview(showBackground = true)
@Composable
fun CanvasPreview() {
    val controller = rememberDrawingController()


    controller.setPathData(mapOf(PathData() to listOf(Offset(0f, 0f), Offset(500f, 500f))))
    controller.color = 1

    Column {
        Board(
            modifier = Modifier.fillMaxSize(),
            drawingController = controller
        )
    }

}

