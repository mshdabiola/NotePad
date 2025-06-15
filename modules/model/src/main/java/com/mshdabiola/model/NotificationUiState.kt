package com.mshdabiola.model

import kotlinx.datetime.LocalDateTime

data class NotificationUiState(
    val currentDateTime: LocalDateTime,
    val currentInterval: NotificationInterval,
    val currentPlace: NotificationPlace?,
)
