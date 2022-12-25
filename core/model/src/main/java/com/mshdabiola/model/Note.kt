package com.mshdabiola.model

data class Note(
    val id: Long? = null,
    val title: String = "",
    val detail: String = "",
    val date: Long = 0,
    val isCheck: Boolean = false,
    val color: Int = 0,
    val isPin: Boolean = false,
    val noteType: NoteType = NoteType.NOTE
)
