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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.atan2
import kotlin.math.roundToInt

@Composable
fun ResizableRectangleWithHandles2() {
    val density = LocalDensity.current
    with(density) {

        var rectangle by remember {
            mutableStateOf(
                Rect(Offset(200f, 200f), Size(200f, 200f)), // Initial position and size
            )
        }
        var rotationAngle by remember { mutableFloatStateOf(0f) } // Added for rotation

        val handleSize = 24.dp
        val handleSizePx = handleSize.toPx()

        // Calculate the center of the combined Box (rectangle + handles) for rotation pivot
        // This needs to be relative to the Box that will be rotated.
        // Since the Box's top-left is at (0,0) before offset, the pivot is its center.
        val rotationPivotX = (rectangle.width + handleSizePx) / 2f
        val rotationPivotY = (rectangle.height + handleSizePx.times(2.5f)) / 2f

        Box(Modifier.fillMaxSize()) {
            // Rotation Handle Column
            Column(
                modifier = Modifier
                    .offset {
                        // The offset is still based on the rectangle's top-left,
                        // but the rotation happens around its own center.
                        IntOffset(
                            rectangle.topLeft.x.roundToInt(),
                            rectangle.topLeft.y.roundToInt(),
                        )
                    }
                    .graphicsLayer(
                        // Apply rotation here
                        rotationZ = rotationAngle,
                        transformOrigin = TransformOrigin(
                            rotationPivotX / (rectangle.width + handleSizePx),
                            rotationPivotY / (rectangle.height + handleSizePx),
                        ), // Pivot around the center of this Box
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(handleSize)

                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()

                                // Calculate the center of the rectangle in screen coordinates
                                val rectCenterX = rectangle.center.x
                                val rectCenterY = rectangle.center.y

                                // Previous position of the drag pointer relative to the rectangle center
                                val prevPos = change.previousPosition - Offset(rectCenterX, rectCenterY)
                                // Current position of the drag pointer relative to the rectangle center
                                val currentPos = change.position - Offset(rectCenterX, rectCenterY)

                                // Calculate angles
                                val prevAngle = atan2(prevPos.y, prevPos.x)
                                val currentAngle = atan2(currentPos.y, currentPos.x)

                                // Calculate angle difference and convert to degrees
                                val angleDiff =
                                    Math.toDegrees((currentAngle - prevAngle).toDouble()).toFloat()

                                rotationAngle += angleDiff
                            }
                        }
                        .background(Color.Blue, CircleShape), // Changed color for distinction
                ) {
                    Icon(
                        Icons.Filled.Refresh,
                        contentDescription = "Rotate",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(2.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer, // Ensure contrast
                    )
                }
                VerticalDivider(
                    modifier = Modifier.height(handleSize / 2),
                    thickness = 4.dp,
                    color = Color.Blue, // Changed color for distinction
                )
            Box(
                modifier = Modifier

                    .size(
                        // Size of the rotatable box (rectangle + handles)
                        rectangle.width.toDp() + handleSize,
                        rectangle.height.toDp() + handleSize,
                    )
                    .pointerInput(Unit) { // Pointer input for translating the WHOLE BOX
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            // Translate the underlying rectangle data
                            rectangle = rectangle.translate(dragAmount.x, dragAmount.y)
                        }
                    },
            )
            {
                // Inner Box for the visual rectangle (content)
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
                    // Adjust dragAmount based on rotation if handles are outside rotated box
                    // For simplicity, this example keeps handles inside the rotated box.
                    // If handles were outside, you'd need to transform dragAmount by the inverse rotation.
                    val newWidth = (rectangle.width - 2 * dragAmount.x).coerceAtLeast(handleSizePx)
                    val newHeight =
                        (rectangle.height - 2 * dragAmount.y).coerceAtLeast(handleSizePx)
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
                    val newHeight =
                        (rectangle.height - 2 * dragAmount.y).coerceAtLeast(handleSizePx)
                    val newTopLeft = Offset(rectangle.topLeft.x, rectangle.center.y - newHeight / 2)
                    rectangle = Rect(newTopLeft, Size(rectangle.width, newHeight))
                }
                // Top-End handle
                DraggableHandle(
                    modifier = Modifier
                        .size(handleSize)
                        .align(Alignment.TopEnd),
                ) { dragAmount ->
                    val newWidth = (rectangle.width + 2 * dragAmount.x).coerceAtLeast(handleSizePx)
                    val newHeight =
                        (rectangle.height - 2 * dragAmount.y).coerceAtLeast(handleSizePx)
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
                    val newWidth = (rectangle.width - 2 * dragAmount.x).coerceAtLeast(handleSizePx)
                    val newTopLeft = Offset(rectangle.center.x - newWidth / 2, rectangle.topLeft.y)
                    rectangle = Rect(newTopLeft, Size(newWidth, rectangle.height))
                }
                // Center-End handle
                DraggableHandle(
                    modifier = Modifier
                        .size(handleSize)
                        .align(Alignment.CenterEnd),
                ) { dragAmount ->
                    val newWidth = (rectangle.width + 2 * dragAmount.x).coerceAtLeast(handleSizePx)
                    val newTopLeft = Offset(rectangle.center.x - newWidth / 2, rectangle.topLeft.y)
                    rectangle = Rect(newTopLeft, Size(newWidth, rectangle.height))
                }
                // Bottom-Start handle
                DraggableHandle(
                    modifier = Modifier
                        .size(handleSize)
                        .align(Alignment.BottomStart),
                ) { dragAmount ->
                    val newWidth = (rectangle.width - 2 * dragAmount.x).coerceAtLeast(handleSizePx)
                    val newHeight =
                        (rectangle.height + 2 * dragAmount.y).coerceAtLeast(handleSizePx)
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
                    val newHeight =
                        (rectangle.height + 2 * dragAmount.y).coerceAtLeast(handleSizePx)
                    val newTopLeft = Offset(rectangle.topLeft.x, rectangle.center.y - newHeight / 2)
                    rectangle = Rect(newTopLeft, Size(rectangle.width, newHeight))
                }
                // Bottom-End handle
                DraggableHandle(
                    modifier = Modifier
                        .size(handleSize)
                        .align(Alignment.BottomEnd),
                ) { dragAmount ->
                    val newWidth = (rectangle.width + 2 * dragAmount.x).coerceAtLeast(handleSizePx)
                    val newHeight =
                        (rectangle.height + 2 * dragAmount.y).coerceAtLeast(handleSizePx)
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
