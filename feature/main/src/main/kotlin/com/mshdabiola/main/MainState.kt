package com.mshdabiola.main

import com.mshdabiola.ui.state.NotePadUiState
import com.mshdabiola.ui.state.NoteTypeUi
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

sealed class MainState {
    data object Loading : MainState()
    data class Success(
        val noteType: NoteTypeUi = NoteTypeUi(),
        val notePads: ImmutableList<NotePadUiState> = emptyList<NotePadUiState>().toImmutableList(),
    ) : MainState()

//    data class Error(val message: String) : MainStateN()
    data object Empty : MainState()
    data class Finish(val id: Long) : MainState()
}
