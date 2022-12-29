package com.mshdabiola.mainscreen.state

import com.mshdabiola.model.Note

data class NoteUiState(
    val id: Long? = null,
    val title: String = "",
    val detail: String = "",
    val date: Long = 0,
    val isCheck: Boolean = false,
    val color: Int = -1,
    val background: Int = -1,
    val isPin: Boolean = false
)

fun Note.toNoteUiState() = NoteUiState(id, title, detail, date, isCheck, color, background, isPin)

fun NoteUiState.toNote() = Note(id, title, detail, date, isCheck, color, background, isPin)

