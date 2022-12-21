package com.mshdabiola.xnotepad.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.mshdabiola.editscreen.editScreen
import com.mshdabiola.mainscreen.mainNavigationRoute
import com.mshdabiola.mainscreen.mainScreen


@Composable
fun NotePadAppNavHost(
    navController: NavHostController,
    navigateToMain: () -> Unit,
    navigateToEdit: (Long, String, Uri) -> Unit,
    onBack: () -> Unit,
    startDestination: String = mainNavigationRoute
) {
    NavHost(navController = navController, startDestination = startDestination) {
        mainScreen(navigateToEditScreen = navigateToEdit)
        editScreen(onBack = onBack)
    }
}