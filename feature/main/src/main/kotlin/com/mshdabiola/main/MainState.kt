package com.mshdabiola.main

import com.mshdabiola.model.NoteDisplayCategory
import com.mshdabiola.model.NotePad
import com.mshdabiola.model.NotificationUiState

sealed class MainState {
    data object Loading : MainState()
    data class Success(
        val isGrid: Boolean = true,
        val labelName: String? = null,
        val pinNotePads: List<NotePad> = emptyList(),
        val unPinNotePads: List<NotePad> = emptyList(),
        val noteDisplayCategory: NoteDisplayCategory = NoteDisplayCategory(),
        val selectState: SelectState? = null,
    ) : MainState()

    //    data class Error(val message: String) : MainStateN()
}

data class SelectState(
    val colorIndex: Int = -1,
    val isAllPin: Boolean = false,
    val setOfSelected: Set<Long> = emptySet(),
    val notificationUiState: NotificationUiState? = null,

)
