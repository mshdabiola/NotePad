import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration // Added
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.atan2
import kotlin.math.roundToInt

@Composable
fun ResizableRectangleWithHandles2() {
    val density = LocalDensity.current
    val configuration = LocalWindowInfo.current // Added
    val screenWidthDp = configuration.containerSize.width
    val screenHeightDp = configuration.containerSize.height // Added for height constraint if needed

    with(density) {
        val screenWidthPx = screenWidthDp// Added
        val screenHeightPx = screenHeightDp // Added

        var rectangle by remember {
            mutableStateOf(
                Rect(Offset(200f, 200f), Size(200f, 200f)), // Initial position and size
            )
        }
        var rotationAngle by remember { mutableFloatStateOf(0f) }

        val handleSize = 24.dp
        val handleSizePx = handleSize.toPx()
        val minDimensionPx = handleSizePx * 2 // Minimum size: 2 times handle size

        val rotationPivotX = (rectangle.width + handleSizePx) / 2f
        val rotationPivotY = (rectangle.height + handleSizePx.times(2.5f)) / 2f

        Box(Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            rectangle.topLeft.x.roundToInt(),
                            rectangle.topLeft.y.roundToInt(),
                        )
                    }
                    .graphicsLayer(
                        rotationZ = rotationAngle,
                        transformOrigin = TransformOrigin(
                            rotationPivotX / (rectangle.width + handleSizePx),
                            rotationPivotY / (rectangle.height + handleSizePx),
                        ),
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(handleSize)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                val rectCenterX = rectangle.center.x
                                val rectCenterY = rectangle.center.y
                                val prevPos = change.previousPosition - Offset(rectCenterX, rectCenterY)
                                val currentPos = change.position - Offset(rectCenterX, rectCenterY)
                                val prevAngle = atan2(prevPos.y, prevPos.x)
                                val currentAngle = atan2(currentPos.y, currentPos.x)
                                val angleDiff =
                                    Math.toDegrees((currentAngle - prevAngle).toDouble()).toFloat()
                                rotationAngle += angleDiff
                            }
                        }
                        .background(Color.Blue, CircleShape),
                ) {
                    Icon(
                        Icons.Filled.Refresh,
                        contentDescription = "Rotate",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(2.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
                VerticalDivider(
                    modifier = Modifier.height(handleSize / 2),
                    thickness = 4.dp,
                    color = Color.Blue,
                )
                Box(
                    modifier = Modifier
                        .size(
                            (rectangle.width.toDp() + handleSize), // Constrain outer box too
                            (rectangle.height.toDp() + handleSize)// Constrain outer box too
                        )
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                rectangle = rectangle.translate(dragAmount.x, dragAmount.y)
                            }
                        },
                )
                {
                    Box(
                        modifier = Modifier
                            .size(
                                rectangle.width.toDp(),
                                rectangle.height.toDp(),
                            )
                            .align(Alignment.Center)
                            .border(4.dp, Color.Blue),
                    )

                    // Top-Left handle
                    DraggableHandle(
                        modifier = Modifier
                            .size(handleSize)
                            .align(Alignment.TopStart),
                    ) { dragAmount ->
                        val newWidth = (rectangle.width - 2 * dragAmount.x)
                            .coerceIn(minDimensionPx, screenWidthPx - handleSizePx) // Apply constraint
                        val newHeight = (rectangle.height - 2 * dragAmount.y)
                            .coerceIn(minDimensionPx, screenHeightPx - handleSizePx.times(2.5f)) // Apply constraint
                        val newTopLeft = Offset(
                            rectangle.center.x - newWidth / 2,
                            rectangle.center.y - newHeight / 2,
                        )
                        rectangle = Rect(newTopLeft, Size(newWidth, newHeight))
                    }
                    // Top-Center handle
                    DraggableHandle(
                        modifier = Modifier
                            .size(handleSize)
                            .align(Alignment.TopCenter),
                    ) { dragAmount ->
                        val newHeight = (rectangle.height - 2 * dragAmount.y)
                            .coerceIn(minDimensionPx, screenHeightPx - handleSizePx.times(2.5f)) // Apply constraint
                        val newTopLeft = Offset(rectangle.topLeft.x, rectangle.center.y - newHeight / 2)
                        rectangle = Rect(newTopLeft, Size(rectangle.width.coerceIn(minDimensionPx, screenWidthPx - handleSizePx), newHeight))
                    }
                    // Top-End handle
                    DraggableHandle(
                        modifier = Modifier
                            .size(handleSize)
                            .align(Alignment.TopEnd),
                    ) { dragAmount ->
                        val newWidth = (rectangle.width + 2 * dragAmount.x)
                            .coerceIn(minDimensionPx, screenWidthPx - handleSizePx) // Apply constraint
                        val newHeight = (rectangle.height - 2 * dragAmount.y)
                            .coerceIn(minDimensionPx, screenHeightPx - handleSizePx.times(2.5f)) // Apply constraint
                        val newTopLeft = Offset(
                            rectangle.center.x - newWidth / 2,
                            rectangle.center.y - newHeight / 2,
                        )
                        rectangle = Rect(newTopLeft, Size(newWidth, newHeight))
                    }
                    // Center-Start handle
                    DraggableHandle(
                        modifier = Modifier
                            .size(handleSize)
                            .align(Alignment.CenterStart),
                    ) { dragAmount ->
                        val newWidth = (rectangle.width - 2 * dragAmount.x)
                            .coerceIn(minDimensionPx, screenWidthPx - handleSizePx) // Apply constraint
                        val newTopLeft = Offset(rectangle.center.x - newWidth / 2, rectangle.topLeft.y)
                        rectangle = Rect(newTopLeft, Size(newWidth, rectangle.height.coerceIn(minDimensionPx, screenHeightPx - handleSizePx)))
                    }
                    // Center-End handle
                    DraggableHandle(
                        modifier = Modifier
                            .size(handleSize)
                            .align(Alignment.CenterEnd),
                    ) { dragAmount ->
                        val newWidth = (rectangle.width + 2 * dragAmount.x)
                            .coerceIn(minDimensionPx, screenWidthPx - handleSizePx) // Apply constraint
                        val newTopLeft = Offset(rectangle.center.x - newWidth / 2, rectangle.topLeft.y)
                        rectangle = Rect(newTopLeft, Size(newWidth, rectangle.height.coerceIn(minDimensionPx, screenHeightPx - handleSizePx)))
                    }
                    // Bottom-Start handle
                    DraggableHandle(
                        modifier = Modifier
                            .size(handleSize)
                            .align(Alignment.BottomStart),
                    ) { dragAmount ->
                        val newWidth = (rectangle.width - 2 * dragAmount.x)
                            .coerceIn(minDimensionPx, screenWidthPx - handleSizePx) // Apply constraint
                        val newHeight = (rectangle.height + 2 * dragAmount.y)
                            .coerceIn(minDimensionPx, screenHeightPx - handleSizePx.times(2.5f)) // Apply constraint
                        val newTopLeft = Offset(
                            rectangle.center.x - newWidth / 2,
                            rectangle.center.y - newHeight / 2,
                        )
                        rectangle = Rect(newTopLeft, Size(newWidth, newHeight))
                    }
                    // Bottom-Center handle
                    DraggableHandle(
                        modifier = Modifier
                            .size(handleSize)
                            .align(Alignment.BottomCenter),
                    ) { dragAmount ->
                        val newHeight = (rectangle.height + 2 * dragAmount.y)
                            .coerceIn(minDimensionPx, screenHeightPx - handleSizePx.times(2.5f)) // Apply constraint
                        val newTopLeft = Offset(rectangle.topLeft.x, rectangle.center.y - newHeight / 2)
                        rectangle = Rect(newTopLeft, Size(rectangle.width.coerceIn(minDimensionPx, screenWidthPx - handleSizePx), newHeight))
                    }
                    // Bottom-End handle
                    DraggableHandle(
                        modifier = Modifier
                            .size(handleSize)
                            .align(Alignment.BottomEnd),
                    ) { dragAmount ->
                        val newWidth = (rectangle.width + 2 * dragAmount.x)
                            .coerceIn(minDimensionPx, screenWidthPx - handleSizePx) // Apply constraint
                        val newHeight = (rectangle.height + 2 * dragAmount.y)
                            .coerceIn(minDimensionPx, screenHeightPx - handleSizePx.times(2.5f)) // Apply constraint
                        val newTopLeft = Offset(
                            rectangle.center.x - newWidth / 2,
                            rectangle.center.y - newHeight / 2,
                        )
                        rectangle = Rect(newTopLeft, Size(newWidth, newHeight))
                    }
                }
            }
        }
    }
}


@Composable
fun DraggableHandle(
    modifier: Modifier = Modifier,
    onDrag: (dragAmount: Offset) -> Unit, // dragAmount is in pixels
) {
    Box(
        modifier = modifier
            .background(Color.Blue, RoundedCornerShape(2.dp))
            .border(1.dp, Color.White, RoundedCornerShape(2.dp))
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    onDrag(dragAmount) // Pass drag amount in pixels
                }
            },
    ) {
        // No content needed, it's just a visual box for the handle
    }
}

@Preview(showBackground = true)
@Composable
fun ResizableRectangleWithHandlesPreview() {
    MaterialTheme { // MaterialTheme provides LocalDensity implicitly
        ResizableRectangleWithHandles2()
    }
}