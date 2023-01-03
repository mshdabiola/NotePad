package com.mshdabiola.searchscreen


import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val searchRoute = "search_route"

fun NavGraphBuilder.searchScreen(
    onBack: () -> Unit,
    navigateToEdit: (Long, String, Long) -> Unit
) {
    composable(
        route = searchRoute
    ) {
        SearchScreen(onBack = onBack, navigateToEdit)
    }
}

fun NavController.navigateToSearch() {
    navigate(route = searchRoute)
}
