package com.mshdabiola.model

data class NoteDisplayCategory(val index: Long = NoteType.NOTE.index) {
    val noteType: NoteType
        get() = when (index) {
            -1L -> NoteType.NOTE
            -2L -> NoteType.ARCHIVE
            -3L -> NoteType.TRASH
            -4L -> NoteType.REMAINDER
            else -> NoteType.LABEL
        }
}
