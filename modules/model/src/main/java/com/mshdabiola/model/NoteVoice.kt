package com.mshdabiola.model

data class NoteVoice(
    val id: Long,
    val noteId: Long = 0,
    val path: String = "",
    val length: Long = 0,
)
