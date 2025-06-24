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
import androidx.compose.ui.tooling.preview.Preview
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
        val strokeWidth: Float
    ) : DrawingElement()
    // Add other element types if needed
}

data class ElementTransform(
    val scale: Float = 1f,
    val rotation: Float = 0f, // In degrees
    val offset: Offset = Offset.Zero // Represents the top-left of the base rectangle after transformation
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
    val initialTransform: ElementTransform
)

data class ActiveSelectionRectData(
    val baseTopLeft: Offset,
    val baseSize: Size,
    var currentTransform: ElementTransform
)

// --- Drawing Controller ---

class DrawingController2 {
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

    private val selectionHandleSizePx = 10.dp.value // Use .value for raw float if dp isn't available here easily

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
                handleSizePx = selectionHandleSizePx
            )

            if (handleHitType != null) {
                val rectCenter = Offset(
                    currentSelection.baseTopLeft.x + currentSelection.baseSize.width / 2,
                    currentSelection.baseTopLeft.y + currentSelection.baseSize.height / 2
                )
                activeTransformHandle = TransformHandle(
                    type = handleHitType,
                    dragStartOffsetOnCanvas = touchOffset,
                    initialRectCenter = rectCenter,
                    initialTransform = currentSelection.currentTransform.copy()
                )
            } else {
                clearSelection()
            }
        } else if (currentTool == DrawingTool.SELECT) {
            clearSelection()
        }
    }

    fun onDrag(changePosition: Offset) {
        currentDragEndPoint = changePosition

        if (activeTransformHandle != null && currentTool == DrawingTool.SELECT && activeSelectionRectData != null) {
            val handleInfo = activeTransformHandle!!
            val selectionData = activeSelectionRectData!!
            var newTransform = selectionData.currentTransform

            when (handleInfo.type) {
                TransformHandleType.ROTATE -> {
                    val initialAngleRad = atan2(
                        handleInfo.dragStartOffsetOnCanvas.y - handleInfo.initialRectCenter.y,
                        handleInfo.dragStartOffsetOnCanvas.x - handleInfo.initialRectCenter.x
                    )
                    val currentAngleRad = atan2(
                        currentDragEndPoint.y - handleInfo.initialRectCenter.y,
                        currentDragEndPoint.x - handleInfo.initialRectCenter.x
                    )
                    val angleDeltaDeg = Math.toDegrees((currentAngleRad - initialAngleRad).toDouble()).toFloat()
                    newTransform = newTransform.copy(
                        rotation = (handleInfo.initialTransform.rotation + angleDeltaDeg + 360) % 360
                    )
                }
                TransformHandleType.BOTTOM_RIGHT_SCALE -> {
                    val pivot = selectionData.baseTopLeft
                    val originalDistance = (handleInfo.dragStartOffsetOnCanvas - pivot).getDistance()
                    val currentDistance = (currentDragEndPoint - pivot).getDistance()

                    if (originalDistance > 0.001f) {
                        val scaleFactorChange = currentDistance / originalDistance
                        val proposedScale = handleInfo.initialTransform.scale * scaleFactorChange
                        newTransform = newTransform.copy(scale = max(0.1f, proposedScale))
                    }
                }
                // TODO: Implement other scaling handles
                else -> { /* No specific logic yet */ }
            }
            activeSelectionRectData = selectionData.copy(currentTransform = newTransform)
        }
    }

    fun onDragEnd() {
        if (activeTransformHandle == null) { // Only finalize drawing/selection if not transforming
            when (currentTool) {
                DrawingTool.DRAW -> {
                    if ((startDragPoint - currentDragEndPoint).getDistanceSquared() > 0) {
                        drawingElements.add(
                            DrawingElement.Line(startDragPoint, currentDragEndPoint, currentColor, currentStrokeWidth)
                        )
                    }
                }
                DrawingTool.ERASE -> {
                    if ((startDragPoint - currentDragEndPoint).getDistanceSquared() > 0) {
                        drawingElements.add(
                            DrawingElement.Line(startDragPoint, currentDragEndPoint, Color.White, currentStrokeWidth * 1.5f)
                        )
                    }
                }
                DrawingTool.SELECT -> {
                    if (startDragPoint != Offset.Unspecified && currentDragEndPoint != Offset.Unspecified &&
                        (startDragPoint - currentDragEndPoint).getDistanceSquared() > 10f
                    ) {
                        val selTopLeft = Offset(min(startDragPoint.x, currentDragEndPoint.x), min(startDragPoint.y, currentDragEndPoint.y))
                        val selBottomRight = Offset(max(startDragPoint.x, currentDragEndPoint.x), max(startDragPoint.y, currentDragEndPoint.y))
                        val selSize = Size(selBottomRight.x - selTopLeft.x, selBottomRight.y - selTopLeft.y)

                        if (selSize.width > 0 && selSize.height > 0) {
                            activeSelectionRectData = ActiveSelectionRectData(
                                baseTopLeft = selTopLeft,
                                baseSize = selSize,
                                currentTransform = ElementTransform(offset = selTopLeft)
                            )
                        } else {
                            activeSelectionRectData = null
                        }
                    } else {
                        // If it was a tap and not on a handle, and no selection was made, ensure it's cleared
                        if (activeSelectionRectData == null && (startDragPoint - currentDragEndPoint).getDistanceSquared() <= 10f) {
                            // This case is tricky: a tap *outside* an existing selection should clear it.
                            // onDragStart handles clearing if tap is not on a handle of an *existing* selection.
                            // This 'else' might not be needed if onDragStart correctly clears.
                        }
                    }
                }
            }
        }
        // Reset for next operation
        startDragPoint = Offset.Unspecified
        currentDragEndPoint = Offset.Unspecified
        activeTransformHandle = null // Always reset handle on drag end
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
    val controller = remember { DrawingController2() }

    val currentTool by rememberUpdatedState(controller.currentTool)
    val currentColor by rememberUpdatedState(controller.currentColor)
    val currentStrokeWidth by rememberUpdatedState(controller.currentStrokeWidth)
    val drawingElements = controller.drawingElements // Direct mutableStateList observation

    val startDragPoint by rememberUpdatedState(controller.startDragPoint)
    val currentDragEndPoint by rememberUpdatedState(controller.currentDragEndPoint)
    val activeSelectionRectData by rememberUpdatedState(controller.activeSelectionRectData)
    val activeTransformHandle by rememberUpdatedState(controller.activeTransformHandle)


    Column(modifier = Modifier.fillMaxSize()) {
        DrawingToolbar(
            currentTool = currentTool,
            onToolChange = controller::onToolChange,
            currentColor = currentColor,
            onColorChange = controller::onColorChange,
            currentStrokeWidth = currentStrokeWidth,
            onStrokeWidthChange = controller::onStrokeWidthChange,
            onClearCanvas = controller::clearCanvas
        )

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .background(Color.White)
                .pointerInput(currentTool) { // Key on currentTool if gesture logic needs to change with it
                    detectDragGestures(
                        onDragStart = { offset -> controller.onDragStart(offset) },
                        onDrag = { change, _ -> controller.onDrag(change.position) },
                        onDragEnd = { controller.onDragEnd() },
                        onDragCancel = { controller.onDragCancel() }
                    )
                }
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
                                style = Stroke(width = 1.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f)))
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
                        transformToApply = data.currentTransform
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
    onClearCanvas: () -> Unit
) {
    var showColorPicker by remember { mutableStateOf(false) }
    var showStrokePicker by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { onToolChange(DrawingTool.DRAW) }) {
            Icon(Icons.Filled.Brush, "Draw", tint = if (currentTool == DrawingTool.DRAW) MaterialTheme.colorScheme.primary else Color.Gray)
        }
        IconButton(onClick = { onToolChange(DrawingTool.ERASE) }) {
            Icon(Icons.Filled.Clear, "Erase", tint = if (currentTool == DrawingTool.ERASE) MaterialTheme.colorScheme.primary else Color.Gray) // Consider a better erase icon
        }
        IconButton(onClick = { onToolChange(DrawingTool.SELECT) }) {
            Icon(Icons.Filled.AspectRatio, "Select", tint = if (currentTool == DrawingTool.SELECT) MaterialTheme.colorScheme.primary else Color.Gray) // Better selection icon
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
            Column {
                val colors = listOf(Color.Black, Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Cyan, Color.Magenta, Color.Gray)
                colors.chunked(4).forEach { rowColors ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        rowColors.forEach { color ->
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(4.dp)
                                    .background(color, CircleShape)
                                    .border(2.dp, if (initialColor == color) MaterialTheme.colorScheme.primary else Color.Transparent, CircleShape)
                                    .clickable { onColorSelected(color) }
                            )
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Close") } }
    )
}

@Composable
fun StrokePickerDialog(initialStrokeWidth: Float, onStrokeWidthSelected: (Float) -> Unit, onDismiss: () -> Unit) {
    var tempStrokeWidth by remember(initialStrokeWidth) { mutableStateOf(initialStrokeWidth) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Stroke Width") },
        text = {
            Column {
                Slider(value = tempStrokeWidth, onValueChange = { tempStrokeWidth = it }, valueRange = 1f..50f, steps = 48)
                Text(String.format("%.1f", tempStrokeWidth), modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        },
        confirmButton = { TextButton(onClick = { onStrokeWidthSelected(tempStrokeWidth) }) { Text("OK") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

// --- Drawing and Transformation Helper Functions ---

fun DrawScope.drawTransformableSelectionRect(
    baseTopLeft: Offset,
    baseSize: Size,
    color: Color,
    transformToApply: ElementTransform
) {
    val pivotX = baseTopLeft.x + baseSize.width / 2
    val pivotY = baseTopLeft.y + baseSize.height / 2

    withTransform({
        // Apply transformations relative to the pivot
        translate(left = pivotX, top = pivotY) // Move pivot to origin for rotation/scale
        rotate(degrees = transformToApply.rotation)
        scale(scaleX = transformToApply.scale, scaleY = transformToApply.scale)
        translate(left = -pivotX, top = -pivotY) // Move pivot back
        translate(left = transformToApply.offset.x - baseTopLeft.x, top = transformToApply.offset.y - baseTopLeft.y) // Apply overall offset based on how baseTopLeft should move
    }) {
        // Draw the rectangle using baseTopLeft and baseSize, as the transform is applied to the canvas context
        val scaledStrokeWidth = max(0.5f, 2.dp.toPx() / transformToApply.scale)
        drawRect(color, baseTopLeft, baseSize, style = Stroke(width = scaledStrokeWidth))

        val handleSizeOnScreen = 10.dp.toPx()
        val scaledHandleSize = handleSizeOnScreen / transformToApply.scale

        val localHandles = getLocalHandlePositions(Offset.Zero, baseSize, scaledHandleSize, 20.dp.toPx() / transformToApply.scale)

        localHandles.forEach { (type, localHandleCenterRelative) ->
            val actualHandleCenter = baseTopLeft + localHandleCenterRelative
            if (type == TransformHandleType.ROTATE) {
                val rotationHandleRadius = 8.dp.toPx() / transformToApply.scale
                val connectionLineStart = Offset(baseTopLeft.x + baseSize.width / 2, baseTopLeft.y)
                drawLine(color, connectionLineStart, actualHandleCenter, scaledStrokeWidth)
                drawCircle(color, rotationHandleRadius, actualHandleCenter)
            } else {
                drawRect(
                    color,
                    Offset(actualHandleCenter.x - scaledHandleSize / 2, actualHandleCenter.y - scaledHandleSize / 2),
                    Size(scaledHandleSize, scaledHandleSize)
                )
            }
        }
    }
}

/**
 * Calculates handle positions in the local coordinate system of the rectangle.
 * Assumes the rectangle's top-left is at (0,0) for these local calculations.
 */
fun getLocalHandlePositions(
    rectTopLeftInLocal: Offset, // Should typically be Offset.Zero for this calculation
    rectSize: Size,
    handleVisualSize: Float,
    rotationHandleVisualOffsetUp: Float
): Map<TransformHandleType, Offset> {
    return mapOf(
        TransformHandleType.TOP_LEFT_SCALE to rectTopLeftInLocal,
        TransformHandleType.TOP_RIGHT_SCALE to Offset(rectTopLeftInLocal.x + rectSize.width, rectTopLeftInLocal.y),
        TransformHandleType.BOTTOM_LEFT_SCALE to Offset(rectTopLeftInLocal.x, rectTopLeftInLocal.y + rectSize.height),
        TransformHandleType.BOTTOM_RIGHT_SCALE to Offset(rectTopLeftInLocal.x + rectSize.width, rectTopLeftInLocal.y + rectSize.height),
        TransformHandleType.TOP_MID_SCALE_Y to Offset(rectTopLeftInLocal.x + rectSize.width / 2, rectTopLeftInLocal.y),
        TransformHandleType.BOTTOM_MID_SCALE_Y to Offset(rectTopLeftInLocal.x + rectSize.width / 2, rectTopLeftInLocal.y + rectSize.height),
        TransformHandleType.LEFT_MID_SCALE_X to Offset(rectTopLeftInLocal.x, rectTopLeftInLocal.y + rectSize.height / 2),
        TransformHandleType.RIGHT_MID_SCALE_X to Offset(rectTopLeftInLocal.x + rectSize.width, rectTopLeftInLocal.y + rectSize.height / 2),
        TransformHandleType.ROTATE to Offset(rectTopLeftInLocal.x + rectSize.width / 2, rectTopLeftInLocal.y - rotationHandleVisualOffsetUp)
    )
}

fun isPointOnTransformHandle(
    touchOffset: Offset,
    baseRectTopLeft: Offset,
    baseRectSize: Size,
    currentTransform: ElementTransform,
    handleSizePx: Float
): TransformHandleType? {
    val handleRadius = handleSizePx / 2f
    val screenHandlePositions = getScreenHandlePositions(baseRectTopLeft, baseRectSize, currentTransform, handleSizePx)

    screenHandlePositions.forEach { (type, screenCenter) ->
        if ((touchOffset - screenCenter).getDistanceSquared() < handleRadius * handleRadius) {
            return type
        }
    }
    return null
}

fun getScreenHandlePositions(
    baseRectTopLeft: Offset,
    baseRectSize: Size,
    transform: ElementTransform,
    handleVisualSizeOnScreen: Float
): Map<TransformHandleType, Offset> {
    val localHandleDefinitionOffsets = getLocalHandlePositions(
        Offset.Zero, // Calculate local offsets from (0,0)
        baseRectSize,
        handleVisualSizeOnScreen / transform.scale, // Scale handle size inversely for local calculation
        20.dp.value / transform.scale // Also scale the rotation handle offset
    )

    val screenPositions = mutableMapOf<TransformHandleType, Offset>()
    val rectCenterForPivot = baseRectTopLeft + Offset(baseRectSize.width / 2, baseRectSize.height / 2)

    localHandleDefinitionOffsets.forEach { (type, localOffsetFromZero) ->
        var point = baseRectTopLeft + localOffsetFromZero // Actual local point on the untransformed base rectangle

        // Transformation sequence:
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
        // 5. Apply the overall screen offset of the transformed rectangle.
        //    transform.offset is where the original baseRectTopLeft should end up.
        //    So, calculate how much baseRectTopLeft moved due to scale/rotate around center,
        //    then determine the additional translation needed to reach transform.offset.
        var baseTopLeftAfterScaleRotate = baseRectTopLeft - rectCenterForPivot
        baseTopLeftAfterScaleRotate = Offset(baseTopLeftAfterScaleRotate.x * transform.scale, baseTopLeftAfterScaleRotate.y * transform.scale)
        val rX = baseTopLeftAfterScaleRotate.x * cos(angleRad) - baseTopLeftAfterScaleRotate.y * sin(angleRad)
        val rY = baseTopLeftAfterScaleRotate.x * sin(angleRad) + baseTopLeftAfterScaleRotate.y * cos(angleRad)
        baseTopLeftAfterScaleRotate = Offset(rX, rY) + rectCenterForPivot

        val finalTranslation = transform.offset - baseTopLeftAfterScaleRotate
        screenPositions[type] = point + finalTranslation
    }
    return screenPositions
}

// --- Preview ---

@Preview(showBackground = true)
@Composable
fun DrawingScreenPreview() {
    MaterialTheme {
        DrawingScreen()
    }
}