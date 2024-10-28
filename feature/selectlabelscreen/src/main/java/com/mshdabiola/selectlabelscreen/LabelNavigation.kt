package com.mshdabiola.selectlabelscreen

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

fun NavGraphBuilder.selectLabelScreen(onBack: () -> Unit) {
    composable<LabelsArgs> {
        LabelScreen(onBack)
    }
}

fun NavController.navigateToSelectLabel(ids: Set<Long>) {
    navigate(LabelsArgs(ids.joinToString()))
}

@Serializable
data class LabelsArgs(val ids: String)
