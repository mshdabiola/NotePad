package com.mshdabiola.selectlabelscreen

import androidx.compose.ui.state.ToggleableState
import com.mshdabiola.model.Label

data class LabelUiState(
    val id: Long,
    val label: String,
    val toggleableState: ToggleableState = ToggleableState.Off
)

fun Label.toLabelUiState() = LabelUiState(this.id, this.label)
fun LabelUiState.toLabel() = Label(id, label)
