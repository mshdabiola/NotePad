package com.mshdabiola.detail.navigation

import kotlinx.serialization.Serializable

@Serializable
data class DetailArg(
    val id: Long,
    val colorIndex: Int,
    val background: Int,
)
