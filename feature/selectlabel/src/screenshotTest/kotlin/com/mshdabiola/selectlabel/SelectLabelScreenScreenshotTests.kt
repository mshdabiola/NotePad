/*
 *abiola 2023
 */

package com.mshdabiola.selectlabel

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.state.ToggleableState
import com.mshdabiola.ui.PreviewContainer
import com.mshdabiola.ui.PreviewMain

class SelectLabelScreenScreenshotTests {
    @PreviewMain
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Main() {
        val selectLabelUiState = SelectLabelUiState(
            labels = listOf(
                LabelState(1, "label1", ToggleableState.On),
                LabelState(2, "label2", ToggleableState.Off),
                LabelState(3, "label3", ToggleableState.Indeterminate),
                LabelState(4, "label4", ToggleableState.On),
                LabelState(5, "label5", ToggleableState.Off),
                LabelState(6, "label6", ToggleableState.Indeterminate),
            ),
            labelQuery = TextFieldState(""),
            showAddLabel = false,
        )
        PreviewContainer {
            SelectLabelScreen(selectLabelUiState = selectLabelUiState)
        }
    }
}
