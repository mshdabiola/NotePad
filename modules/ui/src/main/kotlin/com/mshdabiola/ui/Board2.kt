package com.mshdabiola.ui // Adjust package name as needed

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

// --- Enums and Data Classes ---


sealed class DrawingElement {
    data class Line(
        val start: Offset,
        val end: Offset,
        val color: Color,
        val strokeWidth: Float,
    ) : DrawingElement()
    // Add other element types if needed
}

data class ElementTransform(
    val scale: Float = 1f,
    val rotation: Float = 0f, // In degrees
    val offset: Offset = Offset.Zero, // Represents the target screen position of the baseTopLeft after scale/rotation around its center
)

enum class TransformHandleType {
    TOP_LEFT_SCALE, TOP_RIGHT_SCALE, BOTTOM_LEFT_SCALE, BOTTOM_RIGHT_SCALE,
    TOP_MID_SCALE_Y, BOTTOM_MID_SCALE_Y, LEFT_MID_SCALE_X, RIGHT_MID_SCALE_X,
    ROTATE
}

data class TransformHandle(
    val type: TransformHandleType,
    val dragStartOffsetOnCanvas: Offset,
    val initialRectCenter: Offset,
    val initialTransform: ElementTransform,
)

data class ActiveSelectionRectData(
    val baseTopLeft: Offset, // Original, untransformed top-left
    val baseSize: Size,     // Original, untransformed size
    var currentTransform: ElementTransform,
)

// --- Drawing Controller ---

