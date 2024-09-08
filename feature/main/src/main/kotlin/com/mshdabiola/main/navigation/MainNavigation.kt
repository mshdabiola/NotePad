/*
 *abiola 2022
 */

package com.mshdabiola.main.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.mshdabiola.main.MainRoute

fun NavController.navigateToMain(main: Main, navOptions: NavOptions = androidx.navigation.navOptions { }) = navigate(main, navOptions)

@OptIn(ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.mainScreen(
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope,
    onShowSnack: suspend (String, String?) -> Boolean,
    navigateToDetail: (Long) -> Unit,
    navigateToSelectLevel: (Set<Long>) -> Unit,
    onOpenDrawer: () -> Unit,
) {
    composable<Main> {
        MainRoute(
            modifier = modifier,
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = this,
            onShowSnackbar = onShowSnack,
            navigateToDetail = navigateToDetail,
            navigateToSelectLevel = navigateToSelectLevel,
            onOpenDrawer = onOpenDrawer,
        )
    }
}

//
//
// internal class MainArg(val id: Long) {
//    constructor(savedStateHandle: SavedStateHandle) :
//            this(
//                id = checkNotNull(savedStateHandle[noteId]),
//                content = checkNotNull(savedStateHandle[contentId]),
//                data = checkNotNull(savedStateHandle[dataId]),
//            )
//
//    companion object {
//        fun decode(string: String): Uri {
//            return Uri.decode(string).toUri()
//        }
//    }
// }
//
