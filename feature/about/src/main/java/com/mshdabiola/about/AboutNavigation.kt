package com.mshdabiola.about

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val AboutRoute = "About_route"

fun NavGraphBuilder.aboutScreen(onBack: () -> Unit) {
    composable(
        route = AboutRoute,
    ) {
        AboutScreen(onBack = onBack)
    }
}

fun NavController.navigateToAbout() {
    navigate(route = AboutRoute)
}
