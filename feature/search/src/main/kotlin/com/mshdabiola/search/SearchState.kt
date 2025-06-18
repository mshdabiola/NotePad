package com.mshdabiola.search

import com.mshdabiola.model.NoteDisplayCategory
import com.mshdabiola.model.NotePad
import com.mshdabiola.model.NotificationUiState

sealed class SearchState {
    data object Loading : SearchState()
    data class Success(
        val notePads: List<NotePad> = emptyList(),
        val noteDisplayCategory: NoteDisplayCategory = NoteDisplayCategory(),
        val setOfSelected: Set<Long> = emptySet(),
        val notificationUiState: NotificationUiState? = null,

    ) : SearchState()

    //    data class Error(val message: String) : MainStateN()
}
