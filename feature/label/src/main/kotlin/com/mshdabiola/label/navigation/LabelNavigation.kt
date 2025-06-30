package com.mshdabiola.labelscreen

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.label(onBack: () -> Unit) {
    composable<LabelArg> {
        val labelViewModel = hiltViewModel<LabelViewModel>()
        val labelUiState = labelViewModel.labelUiState.collectAsStateWithLifecycle()

        LabelScreen(
            labelUiState = labelUiState.value,
            onBack = onBack,
            onDelete = labelViewModel::onDelete,
            onAdd = labelViewModel::onAddNew,
        )
    }
}

fun NavController.navigateToLabel(editMode: Boolean) {
    navigate(LabelArg(editMode))
}
