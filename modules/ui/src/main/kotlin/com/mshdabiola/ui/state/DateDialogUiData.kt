package com.mshdabiola.ui.state

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

data class DateDialogUiData constructor(
    val isEdit: Boolean = false,
    val currentTime: Int = 0,
    val timeData: ImmutableList<DateListUiState> = emptyList<DateListUiState>().toImmutableList(),
    val timeError: Boolean = false,
    val currentDate: Int = 0,
    val dateData: ImmutableList<DateListUiState> = emptyList<DateListUiState>().toImmutableList(),
    val currentInterval: Int = 0,
    val interval: ImmutableList<DateListUiState> = emptyList<DateListUiState>().toImmutableList(),
    val showTimeDialog: Boolean = false,
    val showDateDialog: Boolean = false,
)
