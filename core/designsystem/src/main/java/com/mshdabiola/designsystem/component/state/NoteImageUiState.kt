package com.mshdabiola.designsystem.component.state

import com.mshdabiola.model.NoteImage

data class NoteImageUiState(
    val id: Long,
    val noteId: Long,
    val imageName: String,
    val isDrawing: Boolean,
    val timestamp: Long=0
)

fun NoteImage.toNoteImageUiState() = NoteImageUiState(id, noteId, imageName, isDrawing,timestamp)

fun NoteImageUiState.toNoteImage() = NoteImage(id, noteId, imageName, isDrawing,timestamp)
