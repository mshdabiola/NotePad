package com.mshdabiola.mainscreen

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val mainNavigationRoute = "main_route"

fun NavController.navigateToMain() {
    navigate(mainNavigationRoute)
}

fun NavGraphBuilder.mainScreen(
    navigateToEditScreen: (Long, String, Long) -> Unit,
    navigateToLevel: (Boolean) -> Unit,
    navigateToSearch: () -> Unit,
    navigateToSelectLevel: (IntArray) -> Unit,
    navigateToAbout: () -> Unit
) {
    composable(route = mainNavigationRoute) {
        MainScreen(
            navigateToEdit = navigateToEditScreen,
            navigateToLevel = navigateToLevel,
            navigateToSearch = navigateToSearch,
            navigateToSelectLevel = navigateToSelectLevel,
            navigateToAbout = navigateToAbout
        )
    }
}