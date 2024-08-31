package com.mshdabiola.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "note_check_table",
)
data class NoteCheckEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long?,
    val noteId: Long,
    val content: String,
    val isCheck: Boolean,
)
