package com.mshdabiola.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "note_image_table",
)
data class NoteImageEntity(
    @PrimaryKey()
    val id: Long,
    val noteId: Long,
    val isDrawing: Boolean,
    @ColumnInfo(defaultValue = "0")
    val timestamp: Long,
)
