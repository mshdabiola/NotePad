package com.mshdabiola.searchscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.max

fun flowLayoutMeasurePolicy() = MeasurePolicy { measurables, constraints ->
    layout(constraints.maxWidth, constraints.maxHeight) {
        val placeables = measurables.map { measurable ->
            measurable.measure(constraints)
        }
        var yPos = 0
        var xPos = 0
        var maxY = 0
        placeables.forEach { placeable ->
            if (xPos + placeable.width >
                constraints.maxWidth
            ) {
                xPos = 0
                yPos += maxY
                maxY = 0
            }
            placeable.placeRelative(
                x = xPos,
                y = yPos
            )
            xPos += placeable.width
            if (maxY < placeable.height) {
                maxY = placeable.height
            }
        }
    }
}

@Composable
fun FlowLayout(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val measurePolicy = flowLayoutMeasurePolicy()
    Layout(
        measurePolicy = measurePolicy,
        content = content,
        modifier = modifier
    )
}


@Composable
fun FlowLayout2(
    modifier: Modifier = Modifier,
    rowHorizontalGravity: Alignment.Horizontal = Alignment.Start,
    childVerticalGravity: Alignment.Vertical = Alignment.Top,
    children: @Composable () -> Unit
) {
    class RowInfo(val width: Int, val height: Int, val nextChildIndex: Int)

    Layout(
        content = children,
        modifier = modifier
    ) { measurables, constraints ->
        var contentWidth = 0
        var contentHeight = 0
        var rowWidth = 0
        var rowHeight = 0
        val rows = mutableListOf<RowInfo>()
        val maxWidth = constraints.maxWidth
        val childConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        val placeables = measurables.mapIndexed { index, measurable ->
            measurable.measure(childConstraints).also { placeable ->
                val newRowWidth = rowWidth + placeable.width
                if (newRowWidth > maxWidth) {
                    rows.add(RowInfo(width = rowWidth, height = rowHeight, nextChildIndex = index))
                    contentWidth = maxOf(contentWidth, rowWidth)
                    contentHeight += rowHeight
                    rowWidth = placeable.width
                    rowHeight = placeable.height
                } else {
                    rowWidth = newRowWidth
                    rowHeight = maxOf(rowHeight, placeable.height)
                }
            }
        }
        rows.add(RowInfo(width = rowWidth, height = rowHeight, nextChildIndex = measurables.size))
        contentWidth = maxOf(contentWidth, rowWidth)
        contentHeight += rowHeight

        layout(
            width = maxOf(contentWidth, constraints.minWidth),
            height = maxOf(contentHeight, constraints.minHeight)
        ) {
            var childIndex = 0
            var y = 0
            rows.forEach { rowInfo ->
                var x = rowHorizontalGravity.align(
                    constraints.maxWidth - rowInfo.width,
                    3,
                    LayoutDirection.Ltr
                )
                val rowHeight = rowInfo.height
                val nextChildIndex = rowInfo.nextChildIndex
                while (childIndex < nextChildIndex) {
                    val placeable = placeables[childIndex]
                    placeable.placeRelative(
                        x = x,
                        y = y + childVerticalGravity.align(rowHeight - placeable.height, 3)
                    )
                    x += placeable.width
                    childIndex++
                }

                y += rowHeight
            }
        }
    }
}

@Composable
fun FlowLayout(
    modifier: Modifier = Modifier,
    verticalSpacing: Dp = 0.dp,
    horizontalSpacing: Dp = 0.dp,
    content: @Composable () -> Unit
) {
    Layout(modifier = modifier, content = content) { measurables, constraints ->
        val placeables = measurables.map { measurable ->
            measurable.measure(constraints)
        }

        data class FlowContent(
            val placeable: Placeable,
            val x: Int,
            val y: Int
        )

        var y = 0
        var x = 0
        var rowMaxY = 0
        val flowContents = mutableListOf<FlowContent>()

        val verticalSpacingPx = verticalSpacing.roundToPx()
        val horizontalSpacingPx = horizontalSpacing.roundToPx()

        placeables.forEach { placeable ->
            if (placeable.width + x > constraints.maxWidth) {
                x = 0
                y += rowMaxY
                rowMaxY = 0
            }
            rowMaxY = max(placeable.height + verticalSpacingPx, rowMaxY)

            flowContents.add(FlowContent(placeable, x, y))
            x += placeable.width + horizontalSpacingPx
        }
        y += rowMaxY

        layout(constraints.maxWidth, y) {
            flowContents.forEach {
                it.placeable.place(it.x, it.y)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewFlowRow() {
    MaterialTheme {
        Surface {
            FlowLayout(
                modifier = Modifier.padding(8.dp),
                verticalSpacing = 8.dp,
                horizontalSpacing = 8.dp,
            ) {
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(40.dp)
                        .background(color = Color.Blue)
                )

                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(40.dp)
                        .background(color = Color.Red)
                )

                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(40.dp)
                        .background(color = Color.Blue)
                )

                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(40.dp)
                        .background(color = Color.Red)
                )

                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(40.dp)
                        .background(color = Color.Blue)
                )
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(40.dp)
                        .background(color = Color.Red)
                )
            }
        }
    }
}