class DrawingController2(
    val handleScreenSizeDp: Dp = 10.dp, // Desired apparent size of handles on screen
    val rotationHandleOffsetDp: Dp = 20.dp, // Desired apparent offset for rotation handle
) {
    var currentTool by mutableStateOf(DrawingTool.DRAW)
    var currentColor by mutableStateOf(Color.Black)
    var currentStrokeWidth by mutableStateOf(5f)
    val drawingElements = mutableStateListOf<DrawingElement>()

    var startDragPoint by mutableStateOf(Offset.Unspecified)
    var currentDragEndPoint by mutableStateOf(Offset.Unspecified)

    var activeSelectionRectData by mutableStateOf<ActiveSelectionRectData?>(null)
        private set
    var activeTransformHandle by mutableStateOf<TransformHandle?>(null)
        private set

    // Pixel values will be resolved using LocalDensity where needed (e.g., in Composable scope)
    // For now, these are placeholders if used directly in controller calculations outside Composable
    private var handleScreenSizePx: Float = handleScreenSizeDp.value
    private var rotationHandleOffsetPx: Float = rotationHandleOffsetDp.value

    fun updateDensityValues(handlePx: Float, rotationOffsetPx: Float) {
        handleScreenSizePx = handlePx
        rotationHandleOffsetPx = rotationOffsetPx
    }


    fun onToolChange(newTool: DrawingTool) {
        currentTool = newTool
        if (newTool != DrawingTool.SELECT) {
            clearSelection()
        }
    }

    fun onColorChange(newColor: Color) {
        currentColor = newColor
    }

    fun onStrokeWidthChange(newStrokeWidth: Float) {
        currentStrokeWidth = newStrokeWidth
    }

    fun clearCanvas() {
        drawingElements.clear()
        clearSelection()
    }

    private fun clearSelection() {
        activeSelectionRectData = null
        activeTransformHandle = null
    }

    fun onDragStart(touchOffset: Offset) {
        startDragPoint = touchOffset
        currentDragEndPoint = touchOffset
        activeTransformHandle = null

        if (currentTool == DrawingTool.SELECT && activeSelectionRectData != null) {
            val currentSelection = activeSelectionRectData!!
            val handleHitType = isPointOnTransformHandle(
                touchOffset = touchOffset,
                baseRectTopLeft = currentSelection.baseTopLeft,
                baseRectSize = currentSelection.baseSize,
                currentTransform = currentSelection.currentTransform,
                handleScreenSizePx = handleScreenSizePx, // Use the resolved pixel value
                rotationHandleScreenOffsetPx = rotationHandleOffsetPx,
            )

            if (handleHitType != null) {
                val rectCenter = Offset(
                    currentSelection.baseTopLeft.x + currentSelection.baseSize.width / 2,
                    currentSelection.baseTopLeft.y + currentSelection.baseSize.height / 2,
                )
                activeTransformHandle = TransformHandle(
                    type = handleHitType,
                    dragStartOffsetOnCanvas = touchOffset,
                    initialRectCenter = rectCenter,
                    initialTransform = currentSelection.currentTransform.copy(),
                )
            } else {
                // Clicked in select mode, but not on a handle of an existing selection -> clear current selection
                clearSelection()
            }
        } else if (currentTool == DrawingTool.SELECT) {
            // Started drag in select mode with no prior selection -> clear to be safe
            clearSelection()
        }
    }

    fun onDrag(changePosition: Offset) {
        currentDragEndPoint = changePosition

        if (activeTransformHandle != null && currentTool == DrawingTool.SELECT && activeSelectionRectData != null) {
            val handleInfo = activeTransformHandle!!
            val selectionData = activeSelectionRectData!!
            var newTransform = selectionData.currentTransform // Start with the current transform

            val pivot = selectionData.baseTopLeft + Offset(selectionData.baseSize.width / 2, selectionData.baseSize.height / 2)
            // Or handleInfo.initialRectCenter can also be used as the pivot if it's consistently the center

            when (handleInfo.type) {
                TransformHandleType.ROTATE -> {
                    val initialAngleRad = atan2(
                        handleInfo.dragStartOffsetOnCanvas.y - handleInfo.initialRectCenter.y,
                        handleInfo.dragStartOffsetOnCanvas.x - handleInfo.initialRectCenter.x,
                    )
                    val currentAngleRad = atan2(
                        currentDragEndPoint.y - handleInfo.initialRectCenter.y,
                        currentDragEndPoint.x - handleInfo.initialRectCenter.x,
                    )
                    val angleDeltaDeg = Math.toDegrees((currentAngleRad - initialAngleRad).toDouble()).toFloat()
                    newTransform = newTransform.copy(
                        rotation = (handleInfo.initialTransform.rotation + angleDeltaDeg + 360) % 360,
                    )
                }
                TransformHandleType.BOTTOM_RIGHT_SCALE -> {
                    // Pivot for this specific handle is the top-left corner of the base rectangle
                    val scalePivot = selectionData.baseTopLeft

                    // Project drag points onto the coordinate system of the unscaled, unrotated rectangle
                    // to correctly determine scale factor based on distance from pivot.
                    // This is complex if rotation is also involved. A simpler approach:
                    // Calculate distance from pivot to initial touch point and current touch point *on screen*
                    // This works well for uniform scaling from a corner.

                    // Distance from the *visual* pivot (top-left of the transformed rect) to the drag points.
                    // The visual pivot (top-left) is selectionData.currentTransform.offset
                    val visualPivot = selectionData.currentTransform.offset

                    // It's generally better to calculate scale based on the distance from the pivot
                    // to the handle's *original* position vs. the current drag point, relative to the
                    // rectangle's transformed orientation.

                    // Simpler approach: Calculate scale factor based on change in distance from the
                    // center of the rectangle to the drag point, adjusted for initial distance.
                    val originalDistToCenter = (handleInfo.dragStartOffsetOnCanvas - handleInfo.initialRectCenter).getDistance()
                    val currentDistToCenter = (currentDragEndPoint - handleInfo.initialRectCenter).getDistance()

                    if (originalDistToCenter > 0.01f) {
                        val scaleFactorChange = currentDistToCenter / originalDistToCenter
                        val proposedScale = handleInfo.initialTransform.scale * scaleFactorChange
                        newTransform = newTransform.copy(scale = max(0.1f, proposedScale)) // Ensure scale is positive
                    }
                }
                // TODO: Implement precise scaling logic for other handles (TOP_LEFT, etc.)
                // This often involves considering the handle's opposite corner/edge as the pivot
                // and calculating scale based on the change in projected distance.
                else -> { /* No specific logic yet for other scale handles */ }
            }
            activeSelectionRectData = selectionData.copy(currentTransform = newTransform)
        }
    }

    fun onDragEnd() {
        if (activeTransformHandle == null) {
            when (currentTool) {
                DrawingTool.DRAW -> {
                    if ((startDragPoint - currentDragEndPoint).getDistanceSquared() > 1f) { // Min drag distance
                        drawingElements.add(
                            DrawingElement.Line(
                                startDragPoint,
                                currentDragEndPoint,
                                currentColor,
                                currentStrokeWidth
                            ),
                        )
                    }
                }
                DrawingTool.ERASE -> {
                    if ((startDragPoint - currentDragEndPoint).getDistanceSquared() > 1f) {
                        drawingElements.add(
                            DrawingElement.Line(
                                startDragPoint,
                                currentDragEndPoint,
                                Color.White,
                                currentStrokeWidth * 1.5f
                            ),
                        )
                    }
                }
                DrawingTool.SELECT -> {
                    if (startDragPoint != Offset.Unspecified && currentDragEndPoint != Offset.Unspecified &&
                        (startDragPoint - currentDragEndPoint).getDistanceSquared() > (handleScreenSizePx * 0.5f).let { it * it } // Min drag for selection box
                    ) {
                        val selTopLeft = Offset(min(startDragPoint.x, currentDragEndPoint.x), min(startDragPoint.y, currentDragEndPoint.y))
                        val selBottomRight = Offset(max(startDragPoint.x, currentDragEndPoint.x), max(startDragPoint.y, currentDragEndPoint.y))
                        val selSize = Size(selBottomRight.x - selTopLeft.x, selBottomRight.y - selTopLeft.y)

                        if (selSize.width > 0 && selSize.height > 0) {
                            activeSelectionRectData = ActiveSelectionRectData(
                                baseTopLeft = selTopLeft,
                                baseSize = selSize,
                                currentTransform = ElementTransform(offset = selTopLeft), // Initial offset IS the top-left
                            )
                        } else {
                            activeSelectionRectData = null
                        }
                    } else if (activeSelectionRectData == null) {
                        // If it was a tap or very small drag and didn't result in a new selection,
                        // and there wasn't an active selection that got cleared by onDragStart,
                        // we might want to ensure nothing is selected.
                        // This is often handled by onDragStart clearing previous selections.
                    }
                }
            }
        }
        startDragPoint = Offset.Unspecified
        currentDragEndPoint = Offset.Unspecified
        activeTransformHandle = null // Always reset active handle on drag end
    }

    fun onDragCancel() {
        startDragPoint = Offset.Unspecified
        currentDragEndPoint = Offset.Unspecified
        activeTransformHandle = null
    }
}


