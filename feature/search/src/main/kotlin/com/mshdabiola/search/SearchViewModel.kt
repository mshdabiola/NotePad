package com.mshdabiola.search

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mshdabiola.data.repository.UserDataRepository
import com.mshdabiola.domain.GetAllNoteUseCase
import com.mshdabiola.model.NotePad
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
    userDataRepository: UserDataRepository,
    private val getAllNoteUseCase: GetAllNoteUseCase,
) : ViewModel() {

    val searchQuery = TextFieldState()

    private val notepads = userDataRepository
        .userData
        .mapLatest {
            it.noteDisplayCategory
        }
        .flatMapLatest {
            getAllNoteUseCase(it)
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
            .map { SearchSort.Label(it.name, 6, it.id) }

        val backgrounds = notepads
            .map {
                it.note.color
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
                        notepads.filter { it.note.color == searchSort.colorIndex }
                    }

                    is SearchSort.Label -> {
                        notepads.filter { it.labels.any { it.id == searchSort.id } }
                    }

                    is SearchSort.Type -> {
                        when (searchSort.index) {
                            0 -> notepads.filter { it.notification != null }
                            1 -> notepads.filter { it.note.isCheck }
                            2 -> notepads.filter { it.images.isNotEmpty() }
                            3 -> notepads.filter { it.voices.isNotEmpty() }
                            4 -> notepads.filter { it.drawings.isNotEmpty() }
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
