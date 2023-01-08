package com.mshdabiola.xnotepad.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.mshdabiola.editscreen.editScreen
import com.mshdabiola.labelscreen.labelScreen
import com.mshdabiola.mainscreen.mainNavigationRoute
import com.mshdabiola.mainscreen.mainScreen
import com.mshdabiola.searchscreen.searchScreen
import com.mshdabiola.selectlabelscreen.selectLabelScreen


@Composable
fun NotePadAppNavHost(
    navController: NavHostController,
    navigateToMain: () -> Unit,
    navigateToEdit: (Long, String, Long) -> Unit,
    navigateToLevel: (Boolean) -> Unit,
    navigateToSearch: () -> Unit,
    onBack: () -> Unit,
    startDestination: String = mainNavigationRoute,
    navigateToSelectLevel: (IntArray) -> Unit
) {
    NavHost(navController = navController, startDestination = startDestination) {
        mainScreen(
            navigateToEditScreen = navigateToEdit,
            navigateToLevel = navigateToLevel,
            navigateToSearch = navigateToSearch,
            navigateToSelectLevel = navigateToSelectLevel
        )
        editScreen(onBack = onBack, navigateToSelectLevel = navigateToSelectLevel)
        labelScreen(onBack = onBack)
        selectLabelScreen(onBack)
        searchScreen(onBack, navigateToEdit)
    }
}