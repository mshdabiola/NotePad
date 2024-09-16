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

fun NavController.navigateToDrawing(imageId: Long) {
    navigate(DrawingArgs(imageId))
}

@Serializable
data class DrawingArgs(val imageId: Long)
