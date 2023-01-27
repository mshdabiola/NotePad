package com.mshdabiola.labelscreen

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

data class LabelScreenUiState(
    val labels: ImmutableList<LabelUiState> = emptyList<LabelUiState>().toImmutableList(),
    val editText: String = "",
    val isEditMode: Boolean = false,
    val errorOccur: Boolean = false,
)
