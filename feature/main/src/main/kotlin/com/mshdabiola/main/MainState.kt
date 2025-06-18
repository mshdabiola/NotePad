package com.mshdabiola.main

import com.mshdabiola.model.NoteDisplayCategory
import com.mshdabiola.model.NotePad
import com.mshdabiola.model.NotificationUiState

sealed class MainState {
    data object Loading : MainState()
    data class Success(
        val notePads: List<NotePad> = emptyList(),
        val noteDisplayCategory: NoteDisplayCategory = NoteDisplayCategory(),
        val setOfSelected: Set<Long> = emptySet(),
        val notificationUiState: NotificationUiState? = null,

        ) : MainState()

    //    data class Error(val message: String) : MainStateN()
}
