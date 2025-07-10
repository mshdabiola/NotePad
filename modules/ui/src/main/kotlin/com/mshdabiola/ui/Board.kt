package com.mshdabiola.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.mshdabiola.model.DrawingPath
import kotlin.math.max
import kotlin.math.min

@Composable
fun Board(
    modifier: Modifier = Modifier,
    controller: DrawingController = remember { DrawingController() },
) {
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .pointerInput(controller.currentTool) { // Re-key on tool if necessary
                detectDragGestures(
                    onDragStart = { offset -> controller.onDragStart(offset) },
                    onDrag = { change, dragAmount -> controller.onDrag(change, dragAmount) },
                    onDragEnd = { controller.onDragEnd() },
                    onDragCancel = { /* Optional: Handle cancellation */ },
                )
            },
    ) {
        // Draw existing paths
        controller.drawingPaths.forEach { drawingPath ->

            if (drawingPath.drawingProperties.isPen) {
                val points = drawingPath.paths // You'll need to add this to NewDrawingController

                if (points.size > 1) {
                    val maxStrokeWidthPx = drawingPath.strokeWidth.width
                    val taperSegments = 30 // How many of the last segments to taper
                    val minStrokeFactor = 0.1f // How thin the line can get (e.g., 10% of max width)

                    for (i in 0 until points.size - 1) {
                        val startPoint = points[i]
                        val endPoint = points[i + 1]

                        // Calculate current segment's position from the end of the path
                        val segmentsFromEnd = points.size - 1 - i
                        val currentStrokeWidthPx = if (segmentsFromEnd <= taperSegments) {
                            // Apply tapering
                            val taperFactor = (segmentsFromEnd - 1).toFloat() / taperSegments.toFloat()
                            // Ensure stroke width doesn't go below a minimum, and interpolate
                            max(maxStrokeWidthPx * minStrokeFactor, maxStrokeWidthPx * taperFactor)
                        } else {
                            maxStrokeWidthPx // Full width for segments not being tapered
                        }
                        // Ensure we don't go to zero if not intended
                        val finalWidth = max(1f, currentStrokeWidthPx)

                        // Draw each segment as a line
                        // For smoother curves with varying width, you'd typically draw quadratic/cubic Beziers
                        // between triplets/quadruplets of points, adjusting control points and width.
                        // For simplicity, we draw lines here.
                        drawLine(
                            color = drawingPath.color,
                            start = startPoint,
                            end = endPoint,
                            strokeWidth = finalWidth,
                            cap = drawingPath.strokeWidth.cap, // Use cap from original stroke
                            // pathEffect = controller.currentPath.strokeWidth.pathEffect // If any
                        )
                    }
                }
            } else {
                drawPath(
                    path = drawingPath.path,
                    color = drawingPath.color,
                    style = drawingPath.strokeWidth,
                )
            }
        }

        // Draw current drawing/erasing path (the one actively being drawn)
        if (controller.currentTool == DrawingTool.DRAW) {
            if (controller.currentDrawingProperties.isPen) {
                val points =
                    controller.currentPath.paths // You'll need to add this to NewDrawingController

                if (points.size > 1) {
                    val maxStrokeWidthPx = controller.currentPath.strokeWidth.width
                    val taperSegments = 25 // How many of the last segments to taper
                    val minStrokeFactor = 0.1f // How thin the line can get (e.g., 10% of max width)

                    for (i in 0 until points.size - 1) {
                        val startPoint = points[i]
                        val endPoint = points[i + 1]

                        // Calculate current segment's position from the end of the path
                        val segmentsFromEnd = points.size - 1 - i
                        val currentStrokeWidthPx = if (segmentsFromEnd <= taperSegments) {
                            // Apply tapering
                            val taperFactor = (segmentsFromEnd - 1).toFloat() / taperSegments.toFloat()
                            // Ensure stroke width doesn't go below a minimum, and interpolate
                            max(maxStrokeWidthPx * minStrokeFactor, maxStrokeWidthPx * taperFactor)
                        } else {
                            maxStrokeWidthPx // Full width for segments not being tapered
                        }
                        // Ensure we don't go to zero if not intended
                        val finalWidth = max(1f, currentStrokeWidthPx)

                        drawLine(
                            color = controller.currentPath.color,
                            start = startPoint,
                            end = endPoint,
                            strokeWidth = finalWidth,
                            cap = controller.currentPath.strokeWidth.cap, // Use cap from original stroke
                            // pathEffect = controller.currentPath.strokeWidth.pathEffect // If any
                        )
                    }
                }
            } else {
                drawPath(
                    path = controller.currentPath.path,
                    color = controller.currentPath.color,
                    style = controller.currentPath.strokeWidth,
                )
            }
        }

        // Draw the visual selection rectangle during drag (for SELECT tool)
        controller.selectionRect?.let { rect ->
            val normalizedRect = Rect(
                // Explicitly use compose.ui.geometry.Rect
                left = min(rect.left, rect.right),
                top = min(rect.top, rect.bottom),
                right = max(rect.left, rect.right),
                bottom = max(rect.top, rect.bottom),
            )
            drawRect(
                color = Color.Blue.copy(alpha = 0.3f),
                topLeft = normalizedRect.topLeft,
                size = normalizedRect.size,
                style = Stroke(width = 1.dp.toPx()),
            )
        }

        // Draw the collective bounding box for ALL selected paths
        controller.collectiveSelectedPathsBounds?.let { bounds ->
            drawRect(
                color = Color.Magenta.copy(alpha = 0.5f),
                topLeft = bounds.topLeft,
                size = bounds.size,
                style = Stroke(width = 2.dp.toPx()),
            )
        }
    }
}

