package com.mshdabiola.main

import com.mshdabiola.model.NotePad
import com.mshdabiola.model.NoteType

sealed class MainState {
    data object Loading : MainState()
    data class Success(
        val noteType: NoteType = NoteType.NOTE,
        val notePads: List<NotePad> = emptyList(),
    ) : MainState()

//    data class Error(val message: String) : MainStateN()
    data object Empty : MainState()
    data class Finish(val id: Long) : MainState()
}
