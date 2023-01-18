package com.mshdabiola.drawing

import androidx.compose.ui.geometry.Offset
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableMap

data class ListOfPathData(

    val paths2: ImmutableMap<PathData, List<Offset>> = emptyMap<PathData, List<Offset>>().toImmutableMap()
)
