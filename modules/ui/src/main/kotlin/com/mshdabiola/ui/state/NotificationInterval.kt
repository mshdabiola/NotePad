package com.mshdabiola.ui.state

import androidx.compose.foundation.text.input.TextFieldState
import kotlinx.datetime.LocalDate

sealed class NotificationInterval(val index:Int=0) {
    data class Daily(
        val interval: TextFieldState = TextFieldState("1"),
        val intervalEnd: IntervalEnd,
    ) : NotificationInterval(1)
    data class Weekly(
        val interval: TextFieldState = TextFieldState("1"),
        val days: Set<Int> = emptySet(),
        val intervalEnd: IntervalEnd,
    ) : NotificationInterval(2)
    data class Monthly(
        val interval: TextFieldState = TextFieldState("1"),
        val sameDay: Boolean,
        val intervalEnd: IntervalEnd,
    ) : NotificationInterval(3)
    data class Yearly(
        val interval: TextFieldState = TextFieldState("1"),
        val intervalEnd: IntervalEnd,
    ) : NotificationInterval(4)
    data object DoNotRepeat : NotificationInterval(0)
    data object Custom : NotificationInterval(5)
}

sealed class IntervalEnd (val index: Int){
    data object Forever : IntervalEnd(0)
    data class EndDate(val date: LocalDate) : IntervalEnd(1)
    data class NumberOfTimes(val times: Int) : IntervalEnd(2)
}
