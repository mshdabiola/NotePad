import android.util.Log
import androidx.compose.foundation.background // Corrected: Direct import for Modifier.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
    var rectOffsetX by remember { mutableStateOf(50f) } // Offset in pixels for positioning
    var rectOffsetY by remember { mutableStateOf(50f) } // Offset in pixels for positioning

    val handleSize = 24.dp
    // CORRECT USAGE OF toPx
    val handleSizePx = with(LocalDensity.current) { handleSize.toPx() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) { // Optional: Allow dragging the main rectangle
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
            val currentDensity = LocalDensity.current // Capture density for use in lambdas if needed
            val (alignment, dragLogic) = getHandleProperties(
                position = position,
                currentWidth = rectWidth,
                currentHeight = rectHeight,
                onSizeChange = { dw, dh ->
                    val newWidth = (rectWidth + dw).coerceAtLeast(handleSize * 2) // Min size
                    val newHeight = (rectHeight + dh).coerceAtLeast(handleSize * 2) // Min size

                    // Adjust offset based on which handle is dragged to keep the opposite side fixed
                    // CORRECT USAGE OF toPx (implicitly via Dp.toPx() extension within Density scope)
                    with(currentDensity) { // Ensure Density is in scope for toPx
                        when (position) {
                            HandlePosition.TopLeft -> {
                                rectOffsetX += (rectWidth - newWidth).toPx()
                                rectOffsetY += (rectHeight - newHeight).toPx()
                            }

                            HandlePosition.TopCenter -> {
                                rectOffsetY += (rectHeight - newHeight).toPx()
                            }

                            HandlePosition.TopRight -> {
                                rectOffsetY += (rectHeight - newHeight).toPx()
                            }

                            HandlePosition.CenterLeft -> {
                                rectOffsetX += (rectWidth - newWidth).toPx()
                            }

                            HandlePosition.CenterRight -> { /* No offset change for width */
                            }

                            HandlePosition.BottomLeft -> {
                                rectOffsetX += (rectWidth - newWidth).toPx()
                            }

                            HandlePosition.BottomCenter -> { /* No offset change for height */
                            }

                            HandlePosition.BottomRight -> { /* No offset change */
                            }
                        }
                    }
                    rectWidth = newWidth
                    rectHeight = newHeight
                },
                handleSizePx = handleSizePx, // Pass the already converted px value
                density = currentDensity, // Pass density explicitly
            )

            DraggableHandle(
                modifier = Modifier
                    .offset { // This offset is for the handle relative to the main rectangle's top-left
                        IntOffset(
                            rectOffsetX.roundToInt(),
                            rectOffsetY.roundToInt(),
                        )
                    }
//                    .align(alignment) // Align within the rectangle's conceptual bounds
                    .offset { // Further offset the handle to be on the edge/corner
                        val (xOff, yOff) = when (position) {
                            HandlePosition.TopLeft -> IntOffset(
                                -handleSizePx.roundToInt() / 2,
                                -handleSizePx.roundToInt() / 2,
                            )

                            HandlePosition.TopCenter -> IntOffset(0, -handleSizePx.roundToInt() / 2)
                            HandlePosition.TopRight -> IntOffset(
                                handleSizePx.roundToInt() / 2,
                                -handleSizePx.roundToInt() / 2,
                            )

                            HandlePosition.CenterLeft -> IntOffset(
                                -handleSizePx.roundToInt() / 2,
                                0,
                            )

                            HandlePosition.CenterRight -> IntOffset(
                                handleSizePx.roundToInt() / 2,
                                0,
                            )

                            HandlePosition.BottomLeft -> IntOffset(
                                -handleSizePx.roundToInt() / 2,
                                handleSizePx.roundToInt() / 2,
                            )

                            HandlePosition.BottomCenter -> IntOffset(
                                0,
                                handleSizePx.roundToInt() / 2,
                            )

                            HandlePosition.BottomRight -> IntOffset(
                                handleSizePx.roundToInt() / 2,
                                handleSizePx.roundToInt() / 2,
                            )
                        }

                        // Adjust for rectangle size for correct corner/edge placement
                        // CORRECT USAGE OF toPx (implicitly via Dp.toPx() extension within Density scope)
                        with(currentDensity) { // Ensure Density is in scope for toPx
                            IntOffset(
                                xOff + when (position) {
                                    HandlePosition.TopRight, HandlePosition.CenterRight, HandlePosition.BottomRight -> rectWidth.toPx()
                                        .roundToInt() - handleSizePx.toInt()

                                    HandlePosition.TopCenter, HandlePosition.BottomCenter -> (rectWidth.toPx() / 2).roundToInt() - handleSizePx.toInt() / 2
                                    else -> 0
                                },//- (if (position == HandlePosition.TopCenter || position == HandlePosition.BottomCenter) handleSizePx.roundToInt() / 2 else 0), // Center horizontal middle handles
                                yOff + when (position) {
                                    HandlePosition.BottomLeft, HandlePosition.BottomCenter, HandlePosition.BottomRight -> (rectHeight.toPx()
                                        .roundToInt() - handleSizePx.toInt())

                                    HandlePosition.CenterLeft, HandlePosition.CenterRight -> (rectHeight.toPx() / 2).roundToInt() - handleSizePx.toInt() / 2
                                    else -> 0
                                }, //- (if (position == HandlePosition.CenterLeft || position == HandlePosition.CenterRight) handleSizePx.roundToInt() / 2 else 0), // Center vertical middle handles
                            )
                        }
                    }
                    .size(handleSize),
                onDrag = dragLogic,
            )
        }
    }
}

