package com.mshdabiola.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

data class NotificationUiState(
    val currentTime: LocalTime,
    val currentDate: LocalDate,
    val currentInterval: NotificationInterval,
    val currentPlace: NotificationPlace?,
    val times: List<NotificationTime>,
    val dates: List<NotificationDate>,
    val intervals: List<NotificationInterval>,
    val places: List<NotificationPlace>,
)
