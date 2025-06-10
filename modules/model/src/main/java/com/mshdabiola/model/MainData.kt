package com.mshdabiola.model

sealed class MainData(val index: Long) {
    object Note : MainData(-1L)
    object Achieve : MainData(-2L)
    object Trash : MainData(-3L)
    data class Label(val id: Long) : MainData(index = id) // Assigning a default index like in the original enum
    object Remainder : MainData(-4L)
}
