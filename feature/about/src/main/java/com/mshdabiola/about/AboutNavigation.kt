package com.mshdabiola.about

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.aboutScreen(onBack: () -> Unit) {
    composable<AboutArg> {
        AboutScreen(onBack = onBack)
    }
}

fun NavController.navigateToAbout() {
    navigate(AboutArg)
}
