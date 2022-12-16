package com.mshdabiola.mainscreen.state

import com.mshdabiola.model.NoteImage

data class NoteImageUiState(
    val id: Long,
    val noteId: Long,
    val imageName: String,
)

fun NoteImage.toNoteImageUiState() = NoteImageUiState(id, noteId, imageName)
