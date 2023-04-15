package com.mshdabiola.designsystem.component.state

import com.mshdabiola.model.NoteImage

data class NoteImageUiState(
    val id: Long,
    val noteId: Long,
    val path: String,
    val isDrawing: Boolean,
    val timestamp: Long=0
)

fun NoteImage.toNoteImageUiState(toPath:(Long)->String) = NoteImageUiState(id, noteId, toPath(id), isDrawing,timestamp)

fun NoteImageUiState.toNoteImage() = NoteImage(id, noteId,  isDrawing,timestamp)
