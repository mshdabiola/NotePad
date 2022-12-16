package com.mshdabiola.mainscreen.state

import com.mshdabiola.model.NoteCheck

data class NoteCheckUiState(
    val id: Long? = null,
    val noteId: Long,
    val content: String,
    val isCheck: Boolean
)


fun NoteCheck.toNoteCheckUiState() = NoteCheckUiState(id, noteId, content, isCheck)