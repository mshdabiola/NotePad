package com.mshdabiola.selectlabelscreen

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.state.ToggleableState
import com.mshdabiola.model.Label

data class LabelUiState(
    val labels: List<LabelState> = emptyList(),
    val labelQuery: TextFieldState = TextFieldState(""),
    val showAddLabel: Boolean = false,
)

data class LabelState(
    val id: Long = -1,
    val label: String = "",
    val toggleableState: ToggleableState = ToggleableState.Off,
)

fun Label.toLabelState() = LabelState(id = this.id, label = this.label)
