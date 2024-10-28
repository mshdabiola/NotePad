/*
 *abiola 2022
 */

package com.mshdabiola.detail.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.mshdabiola.detail.DetailRoute

fun NavController.navigateToDetail(detailArg: DetailArg, navOptions: NavOptions = androidx.navigation.navOptions { }) = navigate(detailArg, navOptions)

@OptIn(ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.detailScreen(
    onShowSnackbar: suspend (String, String?) -> Boolean,
    onBack: () -> Unit,
    navigateToGallery: (Long) -> Unit,
    navigateToDrawing: (Long, Long) -> Unit,
    navigateToSelectLevel: (Set<Long>) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    modifier: Modifier.Companion,
) {
    composable<DetailArg> {
        DetailRoute(
            onShowSnackbar = onShowSnackbar,
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = this,
            onBack = onBack,
            navigateToGallery = navigateToGallery,
            navigateToDrawing = navigateToDrawing,
            navigateToSelectLevel = navigateToSelectLevel,
        )
    }
}
