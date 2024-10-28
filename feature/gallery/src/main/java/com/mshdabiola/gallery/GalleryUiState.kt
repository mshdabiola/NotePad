package com.mshdabiola.gallery

import com.mshdabiola.model.NoteImage

data class GalleryUiState(
    val images: List<NoteImage> = emptyList(),
    val currentIndex: Int = 0,
)
