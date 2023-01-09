package com.mshdabiola.designsystem.component.state

import com.mshdabiola.model.NoteType

sealed class NoteTypeUi {
    object NOTE : NoteTypeUi()
    object TRASH : NoteTypeUi()
    object ARCHIVE : NoteTypeUi()
    object REMAINDER : NoteTypeUi()
    data class LABEL(val id: Long, val name: String) : NoteTypeUi()
}

fun NoteTypeUi.toNoteType() = when (this) {
    NoteTypeUi.NOTE -> NoteType.NOTE
    NoteTypeUi.TRASH -> NoteType.TRASH
    else -> NoteType.ARCHIVE
}

fun NoteType.toNoteTypeUi() = when (this) {
    NoteType.NOTE -> NoteTypeUi.NOTE
    NoteType.TRASH -> NoteTypeUi.TRASH
    else -> NoteTypeUi.ARCHIVE
}
