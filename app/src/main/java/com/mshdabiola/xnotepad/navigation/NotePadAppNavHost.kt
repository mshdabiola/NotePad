package com.mshdabiola.xnotepad.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.mshdabiola.editscreen.editScreen
import com.mshdabiola.labelscreen.labelScreen
import com.mshdabiola.mainscreen.mainNavigationRoute
import com.mshdabiola.mainscreen.mainScreen


@Composable
fun NotePadAppNavHost(
    navController: NavHostController,
    navigateToMain: () -> Unit,
    navigateToEdit: (Long, String, Long) -> Unit,
    navigateToLevel: (Boolean) -> Unit,
    onBack: () -> Unit,
    startDestination: String = mainNavigationRoute
) {
    NavHost(navController = navController, startDestination = startDestination) {
        mainScreen(
            navigateToEditScreen = navigateToEdit,
            navigateToLevel
        )
        editScreen(onBack = onBack)
        labelScreen(onBack = onBack)
    }
}