// --- Composable UI (DrawingScreen and its components) ---

@Composable
fun DrawingScreen() {
    val density = LocalDensity.current
    val controller = remember {
        DrawingController2(handleScreenSizeDp = 44.dp, rotationHandleOffsetDp = 44.dp)
    }

    // Update controller's pixel values when density changes or on init
    LaunchedEffect(density) {
        controller.updateDensityValues(
            handlePx = with(density) { controller.handleScreenSizeDp.toPx() },
            rotationOffsetPx = with(density) { controller.rotationHandleOffsetDp.toPx() },
        )
    }

    val currentTool by rememberUpdatedState(controller.currentTool)
    val currentColor by rememberUpdatedState(controller.currentColor)
    val currentStrokeWidth by rememberUpdatedState(controller.currentStrokeWidth)
    val drawingElements = controller.drawingElements

    val startDragPoint by rememberUpdatedState(controller.startDragPoint)
    val currentDragEndPoint by rememberUpdatedState(controller.currentDragEndPoint)
    val activeSelectionRectData by rememberUpdatedState(controller.activeSelectionRectData)
    val activeTransformHandle by rememberUpdatedState(controller.activeTransformHandle) // Needed for canvas logic

    val handleSizePx = with(density) { controller.handleScreenSizeDp.toPx() }
    val rotationHandleOffsetPx = with(density) { controller.rotationHandleOffsetDp.toPx() }


    Column(modifier = Modifier.fillMaxSize()) {
        DrawingToolbar(
            currentTool = currentTool,
            onToolChange = controller::onToolChange,
            currentColor = currentColor,
            onColorChange = controller::onColorChange,
            currentStrokeWidth = currentStrokeWidth,
            onStrokeWidthChange = controller::onStrokeWidthChange,
            onClearCanvas = controller::clearCanvas,
        )

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .background(Color.White)
                .pointerInput(currentTool) { // Re-evaluate if currentTool changes gesture needs
                    detectDragGestures(
                        onDragStart = { offset -> controller.onDragStart(offset) },
                        onDrag = { change, _ -> controller.onDrag(change.position) },
                        onDragEnd = { controller.onDragEnd() },
                        onDragCancel = { controller.onDragCancel() },
                    )
                },
        ) {
            // 1. Draw all existing elements
            drawingElements.forEach { element ->
                when (element) {
                    is DrawingElement.Line -> {
                        drawLine(element.color, element.start, element.end, element.strokeWidth)
                    }
                }
            }

            // 2. Live feedback for current drawing/erasing/selection box (if not transforming)
            if (activeTransformHandle == null && startDragPoint != Offset.Unspecified && currentDragEndPoint != Offset.Unspecified) {
                when (currentTool) {
                    DrawingTool.DRAW -> drawLine(currentColor, startDragPoint, currentDragEndPoint, currentStrokeWidth)
                    DrawingTool.ERASE -> drawLine(Color.White, startDragPoint, currentDragEndPoint, currentStrokeWidth * 1.5f)
                    DrawingTool.SELECT -> {
                        val tempRect = Rect(startDragPoint, currentDragEndPoint)
                        if (tempRect.width > 0 && tempRect.height > 0) {
                            drawRect(
                                color = Color.Blue.copy(alpha = 0.5f),
                                topLeft = tempRect.topLeft,
                                size = tempRect.size,
                                style = Stroke(
                                    width = 1.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
                                ),
                            )
                        }
                    }
                }
            }

            // 3. Draw the active, transformable selection rectangle
            activeSelectionRectData?.let { data ->
                if (currentTool == DrawingTool.SELECT) { // Only draw if in select mode
                    drawTransformableSelectionRect(
                        baseTopLeft = data.baseTopLeft,
                        baseSize = data.baseSize,
                        color = Color.Blue,
                        transformToApply = data.currentTransform,
                        handleScreenSizePx = handleSizePx,
                        rotationHandleScreenOffsetPx = rotationHandleOffsetPx,
                    )
                }
            }
        }
    }
}

