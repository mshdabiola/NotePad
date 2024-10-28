package com.mshdabiola.labelscreen

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.labelScreen(onBack: () -> Unit) {
    composable<LabelArg> {
        LabelScreen(onBack = onBack)
    }
}

fun NavController.navigateToLabel(editMode: Boolean) {
    navigate(LabelArg(editMode))
}
