package com.mshdabiola.ui.state

data class DateDialogUiData(
    val isEdit: Boolean = false,
    val currentTime: Int = 0,
    val timeData: List<DateListUiState> = emptyList<DateListUiState>(),
    val timeError: Boolean = false,
    val currentDate: Int = 0,
    val dateData: List<DateListUiState> = emptyList<DateListUiState>(),
    val currentInterval: Int = 0,
    val interval: List<DateListUiState> = emptyList<DateListUiState>(),
    val showTimeDialog: Boolean = false,
    val showDateDialog: Boolean = false,
)