@Composable
fun DraggableHandle(
    modifier: Modifier = Modifier,
    onDrag: (dragAmount: Offset) -> Unit,
) {
    Button(
        onClick = { /* Handles are for dragging, not clicking */ },
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    onDrag(dragAmount)
                }
            }
            .padding(0.dp), // Remove default button padding if any
    ) {
        // No text or icon needed for small handles
    }
}

// Modified to explicitly take Density
fun getHandleProperties(
    position: HandlePosition,
    currentWidth: Dp,
    currentHeight: Dp,
    onSizeChange: (dw: Dp, dh: Dp) -> Unit,
    handleSizePx: Float, // Already in Px
    density: androidx.compose.ui.unit.Density, // Explicitly pass Density
): Pair<Alignment, (Offset) -> Unit> {
    // val currentWidthPx = with(density) { currentWidth.toPx() } // Not strictly needed here anymore
    // val currentHeightPx = with(density) { currentHeight.toPx() } // Not strictly needed here anymore

    val alignment: Alignment = when (position) {
        HandlePosition.TopLeft -> Alignment.TopStart
        HandlePosition.TopCenter -> Alignment.TopCenter
        HandlePosition.TopRight -> Alignment.TopEnd
        HandlePosition.CenterLeft -> Alignment.CenterStart
        HandlePosition.CenterRight -> Alignment.CenterEnd
        HandlePosition.BottomLeft -> Alignment.BottomStart
        HandlePosition.BottomCenter -> Alignment.BottomCenter
        HandlePosition.BottomRight -> Alignment.BottomEnd
    }

    val dragLogic: (Offset) -> Unit = { dragAmount ->
        // CORRECT USAGE OF toDp (implicitly via Float.toDp() extension within Density scope)
        val dxDp = with(density) { dragAmount.x.toDp() }
        val dyDp = with(density) { dragAmount.y.toDp() }

        when (position) {
            HandlePosition.TopLeft -> onSizeChange(-dxDp, -dyDp)
            HandlePosition.TopCenter -> onSizeChange(0.dp, -dyDp)
            HandlePosition.TopRight -> onSizeChange(dxDp, -dyDp)
            HandlePosition.CenterLeft -> onSizeChange(-dxDp, 0.dp)
            HandlePosition.CenterRight -> onSizeChange(dxDp, 0.dp)
            HandlePosition.BottomLeft -> onSizeChange(-dxDp, dyDp)
            HandlePosition.BottomCenter -> onSizeChange(0.dp, dyDp)
            HandlePosition.BottomRight -> onSizeChange(dxDp, dyDp)
        }
    }
    return Pair(alignment, dragLogic)
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