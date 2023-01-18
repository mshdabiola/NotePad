package com.mshdabiola.searchscreen

import com.mshdabiola.designsystem.component.state.NotePadUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

data class SearchUiState(
    val search: String = "",
    val placeholder: String = "Search",
    val notes: ImmutableList<NotePadUiState> = emptyList<NotePadUiState>().toImmutableList(),
    val labels: ImmutableList<String> = emptyList<String>().toImmutableList()
)
