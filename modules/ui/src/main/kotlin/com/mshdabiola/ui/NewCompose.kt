// import androidx.compose.runtime.getValue // Redundant if using `by` delegate
// import androidx.compose.runtime.setValue // Redundant if using `by` delegate
import android.util.Log
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toIntSize
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.sin

enum class HandlePosition {
    TopLeft, TopCenter, TopRight, CenterLeft, CenterRight, BottomLeft, BottomCenter, BottomRight
}

@Composable
fun ResizableRectangleWithHandles2() {
    val density = LocalDensity.current
    with(density) {

        var rectangle by remember {
            mutableStateOf(
                Rect(Offset(20f, 20f), Size(400f, 470f)),
            )
        }
        var rotationAngle by remember { mutableStateOf(0f) }
        val handleSize = 24.dp
        val minSize = 50f // Minimum size for the rectangle

        Box(Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .offset(rectangle.topLeft.x.toDp(), rectangle.topLeft.y.toDp())
                    .graphicsLayer {
                        rotationZ = rotationAngle
                    }
            ) {
                Box(
                    modifier = Modifier
                        .size(handleSize)
                        .align(Alignment.CenterHorizontally)
                        .background(Color.Blue, CircleShape)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                val rotationSensitivity = 0.5f
                                rotationAngle += dragAmount.x * rotationSensitivity
                            }
                        }

                ) {
                    Icon(
                        Icons.Filled.Refresh,
                        contentDescription = "Rotate handler",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(2.dp),
                        tint = MaterialTheme.colorScheme.onSecondary,
                    )
                }
                HorizontalDivider(
                    modifier = Modifier
                        .height(handleSize / 2)
                        .width(2.dp),
                )
                Box(
                    modifier = Modifier
                        .size(
                            rectangle.width.toDp() + handleSize,
                            rectangle.height.toDp() + handleSize,
                        )
                        .pointerInput(Unit) { // Pointer input for translating the WHOLE BOX
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                rectangle = rectangle.translate(dragAmount.x, dragAmount.y)
                            }
                        },
                ) {
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
                        val newLeft = rectangle.left + dragAmount.x
                        val newTop = rectangle.top + dragAmount.y
                        val newWidth = max(minSize, rectangle.width - dragAmount.x)
                        val newHeight = max(minSize, rectangle.height - dragAmount.y)
                        rectangle = Rect(
                            offset = Offset(newLeft, newTop),
                            size = Size(newWidth, newHeight)
                        )
                    }
                    // Top-Center handle
                    DraggableHandle(
                        modifier = Modifier
                            .size(handleSize)
                            .align(Alignment.TopCenter),
                    ) { dragAmount ->
                        val newTop = rectangle.top + dragAmount.y
                        val newHeight = max(minSize, rectangle.height - dragAmount.y)
                        rectangle = Rect(
                            offset = Offset(rectangle.left, newTop),
                            size = Size(rectangle.width, newHeight)
                        )
                    }
                    // Top-End handle
                    DraggableHandle(
                        modifier = Modifier
                            .size(handleSize)
                            .align(Alignment.TopEnd),
                    ) { dragAmount ->
                        val newTop = rectangle.top + dragAmount.y
                        val newWidth = max(minSize, rectangle.width + dragAmount.x)
                        val newHeight = max(minSize, rectangle.height - dragAmount.y)
                        rectangle = Rect(
                            offset = Offset(rectangle.left, newTop),
                            size = Size(newWidth, newHeight)
                        )
                    }
                    // Center-Start handle
                    DraggableHandle(
                        modifier = Modifier
                            .size(handleSize)
                            .align(Alignment.CenterStart),
                    ) { dragAmount ->
                        val newLeft = rectangle.left + dragAmount.x
                        val newWidth = max(minSize, rectangle.width - dragAmount.x)
                        rectangle = Rect(
                            offset = Offset(newLeft, rectangle.top),
                            size = Size(newWidth, rectangle.height)
                        )
                    }
                    // Center-End handle
                    DraggableHandle(
                        modifier = Modifier
                            .size(handleSize)
                            .align(Alignment.CenterEnd),
                    ) { dragAmount ->
                        val newWidth = max(minSize, rectangle.width + dragAmount.x)
                        rectangle = Rect(
                            offset = rectangle.topLeft,
                            size = Size(newWidth, rectangle.height)
                        )
                    }
                    // Bottom-Start handle
                    DraggableHandle(
                        modifier = Modifier
                            .size(handleSize)
                            .align(Alignment.BottomStart),
                    ) { dragAmount ->
                        val newLeft = rectangle.left + dragAmount.x
                        val newWidth = max(minSize, rectangle.width - dragAmount.x)
                        val newHeight = max(minSize, rectangle.height + dragAmount.y)
                        rectangle = Rect(
                            offset = Offset(newLeft, rectangle.top),
                            size = Size(newWidth, newHeight)
                        )
                    }
                    // Bottom-Center handle
                    DraggableHandle(
                        modifier = Modifier
                            .size(handleSize)
                            .align(Alignment.BottomCenter),
                    ) { dragAmount ->
                        val newHeight = max(minSize, rectangle.height + dragAmount.y)
                        rectangle = Rect(
                            offset = rectangle.topLeft,
                            size = Size(rectangle.width, newHeight)
                        )
                    }
                    // Bottom-End handle
                    DraggableHandle(
                        modifier = Modifier
                            .size(handleSize)
                            .align(Alignment.BottomEnd),
                    ) { dragAmount ->
                        val newWidth = max(minSize, rectangle.width + dragAmount.x)
                        val newHeight = max(minSize, rectangle.height + dragAmount.y)
                        rectangle = Rect(
                            offset = rectangle.topLeft,
                            size = Size(newWidth, newHeight)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun ResizableRectangleWithHandles() {
    var rectWidth by remember { mutableStateOf(200.dp) }
    var rectHeight by remember { mutableStateOf(200.dp) }
    // Position of the top-left corner of the rectangle in pixels
    var rectOffsetX by remember { mutableStateOf(50f) }
    var rectOffsetY by remember { mutableStateOf(50f) }

    val handleSize = 24.dp
    val density = LocalDensity.current
    val handleSizePx = with(density) { handleSize.toPx() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) { // Allow dragging the main rectangle
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    rectOffsetX += dragAmount.x
                    rectOffsetY += dragAmount.y
                }
            },
    ) {
        // The Resizable Rectangle
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        rectOffsetX.roundToInt(),
                        rectOffsetY.roundToInt(),
                    )
                }
                .size(rectWidth, rectHeight)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)), // Semi-transparent
        )

        // Scaling Handles
        HandlePosition.values().forEach { position ->
            // Calculate the absolute top-left offset for each handle
            val handleOffset = with(density) {
                Offset(
                    x = rectOffsetX + when (position) {
                        HandlePosition.TopLeft, HandlePosition.BottomLeft, HandlePosition.CenterLeft -> -handleSizePx / 2
                        HandlePosition.TopCenter, HandlePosition.BottomCenter -> rectWidth.toPx() / 2 - handleSizePx / 2
                        HandlePosition.TopRight, HandlePosition.BottomRight, HandlePosition.CenterRight -> rectWidth.toPx() - handleSizePx / 2
                    },
                    y = rectOffsetY + when (position) {
                        HandlePosition.TopLeft, HandlePosition.TopCenter, HandlePosition.TopRight -> -handleSizePx / 2
                        HandlePosition.CenterLeft, HandlePosition.CenterRight -> rectHeight.toPx() / 2 - handleSizePx / 2
                        HandlePosition.BottomLeft, HandlePosition.BottomCenter, HandlePosition.BottomRight -> rectHeight.toPx() - handleSizePx / 2
                    },
                )
            }

            DraggableHandle(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            handleOffset.x.roundToInt(),
                            handleOffset.y.roundToInt(),
                        )
                    }
                    .size(handleSize),
                onDrag = { dragAmountPx ->
                    // Convert dragAmount (Px) to Dp
                    val dxDp = with(density) { dragAmountPx.x.toDp() }
                    val dyDp = with(density) { dragAmountPx.y.toDp() }

                    // Apply the drag to the rectangle's size and position
                    when (position) {
                        HandlePosition.TopLeft -> {
                            rectWidth -= dxDp
                            rectHeight -= dyDp
                            rectOffsetX += dragAmountPx.x
                            rectOffsetY += dragAmountPx.y
                        }

                        HandlePosition.TopCenter -> {
                            rectHeight -= dyDp
                            rectOffsetY += dragAmountPx.y
                        }

                        HandlePosition.TopRight -> {
                            rectWidth += dxDp
                            rectHeight -= dyDp
                            rectOffsetY += dragAmountPx.y
                        }

                        HandlePosition.CenterLeft -> {
                            rectWidth -= dxDp
                            rectOffsetX += dragAmountPx.x
                        }

                        HandlePosition.CenterRight -> {
                            rectWidth += dxDp
                        }

                        HandlePosition.BottomLeft -> {
                            rectWidth -= dxDp
                            rectHeight += dyDp
                            rectOffsetX += dragAmountPx.x
                        }

                        HandlePosition.BottomCenter -> {
                            rectHeight += dyDp
                        }

                        HandlePosition.BottomRight -> {
                            rectWidth += dxDp
                            rectHeight += dyDp
                        }
                    }

                    // Ensure minimum size
                    rectWidth = rectWidth.coerceAtLeast(handleSize * 2)
                    rectHeight = rectHeight.coerceAtLeast(handleSize * 2)
                },
            )
        }
    }
}

