package com.mshdabiola.mainscreen

import com.mshdabiola.designsystem.component.state.LabelUiState
import com.mshdabiola.designsystem.component.state.NotePadUiState
import com.mshdabiola.designsystem.component.state.NoteTypeUi
import com.mshdabiola.designsystem.component.state.Notify
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

data class MainState(
    val noteType: NoteTypeUi = NoteTypeUi(),
    val notePads: ImmutableList<NotePadUiState> = emptyList<NotePadUiState>().toImmutableList(),
    val labels: ImmutableList<LabelUiState> = emptyList<LabelUiState>().toImmutableList(),
    val messages: ImmutableList<Notify> = emptyList<Notify>().toImmutableList(),
    val isGrid: Boolean = true
)