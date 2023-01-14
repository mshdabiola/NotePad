package com.mshdabiola.drawing

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
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
        var prevOff = Offset.Zero
        drawingController
            .listOfPathData
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


    val ondata = listOf(
        PathData(x = 135.81137f, 654.2855f, mode = MODE.DOWN),
        PathData(x = 135.81137f, 635.52765f, mode = MODE.MOVE),
        PathData(x = 135.81137f, 619.0008f, mode = MODE.MOVE),
        PathData(x = 136.8518f, 604.11786f, mode = MODE.MOVE),
        PathData(x = 137.52032f, 591.8121f, mode = MODE.MOVE),
        PathData(x = 137.80861f, 578.4904f, mode = MODE.MOVE),
        PathData(x = 114.67702f, 546.87665f, mode = MODE.MOVE),
        PathData(x = 113.48426f, 527.53174f, mode = MODE.MOVE),
        PathData(x = 120.42735f, 509.17053f, mode = MODE.MOVE),
        PathData(x = 128.15266f, 494.9035f, mode = MODE.MOVE),
        PathData(x = 133.32599f, 483.2832f, mode = MODE.MOVE),
        PathData(x = 138.08542f, 473.0244f, mode = MODE.MOVE),
        PathData(x = 146.2003f, 459.88135f, mode = MODE.MOVE),
        PathData(x = 154.86403f, 445.43823f, mode = MODE.MOVE),
        PathData(x = 163.00159f, 430.5957f, mode = MODE.MOVE),
        PathData(x = 178.07172f, 408.58167f, mode = MODE.MOVE),
        PathData(x = 183.90186f, 399.88983f, mode = MODE.MOVE),
        PathData(x = 187.64728f, 392.81445f, mode = MODE.MOVE),
        PathData(x = 187.73926f, 386.0727f, mode = MODE.MOVE),
        PathData(x = 188.73787f, 378.8423f, mode = MODE.MOVE),
        PathData(x = 189.73648f, 374.1931f, mode = MODE.MOVE),
        PathData(x = 194.37373f, 365.68213f, mode = MODE.MOVE),
        PathData(x = 203.88248f, 356.48785f, mode = MODE.MOVE),
        PathData(x = 215.25162f, 348.6297f, mode = MODE.MOVE),
        PathData(x = 226.94618f, 343.12152f, mode = MODE.MOVE),
        PathData(x = 238.0866f, 338.81055f, mode = MODE.MOVE),
        PathData(x = 250.83029f, 336.14423f, mode = MODE.MOVE),
        PathData(x = 264.24097f, 334.46667f, mode = MODE.MOVE),
        PathData(x = 276.74277f, 332.79346f, mode = MODE.MOVE),
        PathData(x = 287.96057f, 333.2135f, mode = MODE.MOVE),
        PathData(x = 300.1178f, 334.88666f, mode = MODE.MOVE),
        PathData(x = 309.93646f, 336.5608f, mode = MODE.MOVE),
        PathData(x = 319.9272f, 340.3496f, mode = MODE.MOVE),
        PathData(x = 329.99356f, 347.86902f, mode = MODE.MOVE),
        PathData(x = 338.1177f, 357.07538f, mode = MODE.MOVE),
        PathData(x = 344.84528f, 365.13428f, mode = MODE.MOVE),
        PathData(x = 351.5304f, 373.91565f, mode = MODE.MOVE),
        PathData(x = 360.13245f, 383.26892f, mode = MODE.MOVE),
        PathData(x = 368.4804f, 391.62317f, mode = MODE.MOVE),
        PathData(x = 378.11496f, 401.2649f, mode = MODE.MOVE),
        PathData(x = 392.9939f, 413.208f, mode = MODE.MOVE),
        PathData(x = 403.73904f, 422.29688f, mode = MODE.MOVE),
        PathData(x = 414.2154f, 433.69318f, mode = MODE.MOVE),
        PathData(x = 426.19647f, 450.3213f, mode = MODE.MOVE),
        PathData(x = 434.60648f, 467.00287f, mode = MODE.MOVE),
        PathData(x = 442.95245f, 482.39465f, mode = MODE.MOVE),
        PathData(x = 449.32605f, 495.46466f, mode = MODE.MOVE),
        PathData(x = 456.66687f, 507.83826f, mode = MODE.MOVE),
        PathData(x = 462.36942f, 519.90924f, mode = MODE.MOVE),
        PathData(x = 470.40286f, 531.61993f, mode = MODE.MOVE),
        PathData(x = 477.72037f, 547.2989f, mode = MODE.MOVE),
        PathData(x = 482.0336f, 564.0359f, mode = MODE.MOVE),
        PathData(x = 486.38794f, 580.8271f, mode = MODE.MOVE),
        PathData(x = 489.73038f, 595.14276f, mode = MODE.MOVE),
        PathData(x = 491.7005f, 608.559f, mode = MODE.MOVE),
        PathData(x = 493.37073f, 622.98627f, mode = MODE.MOVE),
        PathData(x = 495.03342f, 636.9622f, mode = MODE.MOVE),
        PathData(x = 496.71597f, 656.3314f, mode = MODE.MOVE),
        PathData(x = 496.3107f, 676.38556f, mode = MODE.MOVE),
        PathData(x = 495.56143f, 692.9408f, mode = MODE.MOVE),
        PathData(x = 491.05557f, 709.63513f, mode = MODE.MOVE),
        PathData(x = 484.96674f, 721.0493f, mode = MODE.MOVE),
        PathData(x = 478.26694f, 730.24994f, mode = MODE.MOVE),
        PathData(x = 471.58463f, 738.6091f, mode = MODE.MOVE),
        PathData(x = 466.02386f, 743.7342f, mode = MODE.MOVE),
        PathData(x = 462.7965f, 746.74445f, mode = MODE.MOVE),
        PathData(x = 459.46252f, 750.3005f, mode = MODE.MOVE),
        PathData(x = 457.10385f, 754.65955f, mode = MODE.MOVE),
        PathData(x = 456.5646f, 757.9988f, mode = MODE.MOVE),
        PathData(x = 454.41342f, 762.82623f, mode = MODE.MOVE),
        PathData(x = 448.7983f, 770.1114f, mode = MODE.MOVE),
        PathData(x = 446.74045f, 778.3004f, mode = MODE.MOVE),
        PathData(x = 445.88715f, 787.8321f, mode = MODE.MOVE),
        PathData(x = 444.21304f, 798.3903f, mode = MODE.MOVE),
        PathData(x = 445.2285f, 810.2904f, mode = MODE.MOVE),
        PathData(x = 446.41782f, 820.4696f, mode = MODE.MOVE),
        PathData(x = 447.37866f, 830.50244f, mode = MODE.MOVE),
        PathData(x = 448.37726f, 838.6692f, mode = MODE.MOVE),
        PathData(x = 448.83698f, 849.036f, mode = MODE.MOVE),
        PathData(x = 448.37726f, 861.02014f, mode = MODE.MOVE),
        PathData(x = 450.1382f, 875.0199f, mode = MODE.MOVE),
        PathData(x = 447.8225f, 893.42017f, mode = MODE.MOVE),
        PathData(x = 443.69345f, 910.5995f, mode = MODE.MOVE),
        PathData(x = 439.68604f, 924.49817f, mode = MODE.MOVE),
        PathData(x = 434.67932f, 937.71484f, mode = MODE.MOVE),
        PathData(x = 429.64743f, 949.03723f, mode = MODE.MOVE),
        PathData(x = 419.86517f, 963.5918f, mode = MODE.MOVE),
        PathData(x = 409.28644f, 977.58484f, mode = MODE.MOVE),
        PathData(x = 397.60425f, 991.3535f, mode = MODE.MOVE),
        PathData(x = 383.0021f, 1007.702f, mode = MODE.MOVE),
        PathData(x = 364.53674f, 1023.5708f, mode = MODE.MOVE),
        PathData(x = 347.85626f, 1035.9917f, mode = MODE.MOVE),
        PathData(x = 330.8265f, 1046.0399f, mode = MODE.MOVE),
        PathData(x = 320.55478f, 1051.2081f, mode = MODE.MOVE),
        PathData(x = 320.55478f, 1051.2081f, mode = MODE.UP)
    )

    controller.setPathData(ondata)
    controller.color = 1

    Column {
        Board(
            modifier = Modifier.fillMaxSize()
        )
    }

}

