/*
 *abiola 2022
 */

package com.mshdabiola.detail.navigation

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mshdabiola.detail.DetailRoute

const val DETAIL_ROUTE = "detail_route"

private val URL_CHARACTER_ENCODING = Charsets.UTF_8.name()

@VisibleForTesting
internal const val DETAIL_ID_ARG = "topicId"

internal class DetailArgs(val id: Long) {
    constructor(savedStateHandle: SavedStateHandle) : this(checkNotNull<Long>(savedStateHandle[DETAIL_ID_ARG]))
    // this(URLDecoder.decode(checkNotNull(savedStateHandle[DETAIL_ID_ARG]), URL_CHARACTER_ENCODING))
}

fun NavController.navigateToDetail(topicId: Long) {
    // val encodedId = URLEncoder.encode(topicId, URL_CHARACTER_ENCODING)
    navigate("$DETAIL_ROUTE/$topicId") {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.detailScreen(
    onShowSnackbar: suspend (String, String?) -> Boolean,
    onBack: () -> Unit,
) {
    composable(
        route = "$DETAIL_ROUTE/{$DETAIL_ID_ARG}",
        arguments = listOf(
            navArgument(DETAIL_ID_ARG) {
                type = NavType.LongType
            },
        ),
//        enterTransition = {
//            slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Left)
//        },
//        exitTransition = {
//            slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Right)
//        },
    ) {
        DetailRoute(onShowSnackbar, onBack)
    }
}
