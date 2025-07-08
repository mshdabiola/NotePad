package com.mshdabiola.labelscreen

import androidx.compose.foundation.text.input.TextFieldState
import com.mshdabiola.model.Label

data class LabelState(
    val id: Long = -1,
    val label: TextFieldState = TextFieldState(),
)

fun Label.toLabelState() = LabelState(this.id, TextFieldState(this.name))
fun LabelState.toLabel() = Label(id, label.text.toString())

data class LabelUiState(
    val labels: List<LabelState> = emptyList(),
    val newLabel: LabelState = LabelState(-1, TextFieldState()),
    val isEditMode: Boolean = false,
)
