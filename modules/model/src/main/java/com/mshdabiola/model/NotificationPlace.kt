package com.mshdabiola.model

import kotlinx.datetime.LocalDate

sealed class NotificationPlace{
    data class Edit(val place:String) : NotificationPlace()
    data object Home : NotificationPlace()
    data object Work : NotificationPlace()
    data object School : NotificationPlace()
}