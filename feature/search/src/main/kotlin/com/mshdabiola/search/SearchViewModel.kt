package com.mshdabiola.search

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mshdabiola.common.IAlarmManager
import com.mshdabiola.data.repository.INotePadRepository
import com.mshdabiola.data.repository.UserDataRepository
import com.mshdabiola.model.NotePad
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
internal class SearchViewModel
@Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val notepadRepository: INotePadRepository,
    private val alarmManager: IAlarmManager,
    userDataRepository: UserDataRepository,
) : ViewModel() {

    val searchQuery = TextFieldState()
    private val searchTriple = MutableStateFlow<
        Triple<
            List<SearchSort.Type>,
            List<SearchSort.Color>,
            List<SearchSort.Label>,
            >,
        >(Triple(emptyList(), emptyList(), emptyList()))
    private val searchSort = MutableStateFlow<SearchSort?>(null)
    private var isTextAfterSearchSort = false

    val searchState = combine(
        snapshotFlow { searchQuery.text }
            .debounce(200),
        notepadRepository.getNotePads(),
        searchTriple,
        searchSort,

    ) { query, notepads, triple, searchSort ->
        val old = SearchState.Success(
            searches = notepads,
            types = triple.first,
            color = triple.second,
            label = triple.third,
            searchSort = searchSort,
        )

        val searchList = onSearch(old)

        SearchState.Success(
            searches = searchList,
            types = triple.first,
            color = triple.second,
            label = triple.third,
            searchSort = searchSort,
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = SearchState.Loading,
        )

    private fun onSearch(mainState: SearchState.Success): List<NotePad> {
        return when {
            mainState.searchSort != null -> {
                var list = when (val searchSort = mainState.searchSort) {
                    is SearchSort.Color -> {
                        mainState.searches.filter { it.color == searchSort.colorIndex }
                    }

                    is SearchSort.Label -> {
                        mainState.searches.filter { it.labels.any { it.id == searchSort.id } }
                    }

                    is SearchSort.Type -> {
                        when (searchSort.index) {
                            0 -> mainState.searches.filter { it.notification != null }
                            1 -> mainState.searches.filter { it.isCheck }
                            2 -> mainState.searches.filter { it.images.isNotEmpty() }
                            3 -> mainState.searches.filter { it.voices.isNotEmpty() }
                            4 -> mainState.searches.filter { it.images.any { it.isDrawing } }
                            5 -> mainState.searches.filter { it.uris.isNotEmpty() }
                            else -> mainState.searches
                        }
                    }

                    null -> TODO()
                }

                if (searchQuery.text.isNotBlank()) {
                    isTextAfterSearchSort = true

                    list = list.filter {
                        it.toString().contains(
                            searchQuery.text,
                            true,
                        )
                    }
                }

                if (isTextAfterSearchSort && searchQuery.text.isBlank()) {
                    isTextAfterSearchSort = false
                    onSetSearch(null)
                }

                list
            }

            searchQuery.text.isNotBlank() -> {
                val list = mainState.searches.filter {
                    it.toString().contains(searchQuery.text, true)
                }

                list
            }

            else -> emptyList()
        }
    }

    fun onExpandSearch(isExpand: Boolean) {
        viewModelScope.launch {
            searchTriple.update {
                if (!isExpand) {
                    Triple(
                        first = emptyList(),
                        second = emptyList(),
                        third = emptyList(),
                    )
                } else {
                    val notes = notepadRepository.getNotePads().first()

                    val labels = notes.asSequence().filter { it.labels.isEmpty().not() }
                        .map { it.labels }
                        .flatten()
                        .distinct()
                        .map { SearchSort.Label(it.label, 6, it.id) }.toList()

                    val colors = notes.asSequence()
                        .map { it.color }
                        .distinct()
                        .map { SearchSort.Color(it) }.toList()

                    val type = ArrayList<SearchSort.Type>(6)
                    if (notes.any { it.notification != null }) {
                        type.add(SearchSort.Type(0))
                    }
                    if (notes.any { it.isCheck }) {
                        type.add(SearchSort.Type(1))
                    }
                    if (notes.any { it.images.isNotEmpty() }) {
                        type.add(SearchSort.Type(2))
                    }
                    if (notes.any { it.voices.isNotEmpty() }) {
                        type.add(SearchSort.Type(3))
                    }

                    if (notes.any { it.images.any { it.isDrawing } }) {
                        type.add(SearchSort.Type(4))
                    }

                    if (notes.any { it.uris.isNotEmpty() }) {
                        type.add(SearchSort.Type(5))
                    }

                    Triple(
                        first = type,
                        second = colors,
                        third = labels,
                    )
                }
            }
        }
    }

    fun onSetSearch(searchSort: SearchSort?) {
        this.searchSort.value = searchSort
    }
}
