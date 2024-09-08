/*
 *abiola 2022
 */

package com.mshdabiola.detail.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.mshdabiola.detail.DetailRoute

fun NavController.navigateToDetail(detailArg: DetailArg, navOptions: NavOptions = androidx.navigation.navOptions { }) = navigate(detailArg, navOptions)

fun NavGraphBuilder.detailScreen(
    onShowSnackbar: suspend (String, String?) -> Boolean,
    onBack: () -> Unit,
    navigateToGallery: (Long) -> Unit,
    navigateToDrawing: (Long) -> Unit,
    navigateToSelectLevel: (Set<Long>) -> Unit,
) {
    composable<DetailArg> {
        DetailRoute(
            onShowSnackbar = onShowSnackbar,
            onBack = onBack,
            navigateToGallery = navigateToGallery,
            navigateToDrawing = navigateToDrawing,
            navigateToSelectLevel = navigateToSelectLevel,
        )
    }
}
