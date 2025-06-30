package com.mshdabiola.gallery.navigation

import kotlinx.serialization.Serializable

@Serializable
data class GalleryArg(
    val id: Long,
    val index: Int,
    val total: Int,
    val currentPath: String,
)
