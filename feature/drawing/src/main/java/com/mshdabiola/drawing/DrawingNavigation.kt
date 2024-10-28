package com.mshdabiola.drawing

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

fun NavGraphBuilder.drawingScreen(
    onBack: () -> Unit,
) {
    composable<DrawingArgs> {
        DrawingScreen(onBack = onBack)
    }
}

fun NavController.navigateToDrawing(noteId: Long, imageId: Long) {
    navigate(DrawingArgs(noteId, imageId))
}

@Serializable
data class DrawingArgs(val noteId: Long, val imageId: Long)
