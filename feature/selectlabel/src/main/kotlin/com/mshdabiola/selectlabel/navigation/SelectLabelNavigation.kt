package com.mshdabiola.selectlabel.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import com.mshdabiola.selectlabel.SelectLabelScreen
import com.mshdabiola.selectlabel.SelectLabelViewModel
import com.mshdabiola.ui.FirebaseScreenLog
import kotlinx.serialization.Serializable

fun EntryProviderBuilder<NavKey>.selectLabelScreen(onBack: () -> Unit) {
    entry<SelectLabelsArgs> { key ->
        FirebaseScreenLog(screen = "select_label_screen")
        val viewModel = hiltViewModel<SelectLabelViewModel, SelectLabelViewModel.Factory>(
            creationCallback = { factory -> factory.create(key) },
        )
        val uiState = viewModel.selectLabelUiState.collectAsStateWithLifecycle()

        SelectLabelScreen(
            selectLabelUiState = uiState.value,
            onCheckClick = viewModel::onCheckClick,
            onCreateLabel = viewModel::onCreateLabel,
            onBack = onBack,
        )
    }
}

fun NavBackStack.navigateToSelectLabel(ids: Set<Long>) {
    add(SelectLabelsArgs(ids.joinToString()))
}

@Serializable
data class SelectLabelsArgs(val ids: String) : NavKey
