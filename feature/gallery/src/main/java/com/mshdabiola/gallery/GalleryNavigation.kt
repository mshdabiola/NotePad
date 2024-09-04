package com.mshdabiola.gallery

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.galleryScreen(
    onBack: () -> Unit,
) {
    composable<GalleryArg> {
        GalleryScreen(
            onBack = onBack,
        )
    }
}

fun NavController.navigateToGallery(galleryArg: GalleryArg) {
    navigate(galleryArg)
}
