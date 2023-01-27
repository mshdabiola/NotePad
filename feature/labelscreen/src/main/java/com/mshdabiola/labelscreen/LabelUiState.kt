package com.mshdabiola.labelscreen

import com.mshdabiola.model.Label

data class LabelUiState(
    val id: Long,
    val label: String,
)

fun Label.toLabelUiState() = LabelUiState(this.id, this.label)
fun LabelUiState.toLabel() = Label(id, label)
