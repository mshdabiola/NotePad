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
const val uriId = "uriId"
const val editDestinationRoute = "edit_screen_route"

internal class EditArg(val id: Long, val content: String, val uri: Uri) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(
                id = checkNotNull(savedStateHandle[noteId]),
                content = checkNotNull(savedStateHandle[contentId]),
                uri = decode(checkNotNull(savedStateHandle[uriId]))
            )

    companion object {
        fun decode(string: String): Uri {
            return Uri.decode(string).toUri()

        }
    }
}

fun NavController.navigateToEditScreen(id: Long, content: String = "", uri: Uri = Uri.EMPTY) {
    val encodeUri = Uri.encode(uri.toString())
    val encodeString = Uri.encode(content)
    navigate(route = "$editDestinationRoute?$noteId=$id?$contentId=$encodeString?$uriId=$encodeUri")
}

fun NavGraphBuilder.editScreen() {
    composable(
        route = "$editDestinationRoute?$noteId={$noteId}?$contentId={$contentId}?$uriId={$uriId}",
        arguments = listOf(
            navArgument(noteId) {
                type = NavType.LongType
            }
        )
    ) {
        EditScreen()
    }
}