package com.mshdabiola.model

import kotlinx.datetime.LocalTime

data class NotificationTime(
    val time: LocalTime,
    val isPickTime : Boolean = false,
    val isEnable : Boolean =false
)