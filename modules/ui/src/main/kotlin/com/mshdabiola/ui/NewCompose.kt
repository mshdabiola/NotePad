import androidx.compose.foundation.background // Corrected: Direct import for Modifier.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
// import androidx.compose.runtime.getValue // Redundant if using `by` delegate
// import androidx.compose.runtime.setValue // Redundant if using `by` delegate
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

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
                            HandlePosition.CenterRight -> { /* No offset change for width */ }
                            HandlePosition.BottomLeft -> {
                                rectOffsetX += (rectWidth - newWidth).toPx()
                            }
                            HandlePosition.BottomCenter -> { /* No offset change for height */ }
                            HandlePosition.BottomRight -> { /* No offset change */ }
                        }
                    }
                    rectWidth = newWidth
                    rectHeight = newHeight
                },
                handleSizePx = handleSizePx, // Pass the already converted px value
                density = currentDensity // Pass density explicitly
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
                            HandlePosition.TopLeft -> IntOffset(-handleSizePx.roundToInt() / 2, -handleSizePx.roundToInt() / 2)
                            HandlePosition.TopCenter -> IntOffset(0, -handleSizePx.roundToInt() / 2)
                            HandlePosition.TopRight -> IntOffset(handleSizePx.roundToInt() / 2, -handleSizePx.roundToInt() / 2)
                            HandlePosition.CenterLeft -> IntOffset(-handleSizePx.roundToInt() / 2, 0)
                            HandlePosition.CenterRight -> IntOffset(handleSizePx.roundToInt() / 2, 0)
                            HandlePosition.BottomLeft -> IntOffset(-handleSizePx.roundToInt() / 2, handleSizePx.roundToInt() / 2)
                            HandlePosition.BottomCenter -> IntOffset(0, handleSizePx.roundToInt() / 2)
                            HandlePosition.BottomRight -> IntOffset(handleSizePx.roundToInt() / 2, handleSizePx.roundToInt() / 2)
                        }

                        // Adjust for rectangle size for correct corner/edge placement
                        // CORRECT USAGE OF toPx (implicitly via Dp.toPx() extension within Density scope)
                        with(currentDensity) { // Ensure Density is in scope for toPx
                            IntOffset(
                                xOff + when (position) {
                                    HandlePosition.TopRight, HandlePosition.CenterRight, HandlePosition.BottomRight -> rectWidth.toPx().roundToInt()-handleSizePx.toInt()
                                    HandlePosition.TopCenter, HandlePosition.BottomCenter -> (rectWidth.toPx() / 2).roundToInt()-handleSizePx.toInt()/2
                                    else -> 0
                                } ,//- (if (position == HandlePosition.TopCenter || position == HandlePosition.BottomCenter) handleSizePx.roundToInt() / 2 else 0), // Center horizontal middle handles
                                yOff + when (position) {
                                    HandlePosition.BottomLeft, HandlePosition.BottomCenter, HandlePosition.BottomRight ->( rectHeight.toPx().roundToInt()-handleSizePx.toInt())
                                    HandlePosition.CenterLeft, HandlePosition.CenterRight -> (rectHeight.toPx() / 2).roundToInt()-handleSizePx.toInt()/2
                                    else -> 0
                                } //- (if (position == HandlePosition.CenterLeft || position == HandlePosition.CenterRight) handleSizePx.roundToInt() / 2 else 0), // Center vertical middle handles
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
    density: androidx.compose.ui.unit.Density // Explicitly pass Density
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