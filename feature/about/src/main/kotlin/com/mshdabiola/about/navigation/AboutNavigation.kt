package com.mshdabiola.about.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mshdabiola.about.AboutScreen

fun NavGraphBuilder.aboutScreen(onBack: () -> Unit) {
    composable<AboutArg> {
        AboutScreen(onBack = onBack)
    }
}

fun NavController.navigateToAbout() {
    navigate(AboutArg)
}
