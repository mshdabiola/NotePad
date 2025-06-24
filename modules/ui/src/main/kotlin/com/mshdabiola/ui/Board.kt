package com.mshdabiola.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
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

                        // Draw each segment as a line
                        // For smoother curves with varying width, you'd typically draw quadratic/cubic Beziers
                        // between triplets/quadruplets of points, adjusting control points and width.
                        // For simplicity, we draw lines here.
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

@Composable
fun BoardViewer(
    modifier: Modifier = Modifier,
    drawingPaths: List<DrawingPath>,
) {
    Canvas(
        modifier = modifier
            .graphicsLayer {
                clip = true
            },
    ) {
        // Draw existing paths
        drawingPaths.forEach { drawingPath ->

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
    }
}
