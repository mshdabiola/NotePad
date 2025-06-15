package com.mshdabiola.model

import kotlinx.datetime.LocalDate

sealed class NotificationDate {
    data class Date(val localDate: LocalDate) : NotificationDate()
    data object PickDate : NotificationDate()
}
