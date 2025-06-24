package com.mshdabiola.gallery

import com.mshdabiola.model.NoteVisual

data class GalleryUiState(
    val images: List<NoteVisual.NoteImage> = emptyList(),
    val initIndex: Int = 0,
)