// ... other imports from Board.kt
// ... Board composable ...
@Composable
fun BoardViewer(
    modifier: Modifier = Modifier,
    drawingPaths: List<DrawingPath>,
) {
    Canvas(
        modifier = modifier
            .graphicsLayer { // Using graphicsLayer for clipping is good
                clip = true
            }
            .background(Color.White), // Added a background to see canvas bounds
    ) {
        if (drawingPaths.isEmpty()) {
            return@Canvas // Nothing to draw
        }

        // 1. Calculate the bounding box of all drawing paths
        var minX = Float.MAX_VALUE
        var minY = Float.MAX_VALUE
        var maxX = Float.MIN_VALUE
        var maxY = Float.MIN_VALUE

        val pathMeasure = PathMeasure() // Reusable PathMeasure

        drawingPaths.forEach { drawingPath ->
            // If drawingPath.paths directly gives you List<Offset> for pen strokes
            if (drawingPath.drawingProperties.isPen && drawingPath.paths.isNotEmpty()) {
                drawingPath.paths.forEach { point ->
                    minX = min(minX, point.x)
                    minY = min(minY, point.y)
                    maxX = max(maxX, point.x)
                    maxY = max(maxY, point.y)
                }
            } else {
                if (drawingPath.paths.isNotEmpty()) {
                    drawingPath.paths.forEach { point ->
                        minX = min(minX, point.x)
                        minY = min(minY, point.y)
                        maxX = max(maxX, point.x)
                        maxY = max(maxY, point.y)
                    }
                } else if (!drawingPath.path.isEmpty) {
                    try {
                        pathMeasure.setPath(drawingPath.path, false)
                        val pathRect = Rect(
                            0f,
                            0f,
                            pathMeasure.length,
                            pathMeasure.length,
                        )
                    } catch (e: Exception) {
                    }
                }
            }
        }

        if (minX == Float.MAX_VALUE || drawingPaths.isEmpty()) {
            return@Canvas // No valid points found or empty drawing
        }

        val drawingWidth = maxX - minX
        val drawingHeight = maxY - minY

        if (drawingWidth <= 0 || drawingHeight <= 0) {
            return@Canvas // Not a valid drawing area
        }

        // 2. Calculate scale factor and offsets
        val canvasWidth = size.width
        val canvasHeight = size.height

        val scaleX = canvasWidth / drawingWidth
        val scaleY = canvasHeight / drawingHeight
        val scale = min(scaleX, scaleY) // Use min to fit and maintain aspect ratio

        // Calculate translation to center the scaled drawing
        val scaledDrawingWidth = drawingWidth * scale
        val scaledDrawingHeight = drawingHeight * scale

        val translateX = (canvasWidth - scaledDrawingWidth) / 2f - (minX * scale)
        val translateY = (canvasHeight - scaledDrawingHeight) / 2f - (minY * scale)

        // 3. Apply transformation and draw
        withTransform(
            {
                translate(left = translateX, top = translateY)
                scale(
                    scaleX = scale,
                    scaleY = scale,
                    pivot = Offset.Zero,
                ) // Scale around the original drawing's top-left
            },
        ) {
            // Draw existing paths with the transformation
            drawingPaths.forEach { drawingPath ->
                if (drawingPath.drawingProperties.isPen) {
                    val points = drawingPath.paths
                    if (points.size > 1) {
                        val maxStrokeWidthPx =
                            drawingPath.strokeWidth.width // Original stroke width
                        // IMPORTANT: Scale the stroke width as well!
                        val scaledStrokeWidth = max(1f, maxStrokeWidthPx * scale)

                        val taperSegments = 30
                        val minStrokeFactor = 0.1f

                        for (i in 0 until points.size - 1) {
                            val startPoint = points[i]
                            val endPoint = points[i + 1]
                            val segmentsFromEnd = points.size - 1 - i
                            val currentStrokeWidthPx = if (segmentsFromEnd <= taperSegments) {
                                val taperFactor =
                                    (segmentsFromEnd - 1).toFloat() / taperSegments.toFloat()
                                max(
                                    scaledStrokeWidth * minStrokeFactor,
                                    scaledStrokeWidth * taperFactor,
                                )
                            } else {
                                scaledStrokeWidth
                            }
                            val finalWidth =
                                max(0.5f, currentStrokeWidthPx) // Ensure min width after scaling

                            drawLine(
                                color = drawingPath.color,
                                start = startPoint,
                                end = endPoint,
                                strokeWidth = finalWidth, // Use scaled and tapered width
                                cap = drawingPath.strokeWidth.cap,
                            )
                        }
                    }
                } else {
                    // IMPORTANT: Scale the stroke width for non-pen paths too!
                    val originalStroke = drawingPath.strokeWidth
                    val scaledStroke = Stroke(
                        width = max(1f, originalStroke.width * scale), // Scale width
                        miter = originalStroke.miter,
                        cap = originalStroke.cap,
                        join = originalStroke.join,
                        pathEffect = originalStroke.pathEffect, // PathEffect might also need scaling depending on its nature
                    )
                    drawPath(
                        path = drawingPath.path,
                        color = drawingPath.color,
                        style = scaledStroke, // Use the new scaled stroke
                    )
                }
            }
        }
    }
}
