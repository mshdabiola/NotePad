package com.mshdabiola.drawing

import androidx.compose.ui.geometry.Offset

data class PathData(
    val x: Float = 0f,
    val y: Float = 0f,
    val color: Int = 0,
    val lineWidth: Int = 8,
    val lineCap: Int = 0,
    val lineJoin: Int = 0,
    val mode: MODE = MODE.MOVE,
    val colorAlpha: Float = 1f,
    val id: Int = 0
) {
    fun getOffset() = Offset(x, y)
}


enum class MODE {
    UP,
    MOVE,
    DOWN
}

enum class DRAW_MODE {
    SELECT,
    ERASE,
    PEN,
    MARKER,
    CRAYON
}


