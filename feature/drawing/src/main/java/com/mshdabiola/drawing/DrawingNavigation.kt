package com.mshdabiola.drawing


import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val drawingRoute = "drawing_route"
const val noteIdArg = "note_id_arg"

fun NavGraphBuilder.drawingScreen(onBack: () -> Unit) {
    composable(
        route = "$drawingRoute?$noteIdArg={$noteIdArg}",
        arguments = listOf(
            navArgument(noteIdArg) {
                type = NavType.LongType
            }
        )
    ) {
        DrawingScreen(onBack = onBack)


    }
}

fun NavController.navigateToDrawing(noteId: Long) {
    navigate(route = "$drawingRoute?$noteIdArg=$noteId")
}
