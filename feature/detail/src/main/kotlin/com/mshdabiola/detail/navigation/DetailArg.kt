package com.mshdabiola.detail.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class DetailArg(
    val id: Long,
    val colorIndex: Int,
    val background: Int,
) : NavKey
