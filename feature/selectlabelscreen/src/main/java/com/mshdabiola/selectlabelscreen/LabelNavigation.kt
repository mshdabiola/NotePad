package com.mshdabiola.selectlabelscreen

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mshdabiola.ui.FirebaseScreenLog
import kotlinx.serialization.Serializable

fun NavGraphBuilder.selectLabelScreen(onBack: () -> Unit) {
    composable<LabelsArgs> {
        FirebaseScreenLog(screen = "select_label_screen")
        val viewModel = hiltViewModel<LabelViewModel>()
        val uiState = viewModel.labelUiState.collectAsStateWithLifecycle()

        LabelScreen(
            labelUiState = uiState.value,
            onCheckClick = viewModel::onCheckClick,
            onCreateLabel = viewModel::onCreateLabel,
            onBack = onBack,
        )
    }
}

fun NavController.navigateToSelectLabel(ids: Set<Long>) {
    navigate(LabelsArgs(ids.joinToString()))
}

@Serializable
data class LabelsArgs(val ids: String)
