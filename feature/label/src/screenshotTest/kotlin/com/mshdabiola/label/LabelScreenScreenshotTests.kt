/*
 *abiola 2023
 */

package com.mshdabiola.label

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import com.mshdabiola.labelscreen.LabelState
import com.mshdabiola.labelscreen.LabelUiState
import com.mshdabiola.testing.util.PreviewAllLocales
import com.mshdabiola.ui.PreviewContainer

@OptIn(ExperimentalSharedTransitionApi::class)
@PreviewAllLocales
@Composable
fun LabelScreenShot() {
    val labelUiState = LabelUiState(
        labels = listOf(
            LabelState(1, TextFieldState("Java")),
            LabelState(2, TextFieldState("Kotlin")),
            LabelState(3, TextFieldState("Python")),
            LabelState(4, TextFieldState("C sharper")),
            LabelState(5, TextFieldState("JavaScript")),

        ),
        newLabel = LabelState(-1, TextFieldState("new")),
        isEditMode = false,
    )
    PreviewContainer {
        LabelScreen(labelUiState = labelUiState, onBack = {}, onDelete = {}, onAdd = {})
    }
}
