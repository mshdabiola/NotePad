package com.mshdabiola.database.model

import androidx.room.Entity

@Entity(
    tableName = "note_check_table",
    primaryKeys = ["id", "noteId"],
)
data class NoteCheckEntity(
    val id: Long,
    val noteId: Long,
    val content: String,
    val isCheck: Boolean,
)
