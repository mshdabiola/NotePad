package com.mshdabiola.model

data class DrawPath(
    val imageId: Long,
    val pathId: Int,
    val color: Int,
    val width: Int,
    val join: Int,
    val alpha: Float,
    val cap: Int,
    val paths: String
)