@Composable
fun DrawingToolbar(
    currentTool: DrawingTool,
    onToolChange: (DrawingTool) -> Unit,
    currentColor: Color,
    onColorChange: (Color) -> Unit,
    currentStrokeWidth: Float,
    onStrokeWidthChange: (Float) -> Unit,
    onClearCanvas: () -> Unit,
) {
    var showColorPicker by remember { mutableStateOf(false) }
    var showStrokePicker by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = { onToolChange(DrawingTool.DRAW) }) {
            Icon(Icons.Filled.Brush, "Draw", tint = if (currentTool == DrawingTool.DRAW) MaterialTheme.colorScheme.primary else LocalContentColor.current)
        }
        IconButton(onClick = { onToolChange(DrawingTool.ERASE) }) {
            Icon(Icons.Default.Clear, "Erase", tint = if (currentTool == DrawingTool.ERASE) MaterialTheme.colorScheme.primary else LocalContentColor.current) // Better erase icon
        }
        IconButton(onClick = { onToolChange(DrawingTool.SELECT) }) {
            Icon(Icons.Filled.SelectAll, "Select", tint = if (currentTool == DrawingTool.SELECT) MaterialTheme.colorScheme.primary else LocalContentColor.current) // Better selection icon
        }
        IconButton(onClick = { showColorPicker = true }) { Icon(Icons.Filled.ColorLens, "Color", tint = currentColor) }
        Button(onClick = { showStrokePicker = true }) { Text(String.format("%.1f", currentStrokeWidth)) }
        IconButton(onClick = onClearCanvas) { Icon(Icons.Filled.DeleteOutline, "Clear Canvas") }
    }

    if (showColorPicker) {
        ColorPickerDialog(initialColor = currentColor, onColorSelected = { onColorChange(it); showColorPicker = false }, onDismiss = { showColorPicker = false })
    }
    if (showStrokePicker) {
        StrokePickerDialog(initialStrokeWidth = currentStrokeWidth, onStrokeWidthSelected = { onStrokeWidthChange(it); showStrokePicker = false }, onDismiss = { showStrokePicker = false })
    }
}

