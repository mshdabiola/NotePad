package com.mshdabiola.model

data class NoteCheck(
    val id: Long = -1,
    val noteId: Long = 0,
    val content: String = "",
    val focus: Boolean = false,
    val isCheck: Boolean = false,
)
