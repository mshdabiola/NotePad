package com.mshdabiola.main

sealed class SearchSort {
    data class Label(val name: String, val iconIndex: Int, val id: Long) : SearchSort()

    data class Color(val colorIndex: Int) : SearchSort()

    data class Type(val index: Int) : SearchSort()
}
