package com.mshdabiola.gallery

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val galleryRoute = "gallery_route"
const val noteIdStr = "noteId"
const val currentId = "currentImageIndex"

fun NavGraphBuilder.galleryScreen(
    onBack: () -> Unit,
    navigateToEditScreen: (Long, String, Long) -> Unit,
) {
    composable(
        route = "$galleryRoute?$noteIdStr={$noteIdStr}?$currentId={$currentId}",
        arguments = listOf(
            navArgument(noteIdStr) {
                type = NavType.LongType
            },
            navArgument(currentId) {
                type = NavType.LongType
            },
        ),
    ) {
        GalleryScreen(
            onBack = onBack,
            navigateToEditScreen = navigateToEditScreen,
        )
    }
}

fun NavController.navigateToGallery(noteId: Long, currentIndex: Long) {
    navigate(
        route = "$galleryRoute?$noteIdStr=$noteId?$currentId=$currentIndex",
    )
}
