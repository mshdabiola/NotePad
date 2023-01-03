package com.mshdabiola.searchscreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mshdabiola.database.repository.NotePadRepository
import com.mshdabiola.designsystem.component.state.NotePadUiState
import com.mshdabiola.designsystem.component.state.toNotePadUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val notePadRepository: NotePadRepository
) : ViewModel() {

    var searchUiState by mutableStateOf(SearchUiState())
    var notePads: List<NotePadUiState> = emptyList()

    init {
        viewModelScope.launch {
            notePadRepository.getNotePads()
                .collectLatest { pads ->
                    notePads = pads.map { it.toNotePadUiState() }
                }
        }

        viewModelScope
            .launch {
                snapshotFlow { searchUiState }
                    .map { it.search }
                    .collectLatest { text ->
                        val list = notePads.filter { isMatch(it, text) }.toImmutableList()
                        searchUiState = searchUiState.copy(notes = list)
                    }
            }
    }

    fun onSearchTextChange(text: String) {
        searchUiState = searchUiState.copy(search = text)
    }

    private fun isMatch(notePadUiState1: NotePadUiState, text: String): Boolean {

        if (text.isBlank()) return false
        val titleMatch = notePadUiState1.note.title.contains(text, true)
        val detailMatch = notePadUiState1.note.detail.contains(text, ignoreCase = true)
        return detailMatch || titleMatch
    }

    fun onClearSearchText() {
        searchUiState = searchUiState.copy(search = "")
    }
}