import android.util.Log
import androidx.compose.foundation.background // Corrected: Direct import for Modifier.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
// import androidx.compose.runtime.getValue // Redundant if using `by` delegate
// import androidx.compose.runtime.setValue // Redundant if using `by` delegate
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

enum class HandlePosition {
    TopLeft, TopCenter, TopRight,
    CenterLeft, CenterRight,
    BottomLeft, BottomCenter, BottomRight
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
                .offset { IntOffset(rectOffsetX.roundToInt(), rectOffsetY.roundToInt()) }
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
                    }
                )
            }

            DraggableHandle(
                modifier = Modifier
                    .offset { IntOffset(handleOffset.x.roundToInt(), handleOffset.y.roundToInt()) }
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
            .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(2.dp))
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
        ResizableRectangleWithHandles()
    }
}

// Removed the custom Modifier.background extension as it's not strictly necessary
// and could potentially cause confusion if not implemented carefully.
// The standard androidx.compose.foundation.background works fine.


/**
 * A composable that demonstrates a rotatable and draggable element with a dedicated rotation handle.
 *
 * This element does NOT interact with the DrawingScreen's drawingElements. It's a separate example.
 */
@Composable
fun RotatableElementWithHandle() {
    // Rotation angle of the element in degrees
    var rotationAngle by remember { mutableStateOf(0f) }
    // Center position of the element in pixels
    var elementPosition by remember { mutableStateOf(Offset(500f, 500f)) } // Adjusted initial position
    val elementSize = 150.dp // Size of the main element

    val handleRadius = 12.dp // Radius of the rotation handle circle
    val handleVisualSize = handleRadius * 2 // Diameter for the handle Box
    val handleDistanceFromElementCenter = 100.dp // Distance of the handle from the element's center

    val density = LocalDensity.current
    val elementSizePx = with(density) { elementSize.toPx() }
    val handleRadiusPx = with(density) { handleRadius.toPx() }
    val handleDistanceFromElementCenterPx = with(density) { handleDistanceFromElementCenter.toPx() }

    // State to store the absolute position of the handle's top-left corner
    // This will be used to convert local pointer events to global for rotation calculation
    var handleAbsoluteOffset by remember { mutableStateOf(Offset.Zero) }

    // State to store the initial angle of the pointer relative to element center on drag start (in radians)
    var initialPointerAngleRad by remember { mutableStateOf(0f) }
    // State to store the element's rotation at the start of the drag (in radians)
    var initialElementRotationRad by remember { mutableStateOf(0f) }


    Box(modifier = Modifier.fillMaxSize()) {
        // --- Draggable Element ---
        Box(
            modifier = Modifier
                .offset {
                    // Position the element based on its center
                    IntOffset(
                        (elementPosition.x - elementSizePx / 2).roundToInt(),
                        (elementPosition.y - elementSizePx / 2).roundToInt(),
                    )
                }
                .size(elementSize)
                .graphicsLayer(
                    rotationZ = rotationAngle,
                    transformOrigin = TransformOrigin(0.5f, 0.5f) // Ensure rotation is around its center
                )
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        elementPosition += dragAmount
                        // Log.d("RotationDebug", "Element dragged. New ElementPos: $elementPosition") // Re-enable for debugging
                    }
                },
        ) {
            // Content of your rotatable element
            Text(
                "Rotate Me!",
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        // --- Rotation Handle ---
        // Calculate the handle's desired center position based on element's center and its current rotation
        // The handle itself moves with the rotated element for intuitive interaction
        val currentRotationRadForHandle = Math.toRadians(rotationAngle.toDouble()) // Use element's current rotation
        val handleRelativeOffsetX = cos(currentRotationRadForHandle) * handleDistanceFromElementCenterPx
        val handleRelativeOffsetY = sin(currentRotationRadForHandle) * handleDistanceFromElementCenterPx

        // This is the calculated CENTER of the handle in absolute screen coordinates
        val handleCenterXAbsolute = elementPosition.x + handleRelativeOffsetX.toFloat()
        val handleCenterYAbsolute = elementPosition.y + handleRelativeOffsetY.toFloat()

        Box(
            modifier = Modifier
                .offset { // This offset is for the TOP-LEFT corner of the handle's Box
                    IntOffset(
                        (handleCenterXAbsolute - handleRadiusPx).roundToInt(),
                        (handleCenterYAbsolute - handleRadiusPx).roundToInt(),
                    )
                }
                .size(handleVisualSize) // Diameter
                .background(MaterialTheme.colorScheme.secondary, CircleShape)
                .border(1.dp, Color.White, CircleShape) // White border for contrast
                .onGloballyPositioned { coordinates ->
                    // Capture the absolute offset of the handle's top-left corner
                    handleAbsoluteOffset = coordinates.positionInWindow()
                }
                .pointerInput(elementPosition) { // Key to elementPosition as handle position depends on it
                    detectDragGestures(
                        onDragStart = { startOffset ->
                            // Convert startOffset (relative to handle) to absolute screen position
                            val absolutePointerStart = handleAbsoluteOffset + startOffset

                            // Calculate the initial vector from element's center to the absolute pointer start
                            val initialVectorX = absolutePointerStart.x - elementPosition.x
                            val initialVectorY = absolutePointerStart.y - elementPosition.y

                            initialPointerAngleRad = atan2(initialVectorY, initialVectorX)
                            initialElementRotationRad = Math.toRadians(rotationAngle.toDouble()).toFloat() // Store current element rotation in radians

                            // Log.d("RotationDebug", "Handle DRAG START. Initial Element Rotation: $initialElementRotationRad, Initial Pointer Angle: $initialPointerAngleRad") // Re-enable for debugging
                        },
                        onDrag = { change, _ ->
                            change.consume()

                            // Convert change.position (relative to handle) to absolute screen position
                            val currentAbsolutePointer = handleAbsoluteOffset + change.position

                            // Calculate the current vector from element's center to the absolute current pointer
                            val currentVectorX = currentAbsolutePointer.x - elementPosition.x
                            val currentVectorY = currentAbsolutePointer.y - elementPosition.y

                            val currentAngleRad = atan2(currentVectorY, currentVectorX)

                            // Calculate the change in angle relative to the initial pointer angle
                            val deltaAngleRad = currentAngleRad - initialPointerAngleRad

                            // Apply this delta to the element's initial rotation
                            var newRotationRad = initialElementRotationRad + deltaAngleRad
                            var newRotationDeg = Math.toDegrees(newRotationRad.toDouble()).toFloat()

                            // Normalize angle to 0-360 degrees (optional, keeps value cleaner)
                            while (newRotationDeg < 0f) newRotationDeg += 360f
                            newRotationDeg %= 360f

                            rotationAngle = newRotationDeg

                            // Log.d("RotationDebug", "DRAG Event: Current Angle Rad: $currentAngleRad, New Rotation Angle: $newRotationDeg") // Re-enable for debugging
                        },
                        onDragEnd = {
                            // Log.d("RotationDebug", "Handle DRAG END") // Re-enable for debugging
                        },
                    )
                },
        ) {
            Icon(
                Icons.Filled.Refresh,
                contentDescription = "Rotate Handle",
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(handleVisualSize * 0.6f), // Adjust icon size to fit handle
                tint = MaterialTheme.colorScheme.onSecondary,
            )
        }
    }
}

@Preview(showBackground = true, name = "Rotatable Element")
@Composable
fun RotatableElementWithHandlePreview() {
    MaterialTheme {
        RotatableElementWithHandle()
    }
}