package com.mshdabiola.ui.state

import androidx.compose.foundation.text.input.TextFieldState
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

sealed class NotificationInterval {
    data class Daily(
        val interval: TextFieldState = TextFieldState("1"),
        val intervalEnd: IntervalEnd,
    ) : NotificationInterval()
    data class Weekly(
        val interval: TextFieldState = TextFieldState("1"),
        val days: List<DayOfWeek>,
        val intervalEnd: IntervalEnd,
    ) : NotificationInterval()
    data class Monthly(
        val interval: TextFieldState = TextFieldState("1"),
        val sameDay: Boolean,
        val intervalEnd: IntervalEnd,
    ) : NotificationInterval()
    data class Yearly(
        val interval: TextFieldState = TextFieldState("1"),
        val intervalEnd: IntervalEnd,
    ) : NotificationInterval()
    data object DoNotRepeat : NotificationInterval()
    data object Custom : NotificationInterval()
}

sealed class IntervalEnd {
    data object Forever : IntervalEnd()
    data class EndDate(val date: LocalDate) : IntervalEnd()
    data class NumberOfTimes(val times: Int) : IntervalEnd()
}
