package com.mshdabiola.model

data class NoteImage(
    val id: Long = -1,
    val noteId: Long = 0,
    val isDrawing: Boolean = false,
    val timestamp: Long = 0,
)
