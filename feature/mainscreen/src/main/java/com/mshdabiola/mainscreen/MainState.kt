package com.mshdabiola.mainscreen

import com.mshdabiola.designsystem.component.state.LabelUiState
import com.mshdabiola.designsystem.component.state.NotePadUiState
import com.mshdabiola.designsystem.component.state.NoteType
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

data class MainState(
    val noteType: NoteType = NoteType.NOTE,
    val notePads: ImmutableList<NotePadUiState> = emptyList<NotePadUiState>().toImmutableList(),
    val labels: ImmutableList<LabelUiState> = emptyList<LabelUiState>().toImmutableList()
)