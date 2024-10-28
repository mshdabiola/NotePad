/*
 *abiola 2022
 */

package com.mshdabiola.main.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mshdabiola.main.MainRoute
import com.mshdabiola.model.NoteType

fun NavController.navigateToMain(
    type: Long,
    navOptions: NavOptions = androidx.navigation.navOptions { },
) = navigate(route = "$MainRoute/$type", navOptions)

const val MainRoute = "main"
const val TypeArg = "mainArg"
const val FullMainRoute = "$MainRoute/{$TypeArg}"

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
        arguments = listOf(
            navArgument(TypeArg) {
                type = NavType.LongType
                defaultValue = -1L
            },
        ),
    ) {
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
internal class MainArg(val type: Long) {
    val noteType: NoteType = when (type) {
        NoteType.NOTE.index -> NoteType.NOTE
        NoteType.ARCHIVE.index -> NoteType.ARCHIVE
        NoteType.TRASH.index -> NoteType.TRASH
        NoteType.REMAINDER.index -> NoteType.REMAINDER
        else -> NoteType.LABEL
    }

    constructor(savedStateHandle: SavedStateHandle) :
        this(
            type = checkNotNull(savedStateHandle[TypeArg]),

        )
}
//
