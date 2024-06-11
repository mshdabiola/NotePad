package com.mshdabiola.drawing

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val drawingRoute = "drawing_route"
const val noteIdArg = "note_id_arg"
const val imageIdArg = "image_id_arg"

fun NavGraphBuilder.drawingScreen(
    onBack: () -> Unit,
    saveImage: (Long, Long) -> Unit,

) {
    composable(
        route = "$drawingRoute?$noteIdArg={$noteIdArg}?$imageIdArg={$imageIdArg}",
        arguments = listOf(
            navArgument(noteIdArg) {
                type = NavType.LongType
            },
            navArgument(imageIdArg) {
                type = NavType.LongType
            },
        ),
    ) {
        DrawingScreen(onBack = onBack, saveImage = saveImage)
    }
}

fun NavController.navigateToDrawing(noteId: Long, imageId: Long?) {
    val id = imageId ?: -1
    navigate(route = "$drawingRoute?$noteIdArg=$noteId?$imageIdArg=$id")
}
