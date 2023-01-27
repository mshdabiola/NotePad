package com.mshdabiola.selectlabelscreen

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val labelRoute = "select_route"
const val labelArg = "select_args"

fun NavGraphBuilder.selectLabelScreen(onBack: () -> Unit) {
    composable(
        route = "$labelRoute?$labelArg={$labelArg}",
        arguments = listOf(
            navArgument(labelArg) {
                type = NavType.StringType
            },
        ),
    ) {
        LabelScreen(onBack)
    }
}

fun NavController.navigateToSelectLabel(ids: IntArray) {
    Log.e(this::class.simpleName, ids.joinToString())
    navigate(route = "$labelRoute?$labelArg=${ids.joinToString()}")
}

data class LabelsArgs(val ids: Set<Long>) {
    constructor(savedStateHandle: SavedStateHandle) : this(decode(savedStateHandle[labelArg] ?: ""))

    companion object {
        fun decode(string: String): Set<Long> {
            return string.split(",").map { it.trim().toLong() }.toSet()
        }
    }
}
