package com.mshdabiola.designsystem.component.state

import com.mshdabiola.model.NoteCheck

data class NoteCheckUiState(
    val id: Long,
    val noteId: Long,
    val content: String,
    val isCheck: Boolean
)


fun NoteCheck.toNoteCheckUiState() = NoteCheckUiState(id, noteId, content, isCheck)

fun NoteCheckUiState.toNoteCheck() = NoteCheck(id, noteId, content, isCheck)