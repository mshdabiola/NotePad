package com.mshdabiola.drawing

import androidx.compose.ui.geometry.Offset
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableMap

data class DrawingUiState(
    val paths: ImmutableMap<PathData, List<Offset>> = emptyMap<PathData, List<Offset>>().toImmutableMap(),
    val filePath: String = ""
)
