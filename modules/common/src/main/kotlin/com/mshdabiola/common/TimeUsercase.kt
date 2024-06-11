package com.mshdabiola.common

import kotlinx.datetime.LocalTime
import javax.inject.Inject

class TimeUsercase @Inject constructor() {
    operator fun invoke(time: LocalTime): String {
        val hour = when {
            time.hour > 12 -> time.hour - 12
            time.hour == 0 -> 12
            else -> time.hour
        }
        val timeset = if (time.hour > 11) "PM" else "AM"

        return "%2d : %02d %s".format(hour, time.minute, timeset)
    }
}
