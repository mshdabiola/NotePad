package com.mshdabiola.drawing

import com.mshdabiola.model.DrawingPath

data class DrawingUiState(
    val drawingId: Long? = null,
    val drawings: List<DrawingPath> = emptyList(),
)
