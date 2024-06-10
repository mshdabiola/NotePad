package com.mshdabiola.common

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

class DateStringUsercase @Inject constructor() {

    operator fun invoke(date: LocalDate): String {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val month = date.month.name.lowercase().replaceFirstChar { it.uppercaseChar() }

        return when {
            now.date == date -> "Today"
            date == now.date.plus(1, DateTimeUnit.DAY) -> "Tomorrow"
            date.year != now.year -> "$month ${date.dayOfMonth}, ${date.year}"
            else -> "$month ${date.dayOfMonth}"
        }
    }
}
