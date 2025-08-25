package com.mshdabiola.ui

fun Long.toTime(): String {
    val hour = this / 60000
    val minute = this / 1000 % 60
    return "%02d : %02d".format(hour, minute)
}
