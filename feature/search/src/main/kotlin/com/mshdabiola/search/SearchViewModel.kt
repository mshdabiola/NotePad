package com.mshdabiola.search

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mshdabiola.data.repository.INotePadRepository
import com.mshdabiola.data.repository.UserDataRepository
import com.mshdabiola.model.NotePad
import com.mshdabiola.model.NoteType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
internal class SearchViewModel
@Inject constructor(
    private val notepadRepository: INotePadRepository,
    userDataRepository: UserDataRepository,
) : ViewModel() {

    val searchQuery = TextFieldState()

    private val notepads = userDataRepository
        .userData
        .mapLatest {
            it.noteDisplayCategory
        }
        .flatMapLatest {
            when (it.noteType) {
                NoteType.REMINDER -> notepadRepository.getNotePadsWithMainData(it)
                NoteType.ARCHIVE -> notepadRepository.getNotePadsWithMainData(it)
                else -> notepadRepository.getNotePads()
            }
        }

    private val isGrid = userDataRepository
        .userData
        .mapLatest { it.isGrid }
    private val searchSort = MutableStateFlow<SearchSort?>(null)
    private var isTextAfterSearchSort = false

    val searchState = combine(
        snapshotFlow { searchQuery.text }
            .debounce(200),
        notepads,
        searchSort,
        isGrid,

    ) { query, notepads, searchSorts, isGrid ->
        if (query.isBlank() && searchSorts == null) {
            onBlankSearch(notepads)
        } else {
            val list = onSearch(query.toString(), searchSorts, notepads)
            SearchState.Success(
                searches = list,
                isGrid = isGrid,
                searchSort = searchSorts,
            )
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = onBlankSearch(emptyList()),
        )

    fun onSetSearch(searchSort: SearchSort?) {
        this.searchSort.value = searchSort
    }

    private fun onBlankSearch(notepads: List<NotePad>): SearchState.Select {
        val type = listOf(
            SearchSort.Type(0),
            SearchSort.Type(1),
            SearchSort.Type(2),
            SearchSort.Type(3),
            SearchSort.Type(4),
            SearchSort.Type(5),
        )

        val labels = notepads
            .flatMap { it.labels }
            .distinctBy { it.id }
            .map { SearchSort.Label(it.label, 6, it.id) }

        val backgrounds = notepads
            .map {
                it.color
            }
            .distinct()
            .sorted()
            .map { SearchSort.Color(it) }

        return SearchState.Select(
            types = type,
            label = labels,
            color = backgrounds,
        )
    }

    private fun onSearch(
        query: String,
        searchSort: SearchSort?,
        notepads: List<NotePad>,
    ): List<NotePad> {
        return when {
            searchSort != null -> {
                var list = when (searchSort) {
                    is SearchSort.Color -> {
                        notepads.filter { it.color == searchSort.colorIndex }
                    }

                    is SearchSort.Label -> {
                        notepads.filter { it.labels.any { it.id == searchSort.id } }
                    }

                    is SearchSort.Type -> {
                        when (searchSort.index) {
                            0 -> notepads.filter { it.notification != null }
                            1 -> notepads.filter { it.isCheck }
                            2 -> notepads.filter { it.images.isNotEmpty() }
                            3 -> notepads.filter { it.voices.isNotEmpty() }
                            4 -> notepads.filter { it.images.any { it.isDrawing } }
                            5 -> notepads.filter { it.uris.isNotEmpty() }
                            else -> notepads
                        }
                    }
                }

                if (query.isNotBlank()) {
                    isTextAfterSearchSort = true

                    list = list.filter {
                        it.toString().contains(
                            query,
                            true,
                        )
                    }
                }

                if (isTextAfterSearchSort && query.isBlank()) {
                    isTextAfterSearchSort = false
                    onSetSearch(null)
                }

                list
            }

            query.isNotBlank() -> {
                val list = notepads.filter {
                    it.toString().contains(query, true)
                }

                list
            }

            else -> emptyList()
        }
    }
}
