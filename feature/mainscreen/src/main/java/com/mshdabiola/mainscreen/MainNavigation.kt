package com.mshdabiola.mainscreen

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val mainNavigationRoute = "main_route"

fun NavGraphBuilder.mainScreen() {
    composable(route = mainNavigationRoute) {
        MainScreen()
    }
}