/**
 * A generic draggable handle.
 */
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

@Composable
fun UnifiedManipulableBox() {
    var boxOffset by remember {
        mutableStateOf(
            Offset(
                100f,
                100f,
            ),
        )
    } // Top-left of the entire rotated composable
    var lastPointerAngle by remember { mutableStateOf(0f) } // Rotation in degrees
    var boxRotation by remember { mutableStateOf(0f) } // Rotation in degrees
    val boxWidth = 200.dp // Fixed size for this example, can be made stateful for scaling
    val boxHeight = 150.dp

    val density = LocalDensity.current
    val boxWidthPx = with(density) { boxWidth.toPx() }
    val boxHeightPx = with(density) { boxHeight.toPx() }

    // Center of the box in its own local coordinate system (before rotation and offset)
    val localBoxCenter = Offset(boxWidthPx / 2, boxHeightPx / 2)

    // Handle properties
    val handleRadius = 12.dp
    val handleVisualSize = handleRadius * 2
    // Position the handle above the center of the box, in the box's local coordinates
    val localHandleCenterYOffset =
        -(boxHeightPx / 2) - with(density) { 30.dp.toPx() } // 30.dp above the box
    val localHandleCenter = Offset(localBoxCenter.x, localBoxCenter.y + localHandleCenterYOffset)


    // This outer Box is what gets offset and rotated
    Box(
        modifier = Modifier
            .offset { IntOffset(boxOffset.x.roundToInt(), boxOffset.y.roundToInt()) }
            .graphicsLayer(
                rotationZ = boxRotation,
                // transformOrigin is by default Center, which is what we want for this box
                // assuming its content (the innerBox) is centered or defines its own pivot.
                // More accurately, the rotation pivot is the center of *this* Box's bounds.
            ),
    ) {
        // This inner Box defines the visual content and its size
        // It's positioned at (0,0) within the rotated parent
        Box(
            modifier = Modifier
                .size(boxWidth, boxHeight) // The visible content
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
                .pointerInput(Unit) { // Pointer input for translating the WHOLE BOX
                    detectDragGestures { change, dragAmount ->
                        change.consume()

                        // dragAmount is in the coordinate system of *this* Box's parent
                        // (the outer, rotated Box).
                        // To apply it to boxOffset (screen space), we need to rotate
                        // this dragAmount by the current boxRotation.

                        val angleRad = Math.toRadians(boxRotation.toDouble())
                        val rotatedDx = dragAmount.x * cos(angleRad) - dragAmount.y * sin(angleRad)
                        val rotatedDy = dragAmount.x * sin(angleRad) + dragAmount.y * cos(angleRad)

                        boxOffset += Offset(rotatedDx.toFloat(), rotatedDy.toFloat())
                        Log.d("UnifiedBox", "Box Dragged: offset $boxOffset")
                    }
                },
        ) {
            Text(
                "Drag Me",
                Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }

        // Rotation Handle - positioned relative to the innerBox's coordinate system (0,0 is top-left of innerBox)
        Box(
            modifier = Modifier
                // Offset the handle so its center aligns with localHandleCenter
                .offset {
                    IntOffset(
                        (localHandleCenter.x - with(density) { handleRadius.toPx() }).roundToInt(),
                        (localHandleCenter.y - with(density) { handleRadius.toPx() }).roundToInt(),
                    )
                }
                .size(handleVisualSize)
                .background(MaterialTheme.colorScheme.secondary, CircleShape)
                .pointerInput(Unit) { // Pointer input for ROTATING the WHOLE BOX

                    detectDragGestures(
                        onDragStart = { startOffset ->
                            // Convert startOffset (local to handle) to be relative to the outer box's center
                            // 1. Handle's center in outer box's local coords: localHandleCenter
                            // 2. Pointer position in handle's local coords: startOffset
                            // 3. Pointer position in outer box's local coords:
                            //    localHandleCenter - handleCenterToHandleTopLeft + startOffset
                            val handleCenterToHandleTopLeft = Offset(
                                with(density) { handleRadius.toPx() },
                                with(density) { handleRadius.toPx() },
                            )
                            val pointerRelativeToOuterBoxOrigin =
                                localHandleCenter - handleCenterToHandleTopLeft + startOffset

                            val vectorX = pointerRelativeToOuterBoxOrigin.x - localBoxCenter.x
                            val vectorY = pointerRelativeToOuterBoxOrigin.y - localBoxCenter.y
                            lastPointerAngle =
                                Math.toDegrees(atan2(vectorY, vectorX).toDouble()).toFloat()
                        },
                        onDrag = { change, dragAmount -> // dragAmount local to handle
                            change.consume()

                            // Pointer's current position relative to handle's top-left: change.position
                            // We need pointer's current position relative to the outer box's center (localBoxCenter)
                            val handleCenterToHandleTopLeft = Offset(
                                with(density) { handleRadius.toPx() },
                                with(density) { handleRadius.toPx() },
                            )
                            val pointerRelativeToOuterBoxOrigin =
                                localHandleCenter - handleCenterToHandleTopLeft + change.position


                            val currentVectorX =
                                pointerRelativeToOuterBoxOrigin.x - localBoxCenter.x
                            val currentVectorY =
                                pointerRelativeToOuterBoxOrigin.y - localBoxCenter.y
                            val currentPointerAngle =
                                Math.toDegrees(atan2(currentVectorY, currentVectorX).toDouble())
                                    .toFloat()

                            val diff = currentPointerAngle - lastPointerAngle
                            boxRotation += diff
                            lastPointerAngle = currentPointerAngle // Update for next delta

                            Log.d("UnifiedBox", "Handle Dragged: rotation $boxRotation")
                        },
                    )
                },
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
    }
}

@Preview(showBackground = true)
@Composable
fun UnifiedManipulableBoxPreview() {
    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize()) { // Provide a parent for context
            UnifiedManipulableBox()
        }
    }
}