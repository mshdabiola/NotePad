package com.mshdabiola.model

data class NoteCheck(
    val id: Long,
    val noteId: Long,
    val content: String = "",
    val isCheck: Boolean = false,
)
