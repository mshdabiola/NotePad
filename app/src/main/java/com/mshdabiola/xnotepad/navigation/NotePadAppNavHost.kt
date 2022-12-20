package com.mshdabiola.xnotepad.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.mshdabiola.editscreen.editScreen
import com.mshdabiola.editscreen.navigateToEditScreen
import com.mshdabiola.mainscreen.mainNavigationRoute
import com.mshdabiola.mainscreen.mainScreen


@Composable
fun NotePadAppNavHost(
    navController: NavHostController,
    startDestination: String = mainNavigationRoute
) {
    NavHost(navController = navController, startDestination = startDestination) {
        mainScreen(navigateToEditScreen = { id, content, uri ->
            navController.navigateToEditScreen(
                id,
                content,
                uri
            )
        })
        editScreen()
    }
}