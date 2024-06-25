package com.mshdabiola.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "note_image_table",
    primaryKeys = ["id", "noteId"],
)
data class NoteImageEntity(
    val id: Long,
    val noteId: Long,
    val isDrawing: Boolean,
    @ColumnInfo(defaultValue = "0")
    val timestamp: Long,
)
