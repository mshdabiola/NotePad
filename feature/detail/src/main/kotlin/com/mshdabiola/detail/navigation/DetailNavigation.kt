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
) {
    composable<DetailArg> {
        DetailRoute(onShowSnackbar, onBack)
    }
}
