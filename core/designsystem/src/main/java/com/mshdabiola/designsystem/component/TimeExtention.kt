package com.mshdabiola.designsystem.component

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn

fun Long.toTimeString(isCurr: Boolean = false): String {
    val instant =
        Instant.fromEpochMilliseconds(this)

    val dateTime = instant.toLocalDateTime(
        if (isCurr) TimeZone.currentSystemDefault() else TimeZone.UTC,
    )
    val hour = dateTime.hour % 12L
    val a = if (dateTime.hour > 11) "PM" else "AM"
    return "%02d : %02d %s".format(hour, dateTime.minute, a)
}

fun Long.toTime(): String {
    val hour = this / 60000
    val minute = this / 1000 % 60
    return "%02d : %02d".format(hour, minute)
}

fun Long.toDateString(): String {
    val instant =
        Instant.fromEpochMilliseconds(this)
    val today = Clock.System.todayIn(TimeZone.UTC)
    val tomorrow = today.plus(1, DateTimeUnit.DAY)
    val yesterday = today.minus(DateTimeUnit.DAY)
    val dateTime = instant.toLocalDateTime(TimeZone.UTC)

    val datestring = when (dateTime.date) {
        today -> "Today"
        tomorrow -> "Tomorrow"
        yesterday -> "Yesterday"
        else -> {
            "${
                dateTime.month.name.substring(0..2).lowercase().replaceFirstChar { it.uppercase() }
            } ${dateTime.dayOfMonth}"
        }
    }

    return datestring
}

fun Long.toTimeAndDate(): String {
    val instant =
        Instant.fromEpochMilliseconds(this)
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

    val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

    return when {
        today == dateTime.date -> {
            val hour = dateTime.hour % 12L
            val a = if (dateTime.hour > 11) "PM" else "AM"
            "%02d : %02d %s".format(hour, dateTime.minute, a)
        }

        today.minus(dateTime.date).years > 0 -> {
            "${
                dateTime.month.name.substring(0..2).lowercase().replaceFirstChar { it.uppercase() }
            } ${dateTime.dayOfMonth} ${dateTime.year}"
        }

        else -> {
            "${
                dateTime.month.name.substring(0..2).lowercase().replaceFirstChar { it.uppercase() }
            } ${dateTime.dayOfMonth}"
        }
    }
}
