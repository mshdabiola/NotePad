package com.mshdabiola.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.mshdabiola.model.NoteImage
import java.sql.Timestamp

@Entity(
    tableName = "note_image_table",
    primaryKeys = ["id", "noteId"],
)
data class NoteImageEntity(
    val id: Long,
    val noteId: Long,
    val imageName: String,
    val isDrawing: Boolean,
    @ColumnInfo(defaultValue = "0")
    val timestamp: Long
)

fun NoteImage.toNoteImageEntity() = NoteImageEntity(id, noteId, imageName, isDrawing,timestamp)
fun NoteImageEntity.toNoteImage() = NoteImage(id, noteId, imageName, isDrawing,timestamp)
