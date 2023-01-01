package com.mshdabiola.mainscreen.state

import com.mshdabiola.model.Note

data class NoteUiState(
    val id: Long? = null,
    val title: String = "",
    val detail: String = "",
    val editDate: Long = 0,
    val isCheck: Boolean = false,
    val color: Int = -1,
    val background: Int = -1,
    val isPin: Boolean = false,
    val reminder: Long = 0,
    val interval: Long = 0
)

fun Note.toNoteUiState() =
    NoteUiState(id, title, detail, editDate, isCheck, color, background, isPin, reminder, interval)

//fun NoteUiState.toNote() = Note(id, title, detail, date, isCheck, color, background, isPin)

