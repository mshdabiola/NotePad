package com.mshdabiola.ui.state

import com.mshdabiola.model.Note
import kotlinx.datetime.Clock

data class NoteUiState(
    val id: Long = -1,
    val title: String = "",
    val detail: String = "",
    val editDate: Long = Clock.System.now().toEpochMilliseconds(),
    val isCheck: Boolean = false,
    val color: Int = -1,
    val background: Int = -1,
    val isPin: Boolean = false,
    val reminder: Long = 0,
    val interval: Long = 0,
    val selected: Boolean = false,
    val noteType: NoteTypeUi = NoteTypeUi(),
    val focus: Boolean = false,
    val date: String = "feb 1",
    val lastEdit: String = "feb 1",
)

fun Note.toNoteUiState(getDate: (Long) -> String) =
    NoteUiState(
        id ?: -1,
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
        NoteTypeUi(noteType, 0),
        date = getDate(reminder),
        lastEdit = getDate(this.editDate),

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
        noteType.type,
    )
// fun NoteUiState.toNote() = Note(id, title, detail, date, isCheck, color, background, isPin)
