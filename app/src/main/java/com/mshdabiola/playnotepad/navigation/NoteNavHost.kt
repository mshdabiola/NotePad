/*
 *abiola 2022
 */

package com.mshdabiola.playnotepad.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.mshdabiola.mainscreen.mainNavigationRoute
import com.mshdabiola.playnotepad.ui.NoteAppState

@Composable
fun NoteNavHost(
    appState: NoteAppState,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier,
    startDestination: String = mainNavigationRoute,
) {
    val navController = appState.navController
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
    }
}
