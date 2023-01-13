package com.mshdabiola.drawing

import androidx.compose.ui.geometry.Offset

data class PathData(
    val x: Float = 0f,
    val y: Float = 0f,
    val color: Int = 0,
    val lineWidth: Float = 10f,
    val lineCap: Int = 0,
    val lineJoin: Int = 0,
    val isErase: Boolean = false,
    val mode: MODE = MODE.MOVE,
    val id: Int = 0
) {
    fun getOffset() = Offset(x, y)
}


enum class MODE {
    UP,
    MOVE,
    DOWN
}


