package com.mshdabiola.gallery.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class GalleryArg(
    val id: Long,
    val index: Int,
    val total: Int,
    val currentPath: String,
) : NavKey
