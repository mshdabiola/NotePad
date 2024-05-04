package com.mshdabiola.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.mshdabiola.model.NoteImage

@Entity(
    tableName = "note_image_table",
    primaryKeys = ["id", "noteId"],
)
data class NoteImageEntity(
    val id: Long,
    val noteId: Long,
    val isDrawing: Boolean,
    @ColumnInfo(defaultValue = "0")
    val timestamp: Long
)

fun NoteImage.toNoteImageEntity() = NoteImageEntity(id, noteId, isDrawing, timestamp)
fun NoteImageEntity.toNoteImage() = NoteImage(id, noteId, isDrawing, timestamp)
