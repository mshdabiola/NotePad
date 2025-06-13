package com.mshdabiola.main

import com.mshdabiola.model.MainData
import com.mshdabiola.model.NotePad

sealed class MainState {
    data object Loading : MainState()
    data class Success(
//        val isSearch: Boolean = false,
//        val noteType: NoteType = NoteType.NOTE,
        val notePads: List<NotePad> = emptyList(),
//        val types: List<SearchSort.Type> = emptyList(),
//        val color: List<SearchSort.Color> = emptyList(),
//        val label: List<SearchSort.Label> = emptyList(),
//        val searchSort: SearchSort? = null,
        val mainData: MainData = MainData(),
        val setOfSelected: Set<Long> = emptySet(),

    ) : MainState()

    //    data class Error(val message: String) : MainStateN()
}
