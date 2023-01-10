package com.mshdabiola.drawing


import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val drawingRoute = "drawing_route"

fun NavGraphBuilder.drawingScreen(onBack: () -> Unit) {
    composable(
        route = drawingRoute
    ) {
        DrawingScreen(onBack = onBack)
    }
}

fun NavController.navigateToDrawing(noteId: Long) {
    navigate(route = drawingRoute)
}
