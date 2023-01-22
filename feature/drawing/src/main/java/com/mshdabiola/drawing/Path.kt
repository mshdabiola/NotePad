package com.mshdabiola.drawing

data class PathData(
    val color: Int = 0,
    val lineWidth: Int = 8,
    val lineCap: Int = 0,
    val lineJoin: Int = 0,
    val colorAlpha: Float = 1f,
    val id: Int = 0
)


enum class MODE {
    UP,
    MOVE,
    DOWN
}

enum class DRAW_MODE {
    ERASE,
    PEN,
    MARKER,
    CRAYON
}


