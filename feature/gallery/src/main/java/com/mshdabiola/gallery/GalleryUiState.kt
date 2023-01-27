package com.mshdabiola.gallery

import com.mshdabiola.designsystem.component.state.NoteImageUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

data class GalleryUiState(
    val images: ImmutableList<NoteImageUiState> = emptyList<NoteImageUiState>().toImmutableList(),
    val currentIndex: Int = 0,
)
