package com.mshdabiola.model

import kotlinx.serialization.Serializable

@Serializable
data class DrawingPath(
    val coordinates: List<Coordinate> = emptyList(),
    val drawingProperties: DrawingProperties = DrawingProperties(),
    var isSelected: Boolean = false,
)
