package com.mshdabiola.common

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

class DateShortStringUsercase @Inject constructor(
private val time12UserCase: Time12UserCase
){

    operator fun invoke(long:Long):String{
        val date= Instant.fromEpochMilliseconds(long)
            .toLocalDateTime(TimeZone.currentSystemDefault())

        val now=Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val month=date.month.name.lowercase().replaceFirstChar { it.uppercaseChar() }.substring(0..2)

        return when{
            now.date==date.date->"Today ${time12UserCase.invoke(date.time)} "
            date.date==now.date.plus(1,DateTimeUnit.DAY)->"Tomorrow ${time12UserCase.invoke(date.time)}"
            date.year!=now.year->"$month ${date.dayOfMonth}, ${date.year} ${time12UserCase.invoke(date.time)}"
            else->"$month ${date.dayOfMonth} ${time12UserCase.invoke(date.time)}"
        }

    }
}