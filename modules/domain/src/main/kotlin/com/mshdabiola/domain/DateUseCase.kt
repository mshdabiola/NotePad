package com.mshdabiola.domain

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

class DateUseCase @Inject constructor() {
    val dateFormat = LocalDate.Format {
        this.monthName(MonthNames.ENGLISH_FULL)
        char(' ')
        dayOfMonth()
        char(',')
        year(Padding.SPACE)
    }
    val timeFormat = LocalTime.Format {
        amPmHour()
        chars(" : ")
        minute()
        chars(" : ")

        amPmMarker("AM", "PM")
    }
    operator fun invoke(date: Long): String {
        val date = Instant.fromEpochMilliseconds(date)
            .toLocalDateTime(TimeZone.currentSystemDefault())

        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

        return when {
            now.date == date.date -> "Today ${date.time.format(timeFormat)} "
            date.date == now.date.minus(1, DateTimeUnit.DAY) ->
                "Yesterday ${date.time.format(timeFormat)}"

            else -> "${date.date.format(dateFormat)} ${date.time.format(timeFormat)}"
        }
    }
}
