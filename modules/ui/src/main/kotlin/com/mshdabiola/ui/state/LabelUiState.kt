package com.mshdabiola.ui.state

import com.mshdabiola.model.Label

data class LabelUiState(
    val id: Long,
    val label: String,
)

fun Label.toLabelUiState() = LabelUiState(id, label)
