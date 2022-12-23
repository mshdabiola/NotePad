package com.mshdabiola.editscreen

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val noteId = "noteId"
const val contentId = "contentId"
const val dataId = "uriId"
const val editDestinationRoute = "edit_screen_route"

internal class EditArg(val id: Long, val content: String, val data: Long) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(
                id = checkNotNull(savedStateHandle[noteId]),
                content = checkNotNull(savedStateHandle[contentId]),
                data = checkNotNull(savedStateHandle[dataId])
            )

    companion object {
        fun decode(string: String): Uri {
            return Uri.decode(string).toUri()

        }
    }
}

fun NavController.navigateToEditScreen(id: Long, content: String = "", data: Long = 0) {
    //val encodeUri = Uri.encode(uri.toString())
    // val encodeString = Uri.encode(content)
    navigate(route = "$editDestinationRoute?$noteId=$id?$contentId=$content?$dataId=$data")
}

fun NavGraphBuilder.editScreen(onBack: () -> Unit) {
    composable(
        route = "$editDestinationRoute?$noteId={$noteId}?$contentId={$contentId}?$dataId={$dataId}",
        arguments = listOf(
            navArgument(noteId) {
                type = NavType.LongType
            },
            navArgument(contentId) {
                type = NavType.StringType
            },
            navArgument(dataId) {
                type = NavType.LongType
            }
        )
    ) {
        EditScreen(onBack = onBack)
    }
}