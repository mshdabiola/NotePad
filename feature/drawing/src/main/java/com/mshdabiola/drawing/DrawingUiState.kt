package com.mshdabiola.drawing

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

data class DrawingUiState(
    val paths: ImmutableList<PathData> = emptyList<PathData>().toImmutableList()
)