@Composable
fun ColorPickerDialog(initialColor: Color, onColorSelected: (Color) -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Color") },
        text = {
            Column(modifier = Modifier.padding(8.dp)) {
                val colors = listOf(
                    Color.Black,
                    Color.Red,
                    Color.Green,
                    Color.Blue,
                    Color.Yellow,
                    Color.Cyan,
                    Color.Magenta,
                    Color.Gray,
                    Color.White,
                    Color.DarkGray,
                    Color.LightGray,
                    Color.Transparent
                )
                colors.chunked(4).forEach { rowColors ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        rowColors.forEach { color ->
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(color, CircleShape)
                                    .border(
                                        2.dp,
                                        if (initialColor == color) MaterialTheme.colorScheme.primary else Color.LightGray.copy(
                                            alpha = 0.5f
                                        ),
                                        CircleShape,
                                    )
                                    .clickable { onColorSelected(color) },
                            )
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Close") } },
    )
}

@Composable
fun StrokePickerDialog(initialStrokeWidth: Float, onStrokeWidthSelected: (Float) -> Unit, onDismiss: () -> Unit) {
    var tempStrokeWidth by remember(initialStrokeWidth) { mutableStateOf(initialStrokeWidth) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Stroke Width") },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(8.dp)
            ) {
                Slider(
                    value = tempStrokeWidth,
                    onValueChange = { tempStrokeWidth = it },
                    valueRange = 1f..50f,
                    steps = 48, // 50-1 = 49 steps, so 48 intermediate points
                )
                Text(
                    String.format("%.1f px", tempStrokeWidth),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        },
        confirmButton = { TextButton(onClick = { onStrokeWidthSelected(tempStrokeWidth) }) { Text("OK") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

// --- Drawing and Transformation Helper Functions ---

fun DrawScope.drawTransformableSelectionRect(
    baseTopLeft: Offset,
    baseSize: Size,
    color: Color,
    transformToApply: ElementTransform,
    handleScreenSizePx: Float, // Apparent size on screen
    rotationHandleScreenOffsetPx: Float, // Apparent offset on screen
) {
    val pivotX = baseTopLeft.x + baseSize.width / 2
    val pivotY = baseTopLeft.y + baseSize.height / 2

    withTransform(
        {
            // Apply transformations for the main rectangle and its handles
            translate(left = pivotX, top = pivotY)
            rotate(degrees = transformToApply.rotation)
            scale(scaleX = transformToApply.scale, scaleY = transformToApply.scale)
            translate(left = -pivotX, top = -pivotY)
            // Apply the final offset that ensures baseTopLeft (after scale/rotate around its center) moves to transformToApply.offset
            translate(
                left = transformToApply.offset.x - baseTopLeft.x,
                top = transformToApply.offset.y - baseTopLeft.y
            )
        },
    ) {
        // --- Draw the main rectangle ---
        // Stroke width of the rectangle itself should also scale visually with the rectangle
        val rectStrokeWidth = max(0.5f, 1.5.dp.toPx() / transformToApply.scale)
        drawRect(color, baseTopLeft, baseSize, style = Stroke(width = rectStrokeWidth))

        // --- Draw Handles ---
        // To make handles appear `handleScreenSizePx` on screen, their drawing size in this
        // scaled/rotated canvas must be inversely scaled.
        val handleLocalVisualSize = handleScreenSizePx / transformToApply.scale
        val rotationHandleLocalOffsetUp = rotationHandleScreenOffsetPx / transformToApply.scale
        val handleStrokeWidth = max(0.5f, 1.dp.toPx() / transformToApply.scale) // Stroke for handle visuals

        val localHandlePositions = getLocalHandlePositions(
            rectTopLeftInLocal = Offset.Zero, // Calculate relative to origin for local space
            rectSize = baseSize,
            handleVisualSize = handleLocalVisualSize, // Use this for local geometric definition
            rotationHandleVisualOffsetUp = rotationHandleLocalOffsetUp,
        )

        localHandlePositions.forEach { (type, localHandleCenterRelativeToBaseTopLeft) ->
            // actualHandleCenter is in the coordinate system of the untransformed base rectangle
            val actualHandleCenterInBaseCoords = baseTopLeft + localHandleCenterRelativeToBaseTopLeft

            if (type == TransformHandleType.ROTATE) {
                val rotationHandleRadiusLocal = (handleScreenSizePx * 0.8f) / transformToApply.scale // Slightly smaller radius for circle
                val connectionLineStart = Offset(baseTopLeft.x + baseSize.width / 2, baseTopLeft.y) // Mid-top of base rect

                drawLine(color.copy(alpha=0.7f), connectionLineStart, actualHandleCenterInBaseCoords, handleStrokeWidth)
                drawCircle(color, rotationHandleRadiusLocal, actualHandleCenterInBaseCoords, style = Stroke(handleStrokeWidth))
                drawCircle(color.copy(alpha=0.3f), rotationHandleRadiusLocal * 0.6f, actualHandleCenterInBaseCoords) // Inner fill
            } else {
                // For square handles
                drawRect(
                    color = color.copy(alpha = 0.3f), // Semi-transparent fill
                    topLeft = Offset(
                        actualHandleCenterInBaseCoords.x - handleLocalVisualSize / 2,
                        actualHandleCenterInBaseCoords.y - handleLocalVisualSize / 2
                    ),
                    size = Size(handleLocalVisualSize, handleLocalVisualSize),
                )
                drawRect(
                    color = color, // Solid border
                    topLeft = Offset(
                        actualHandleCenterInBaseCoords.x - handleLocalVisualSize / 2,
                        actualHandleCenterInBaseCoords.y - handleLocalVisualSize / 2
                    ),
                    size = Size(handleLocalVisualSize, handleLocalVisualSize),
                    style = Stroke(width = handleStrokeWidth),
                )
            }
        }
    }
}


fun getLocalHandlePositions(
    rectTopLeftInLocal: Offset, // Typically Offset.Zero
    rectSize: Size,
    handleVisualSize: Float, // This is the size used for local geometric definition (e.g., side of a square handle)
    rotationHandleVisualOffsetUp: Float,
): Map<TransformHandleType, Offset> {
    // These offsets are from rectTopLeftInLocal
    return mapOf(
        TransformHandleType.TOP_LEFT_SCALE to rectTopLeftInLocal,
        TransformHandleType.TOP_RIGHT_SCALE to Offset(
            rectTopLeftInLocal.x + rectSize.width,
            rectTopLeftInLocal.y
        ),
        TransformHandleType.BOTTOM_LEFT_SCALE to Offset(
            rectTopLeftInLocal.x,
            rectTopLeftInLocal.y + rectSize.height
        ),
        TransformHandleType.BOTTOM_RIGHT_SCALE to Offset(
            rectTopLeftInLocal.x + rectSize.width,
            rectTopLeftInLocal.y + rectSize.height
        ),
        TransformHandleType.TOP_MID_SCALE_Y to Offset(
            rectTopLeftInLocal.x + rectSize.width / 2,
            rectTopLeftInLocal.y
        ),
        TransformHandleType.BOTTOM_MID_SCALE_Y to Offset(
            rectTopLeftInLocal.x + rectSize.width / 2,
            rectTopLeftInLocal.y + rectSize.height
        ),
        TransformHandleType.LEFT_MID_SCALE_X to Offset(
            rectTopLeftInLocal.x,
            rectTopLeftInLocal.y + rectSize.height / 2
        ),
        TransformHandleType.RIGHT_MID_SCALE_X to Offset(
            rectTopLeftInLocal.x + rectSize.width,
            rectTopLeftInLocal.y + rectSize.height / 2
        ),
        TransformHandleType.ROTATE to Offset(
            rectTopLeftInLocal.x + rectSize.width / 2,
            rectTopLeftInLocal.y - rotationHandleVisualOffsetUp
        ),
    )
}

fun isPointOnTransformHandle(
    touchOffset: Offset,
    baseRectTopLeft: Offset,
    baseRectSize: Size,
    currentTransform: ElementTransform,
    handleScreenSizePx: Float, // Apparent touchable size on screen
    rotationHandleScreenOffsetPx: Float, // Apparent offset for rotation handle on screen
): TransformHandleType? {
    val handleHitRadius = handleScreenSizePx / 2f // Use half of the apparent size for touch radius

    val screenHandlePositions = getScreenHandlePositions(
        baseRectTopLeft = baseRectTopLeft,
        baseRectSize = baseRectSize,
        transform = currentTransform,
        handleApparentScreenSizePx = handleScreenSizePx,
        rotationHandleApparentScreenOffsetPx = rotationHandleScreenOffsetPx,
    )

    // Check distance from touch point to the *center* of each screen handle
    // Iterate in reverse for potentially overlapping handles (e.g., rotate might be on top)
    for ((type, screenCenter) in screenHandlePositions.entries.reversed()) {
        if ((touchOffset - screenCenter).getDistanceSquared() < handleHitRadius * handleHitRadius) {
            return type
        }
    }
    return null
}

fun getScreenHandlePositions(
    baseRectTopLeft: Offset,
    baseRectSize: Size,
    transform: ElementTransform,
    handleApparentScreenSizePx: Float,
    rotationHandleApparentScreenOffsetPx: Float,
): Map<TransformHandleType, Offset> {

    // For local calculations, use sizes that, when scaled by transform.scale, result in the apparent screen size.
    val handleLocalVisualSize = handleApparentScreenSizePx / transform.scale
    val rotationHandleLocalOffset = rotationHandleApparentScreenOffsetPx / transform.scale

    val localHandleDefinitionOffsets = getLocalHandlePositions(
        rectTopLeftInLocal = Offset.Zero, // Define handles relative to (0,0) in local space
        rectSize = baseRectSize,
        handleVisualSize = handleLocalVisualSize,
        rotationHandleVisualOffsetUp = rotationHandleLocalOffset,
    )

    val screenPositions = mutableMapOf<TransformHandleType, Offset>()
    val rectCenterForPivot = baseRectTopLeft + Offset(baseRectSize.width / 2, baseRectSize.height / 2)

    localHandleDefinitionOffsets.forEach { (type, localOffsetFromOrigin) ->
        // Start with the handle's position if baseTopLeft was at (0,0) and then add baseTopLeft
        var point = baseRectTopLeft + localOffsetFromOrigin // This is the handle's position in the untransformed rectangle's coordinate space

        // Transformation Sequence:
        // 1. Translate point so rotation/scale pivot (rect center) is at origin
        point -= rectCenterForPivot
        // 2. Scale
        point = Offset(point.x * transform.scale, point.y * transform.scale)
        // 3. Rotate
        val angleRad = Math.toRadians(transform.rotation.toDouble()).toFloat()
        val rotatedX = point.x * cos(angleRad) - point.y * sin(angleRad)
        val rotatedY = point.x * sin(angleRad) + point.y * cos(angleRad)
        point = Offset(rotatedX, rotatedY)
        // 4. Translate point back from origin (add rect center back)
        point += rectCenterForPivot

        // 5. Apply the final screen offset of the transformed rectangle.
        //    transform.offset is where the original baseRectTopLeft should end up *on screen*.
        //    We need to find out where baseRectTopLeft *would be* after just scale/rotate around its center,
        //    and then add the translation needed to move it to transform.offset.

        var baseTopLeftAfterPivotOps = baseRectTopLeft - rectCenterForPivot // to origin
        baseTopLeftAfterPivotOps = Offset(baseTopLeftAfterPivotOps.x * transform.scale, baseTopLeftAfterPivotOps.y * transform.scale) // scale

        val rX = baseTopLeftAfterPivotOps.x * cos(angleRad) - baseTopLeftAfterPivotOps.y * sin(angleRad)
        val rY = baseTopLeftAfterPivotOps.x * sin(angleRad) + baseTopLeftAfterPivotOps.y * cos(angleRad)
        baseTopLeftAfterPivotOps = Offset(rX, rY) // rotate
        baseTopLeftAfterPivotOps += rectCenterForPivot // translate back from origin

        // The finalTranslation is how much the *entire group* (rectangle and handles)
        // needs to shift so that the original baseTopLeft (after its own scale/rotate)
        // lands on transform.offset.
        val finalOverallTranslation = transform.offset - baseTopLeftAfterPivotOps
        screenPositions[type] = point + finalOverallTranslation
    }
    return screenPositions
}


// --- Preview ---

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp,dpi=420")
@Composable
fun DrawingScreenPreview() {
    MaterialTheme { // Ensure a MaterialTheme is applied for previews
        DrawingScreen()
    }
}