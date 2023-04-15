package com.mshdabiola.searchscreen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mshdabiola.common.ContentManager
import com.mshdabiola.common.DateShortStringUsercase
import com.mshdabiola.database.repository.LabelRepository
import com.mshdabiola.database.repository.NotePadRepository
import com.mshdabiola.designsystem.component.state.NotePadUiState
import com.mshdabiola.designsystem.component.state.toNotePadUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val notePadRepository: NotePadRepository,
    private val labelRepository: LabelRepository,
    private val dateShortStringUsercase: DateShortStringUsercase,
    private val contentManager: ContentManager
) : ViewModel() {

    var searchUiState by mutableStateOf(SearchUiState())
    private var notePads: List<NotePadUiState> = emptyList()

    init {
        viewModelScope.launch {
            notePadRepository.getNotePads()
                .collectLatest { pads ->
                    val lab = labelRepository.getAllLabels().first()

                    notePads = pads.map { it ->
                        val labels =
                            it.labels.map { notelab -> lab.single { it.id == notelab.labelId }.label }
                        it.toNotePadUiState(getTime=dateShortStringUsercase::invoke, toPath = contentManager::getImagePath).copy(labels = labels.toImmutableList())
                    }

                    val labels = notePads
                        .map { it.labels }
                        .flatten()
                        .distinct()
                        .toImmutableList()
                    searchUiState = searchUiState.copy(labels = labels)
                }
        }

        viewModelScope
            .launch {
                snapshotFlow { searchUiState }
                    .map { it.search }
                    .filter { it.isNotBlank() }
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

    fun onItemTypeClick(index: Int) {
        when (index) {
            0 -> {
                val remainder = notePads.filter { it.note.reminder > 0 }
                searchUiState = searchUiState.copy(
                    search = "",
                    placeholder = "Reminders",
                    notes = remainder.toImmutableList(),
                )
            } // Reminders
            1 -> {
                val lists = notePads.filter { it.checks.isNotEmpty() }
                searchUiState = searchUiState.copy(
                    search = "",
                    placeholder = "Lists",
                    notes = lists.toImmutableList(),
                )
            } // Lists
            2 -> {
                val images = notePads.filter { it.images.isNotEmpty() }
                searchUiState = searchUiState.copy(
                    search = "",
                    placeholder = "Images",
                    notes = images.toImmutableList(),
                )
            } // Images
            3 -> {
                val voices = notePads.filter { it.voices.isNotEmpty() }
                searchUiState = searchUiState.copy(
                    search = "",
                    placeholder = "Voice",
                    notes = voices.toImmutableList(),
                )
            } // Voice
            4 -> {} // Drawings
            else -> {
                Log.e(this::class.simpleName, "else")
            }
        }
    }

    fun onItemLabelClick(index: Int) {
        val label = searchUiState.labels[index]

        val labels = notePads.filter { it.labels.contains(label) }
        searchUiState = searchUiState.copy(placeholder = label, notes = labels.toImmutableList())
    }
}
