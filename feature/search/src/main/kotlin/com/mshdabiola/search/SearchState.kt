package com.mshdabiola.search

import com.mshdabiola.model.NotePad

sealed class SearchState {
    data object Loading : SearchState()

    data class Success(
        val searches: List<NotePad> = emptyList(),
        val types: List<SearchSort.Type> = emptyList(),
        val color: List<SearchSort.Color> = emptyList(),
        val label: List<SearchSort.Label> = emptyList(),
        val searchSort: SearchSort? = null,

    ) : SearchState()
}
