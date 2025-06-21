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
import com.mshdabiola.search.SearchScreen
import com.mshdabiola.search.SearchViewModel
import com.mshdabiola.ui.FirebaseScreenLog

fun NavController.navigateToSearch(
    navOptions: NavOptions = navOptions { },
) = navigate(route = SearchRoute, navOptions)

const val SearchRoute = "search"

@OptIn(ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.search(
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope,
    onBack: () -> Unit,
    navigateToDetail: (Long, Int, Int) -> Unit = { _, _, _ -> },

) {
    composable(
        route = SearchRoute,
    ) {
        FirebaseScreenLog(screen = "main_screen")
        val searchViewModel: SearchViewModel = hiltViewModel()
        val searchState = searchViewModel.searchState.collectAsStateWithLifecycle()

        sharedTransitionScope.SearchScreen(
            modifier = modifier,
            animatedContentScope = this,
            searchState = searchState.value,
            searchQuery = searchViewModel.searchQuery,
            onBack = onBack,
            onSetSearch = searchViewModel::onSetSearch,
            onNoteClick = navigateToDetail,

        )
    }
}
