// import androidx.compose.runtime.getValue // Redundant if using `by` delegate
// import androidx.compose.runtime.setValue // Redundant if using `by` delegate
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun ResizableRectangleWithHandles2() {
    val density = LocalDensity.current
    with(density) {

        var rectangle by remember {
            mutableStateOf(
                Rect(Offset(100f, 100f), Size(400f, 470f)), // Initial position and size
            )
        }

        val diameter =remember {
            maxOf(rectangle.width, rectangle.height)
        }

        val handleSize = 24.dp

        Box(Modifier.fillMaxSize()) {
            Box(modifier = Modifier
                .size(
                    rectangle.width.toDp() + handleSize,
                    rectangle.height.toDp() + handleSize,
                )
                .offset {
                    IntOffset(
                        rectangle.topLeft.x.roundToInt(),
                        rectangle.topLeft.y.roundToInt()
                    )
                }
                .pointerInput(Unit) { // Pointer input for translating the WHOLE BOX (now rotated)
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
                    val newWidth = (rectangle.width - 2 * dragAmount.x).coerceAtLeast(0f)
                    val newHeight = (rectangle.height - 2 * dragAmount.y).coerceAtLeast(0f)
                    val newTopLeft = Offset(rectangle.center.x - newWidth / 2, rectangle.center.y - newHeight / 2)
                    rectangle = Rect(newTopLeft, Size(newWidth, newHeight))
                }
                // Top-Center handle
                DraggableHandle(
                    modifier = Modifier
                        .size(handleSize)
                        .align(Alignment.TopCenter),
                ) { dragAmount ->
                    val newHeight = (rectangle.height - 2 * dragAmount.y).coerceAtLeast(0f)
                    val newTopLeft = Offset(rectangle.topLeft.x, rectangle.center.y - newHeight / 2)
                    rectangle = Rect(newTopLeft, Size(rectangle.width, newHeight))
                }
                // Top-End handle
                DraggableHandle(
                    modifier = Modifier
                        .size(handleSize)
                        .align(Alignment.TopEnd),
                ) { dragAmount ->
                    val newWidth = (rectangle.width + 2 * dragAmount.x).coerceAtLeast(0f)
                    val newHeight = (rectangle.height - 2 * dragAmount.y).coerceAtLeast(0f)
                    val newTopLeft = Offset(rectangle.center.x - newWidth / 2, rectangle.center.y - newHeight / 2)
                    rectangle = Rect(newTopLeft, Size(newWidth, newHeight))
                }
                // Center-Start handle
                DraggableHandle(
                    modifier = Modifier
                        .size(handleSize)
                        .align(Alignment.CenterStart),
                ) { dragAmount ->
                    val newWidth = (rectangle.width - 2 * dragAmount.x).coerceAtLeast(0f)
                    val newTopLeft = Offset(rectangle.center.x - newWidth / 2, rectangle.topLeft.y)
                    rectangle = Rect(newTopLeft, Size(newWidth, rectangle.height))
                }
                // Center-End handle
                DraggableHandle(
                    modifier = Modifier
                        .size(handleSize)
                        .align(Alignment.CenterEnd),
                ) { dragAmount ->
                    val newWidth = (rectangle.width + 2 * dragAmount.x).coerceAtLeast(0f)
                    val newTopLeft = Offset(rectangle.center.x - newWidth / 2, rectangle.topLeft.y)
                    rectangle = Rect(newTopLeft, Size(newWidth, rectangle.height))
                }
                // Bottom-Start handle
                DraggableHandle(
                    modifier = Modifier
                        .size(handleSize)
                        .align(Alignment.BottomStart),
                ) { dragAmount ->
                    val newWidth = (rectangle.width - 2 * dragAmount.x).coerceAtLeast(0f)
                    val newHeight = (rectangle.height + 2 * dragAmount.y).coerceAtLeast(0f)
                    val newTopLeft = Offset(rectangle.center.x - newWidth / 2, rectangle.center.y - newHeight / 2)
                    rectangle = Rect(newTopLeft, Size(newWidth, newHeight))
                }
                // Bottom-Center handle
                DraggableHandle(
                    modifier = Modifier
                        .size(handleSize)
                        .align(Alignment.BottomCenter),
                ) { dragAmount ->
                    val newHeight = (rectangle.height + 2 * dragAmount.y).coerceAtLeast(0f)
                    val newTopLeft = Offset(rectangle.topLeft.x, rectangle.center.y - newHeight / 2)
                    rectangle = Rect(newTopLeft, Size(rectangle.width, newHeight))
                }
                // Bottom-End handle
                DraggableHandle(
                    modifier = Modifier
                        .size(handleSize)
                        .align(Alignment.BottomEnd),
                ) { dragAmount ->
                    val newWidth = (rectangle.width + 2 * dragAmount.x).coerceAtLeast(0f)
                    val newHeight = (rectangle.height + 2 * dragAmount.y).coerceAtLeast(0f)
                    val newTopLeft = Offset(rectangle.center.x - newWidth / 2, rectangle.center.y - newHeight / 2)
                    rectangle = Rect(newTopLeft, Size(newWidth, newHeight))
                }
            }

            Column(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            rectangle.topLeft.x.roundToInt(),
                            rectangle.topLeft.y.roundToInt()-handleSize.toPx().roundToInt()
                        )
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier

                        .size(handleSize)
                        .background(Color.Blue, CircleShape)

                ) {
                    Icon(
                        Icons.Filled.Refresh,
                        contentDescription = "Rotate",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(2.dp),
                        tint = MaterialTheme.colorScheme.onSecondary,
                    )
                }
                VerticalDivider(
                    modifier = Modifier.height(handleSize/2),
                    thickness = 4.dp,
                    color = Color.Blue)
                Box(
                    modifier = Modifier
                        .size(
                            diameter.toDp(),
                            diameter.toDp(),
                        )
//                        .align(Alignment.Center)
                        .border(4.dp, Color.Blue, CircleShape),
                )
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
