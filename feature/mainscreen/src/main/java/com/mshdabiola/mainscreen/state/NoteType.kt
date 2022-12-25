package com.mshdabiola.mainscreen.state

sealed class NoteType {
    object NOTE : NoteType()
    object TRASH : NoteType()
    object ARCHIVE : NoteType()
    object REMAINDER : NoteType()
    data class LABEL(val index: Int) : NoteType()
}

fun NoteType.toNoteType() = when (this) {
    NoteType.NOTE -> com.mshdabiola.model.NoteType.NOTE
    NoteType.TRASH -> com.mshdabiola.model.NoteType.TRASH
    else -> com.mshdabiola.model.NoteType.ARCHIVE
}
