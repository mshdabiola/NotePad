package com.mshdabiola.editscreen

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val parameterId = "noteId"
const val editDestinationRoute = "edit_screen_route"

internal class EditArg(val id: Long) {
    constructor(savedStateHandle: SavedStateHandle) : this(id = checkNotNull(savedStateHandle[parameterId]))

    companion object {
        fun decode(string: String): Long {
            val str = Uri.decode(string)
            return str.toLong()
        }
    }
}

fun NavController.navigateToEditScreen(id: Long) {
    //val encode= Uri.encode(id.toString())
    navigate(route = "$editDestinationRoute?$parameterId=$id")
}

fun NavGraphBuilder.editScreen() {
    composable(
        route = "$editDestinationRoute?$parameterId={$parameterId}",
        arguments = listOf(
            navArgument(parameterId) {
                type = NavType.LongType
            }
        )
    ) {
        EditScreen()
    }
}