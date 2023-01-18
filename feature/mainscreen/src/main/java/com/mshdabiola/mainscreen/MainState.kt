package com.mshdabiola.mainscreen

import com.mshdabiola.designsystem.component.state.LabelUiState
import com.mshdabiola.designsystem.component.state.NotePadUiState
import com.mshdabiola.designsystem.component.state.NoteTypeUi
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

data class MainState(
    val noteType: NoteTypeUi = NoteTypeUi.NOTE,
    val notePads: ImmutableList<NotePadUiState> = emptyList<NotePadUiState>().toImmutableList(),
    val labels: ImmutableList<LabelUiState> = emptyList<LabelUiState>().toImmutableList(),
    val message: ImmutableList<String> = emptyList<String>().toImmutableList()
)