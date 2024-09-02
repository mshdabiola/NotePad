/*
 *abiola 2022
 */

package com.mshdabiola.playnotepad.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.mshdabiola.detail.navigation.DetailArg
import com.mshdabiola.detail.navigation.detailScreen
import com.mshdabiola.detail.navigation.navigateToDetail
import com.mshdabiola.main.navigation.Main
import com.mshdabiola.main.navigation.mainScreen
import com.mshdabiola.playnotepad.ui.NoteAppState

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun NoteNavHost(
    appState: NoteAppState,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier,
) {
    val navController = appState.navController
    SharedTransitionLayout(modifier = modifier) {
        NavHost(
            navController = navController,
            startDestination = Main,
            modifier = Modifier,
        ) {
            mainScreen(
                modifier = Modifier,
                sharedTransitionScope = this@SharedTransitionLayout,
                onShowSnack = onShowSnackbar,
                navigateToDetail = { navController.navigateToDetail(DetailArg(it)) },
            )
            detailScreen(
                //  modifier = Modifier,
                //  sharedTransitionScope = this@SharedTransitionLayout,
                onShowSnackbar = onShowSnackbar,
                onBack = navController::popBackStack,
            )
        }
    }
}
