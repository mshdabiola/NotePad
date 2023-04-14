package com.mshdabiola.model

import java.sql.Timestamp

data class NoteImage(
    val id: Long,
    val noteId: Long,
    val imageName: String,
    val isDrawing: Boolean,
    val timestamp: Long=0
)
