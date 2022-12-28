package com.mshdabiola.labelscreen

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val labelRoute = "label_route"
const val editModeArg = "edit_arg"
fun NavGraphBuilder.labelScreen(onBack: () -> Unit) {
    composable(
        route = "$labelRoute?$editModeArg={$editModeArg}",
        arguments = listOf(navArgument(editModeArg) {
            this.type = NavType.BoolType
            defaultValue = false
        })
    ) {
        LabelScreen(onBack = onBack)
    }
}

fun NavController.navigateToLabel(editMode: Boolean) {
    navigate(route = "$labelRoute?$editModeArg=$editMode")
}

data class LabelArg(val editMode: Boolean) {
    constructor(savedStateHandle: SavedStateHandle)
            : this(savedStateHandle.get<Boolean>(editModeArg) ?: false)
}