package com.mshdabiola.model

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek

sealed class NotificationInterval {
    data class Daily(
        val interval: Int = 1,
        val intervalEnd: IntervalEnd,
    ) : NotificationInterval()
    data class Weekly(
        val interval: Int = 1,
        val days: List<DayOfWeek>,
        val intervalEnd: IntervalEnd,
    ) : NotificationInterval()
    data class Monthly(
        val interval: Int = 1,
        val sameDay: Boolean,
        val intervalEnd: IntervalEnd,
    ) : NotificationInterval()
    data class Yearly(
        val interval: Int = 1,
        val intervalEnd: IntervalEnd,
    ) : NotificationInterval()
    data object DoNotRepeat : NotificationInterval()
    data object Custom : NotificationInterval()
}

sealed class IntervalEnd {
    data object Forever : IntervalEnd()
    data class EndDate(val date: DatePeriod) : IntervalEnd()
    data class NumberOfTimes(val times: Int) : IntervalEnd()
}
