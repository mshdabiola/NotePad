package com.mshdabiola.designsystem.component.state

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
    val interval: Long = 0,
    val selected: Boolean = false,
    val noteType: NoteTypeUi = NoteTypeUi.NOTE
)

fun Note.toNoteUiState() =
    NoteUiState(
        id,
        title,
        detail,
        editDate,
        isCheck,
        color,
        background,
        isPin,
        reminder,
        interval,
        false,
        noteType.toNoteTypeUi()
    )

fun NoteUiState.toNote() =
    Note(
        id,
        title,
        detail,
        editDate,
        isCheck,
        color,
        background,
        isPin,
        reminder,
        interval,
        noteType.toNoteType()
    )
//fun NoteUiState.toNote() = Note(id, title, detail, date, isCheck, color, background, isPin)


