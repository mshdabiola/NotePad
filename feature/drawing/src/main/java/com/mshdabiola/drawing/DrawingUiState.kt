package com.mshdabiola.drawing

import com.mshdabiola.model.DrawingPath

data class DrawingUiState(
    val drawings: List<DrawingPath> = emptyList(),
)
