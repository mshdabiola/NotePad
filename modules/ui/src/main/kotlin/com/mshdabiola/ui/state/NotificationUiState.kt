package com.mshdabiola.ui.state

import kotlinx.datetime.LocalDateTime

data class NotificationUiState(
    val currentDateTime: LocalDateTime,
    val currentInterval: NotificationInterval,
    val currentPlace: NotificationPlace?,
)
