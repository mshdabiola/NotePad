package com.mshdabiola.mainscreen.state

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

data class MainState(
    val noteType: NoteType = NoteType.NOTE,
    val notePads: ImmutableList<NotePadUiState> = emptyList<NotePadUiState>().toImmutableList()
)