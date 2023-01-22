package com.mshdabiola.editscreen

import com.mshdabiola.model.Label

data class LabelUiState(
    val id: Long,
    val label: String,
    val isCheck: Boolean = false
)

fun Label.toLabelUiState() = LabelUiState(this.id, this.label)
