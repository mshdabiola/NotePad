package com.mshdabiola.model

data class NoteImage(
    val id: Long,
    val noteId: Long,
    val isDrawing: Boolean,
    val timestamp: Long = 0,
)
