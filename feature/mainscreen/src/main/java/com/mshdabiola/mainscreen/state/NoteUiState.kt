package com.mshdabiola.mainscreen.state

import com.mshdabiola.model.Note

data class NoteUiState(
    val id: Long? = null,
    val title: String = "",
    val detail: String = "",
    val date: Long = 0,
    val isCheck: Boolean = false,
    val color: Int = 0,
    val isPin: Boolean = false
)

fun Note.toNoteUiState() = NoteUiState(id, title, detail, date, isCheck, color, isPin)

fun NoteUiState.toNote() = Note(id, title, detail, date, isCheck, color, isPin)

