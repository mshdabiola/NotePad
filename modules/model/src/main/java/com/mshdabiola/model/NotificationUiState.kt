package com.mshdabiola.model

import kotlinx.datetime.LocalDateTime

data class NotificationUiState(
    val noteId: Long = -1,
    val currentDateTime: LocalDateTime,
    val currentInterval: NotificationInterval,
    val currentPlace: NotificationPlace?,
)
