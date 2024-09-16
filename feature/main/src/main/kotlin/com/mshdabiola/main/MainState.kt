package com.mshdabiola.main

import com.mshdabiola.model.NotePad
import com.mshdabiola.model.NoteType

sealed class MainState {
    data object Loading : MainState()
    data class Success(
        val isSearch: Boolean = false,
        val noteType: NoteType = NoteType.NOTE,
        val notePads: List<NotePad> = emptyList(),
        val types: List<SearchSort.Type> = emptyList(),
        val color: List<SearchSort.Color> = emptyList(),
        val label: List<SearchSort.Label> = emptyList(),
        val searchSort: SearchSort? = null,

    ) : MainState()

    //    data class Error(val message: String) : MainStateN()
    data object Empty : MainState()
    data class Finish(val id: Long) : MainState()
}
