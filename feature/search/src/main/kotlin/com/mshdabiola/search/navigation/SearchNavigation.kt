/*
 *abiola 2022
 */

package com.mshdabiola.search.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import com.mshdabiola.search.SearchViewModel
import com.mshdabiola.ui.FirebaseScreenLog

fun NavController.navigateToMain(
    navOptions: NavOptions = navOptions { },
) = navigate(route = MainRoute, navOptions)

const val MainRoute = "search"
const val FullMainRoute = MainRoute

@OptIn(ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.mainScreen(
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope,
    onShowSnack: suspend (String, String?) -> Boolean,
    navigateToDetail: (Long) -> Unit,
    navigateToSelectLevel: (Set<Long>) -> Unit,
    onOpenDrawer: () -> Unit,
) {
    composable(
        route = FullMainRoute,
    ) {
        FirebaseScreenLog(screen = "main_screen")
        val searchViewModel: SearchViewModel = hiltViewModel()
        val searchState = searchViewModel.searchState.collectAsStateWithLifecycle()
    }
}
