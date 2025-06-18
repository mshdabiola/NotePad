package com.mshdabiola.main

import com.mshdabiola.model.MainData
import com.mshdabiola.model.NotePad
import com.mshdabiola.ui.state.NotificationUiState

sealed class MainState {
    data object Loading : MainState()
    data class Success(
        val notePads: List<NotePad> = emptyList(),
        val mainData: MainData = MainData(),
        val setOfSelected: Set<Long> = emptySet(),
        val notificationUiState: NotificationUiState? = null,

    ) : MainState()

    //    data class Error(val message: String) : MainStateN()
}
