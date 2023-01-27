package com.mshdabiola.database.model

import androidx.room.Entity
import com.mshdabiola.model.NoteImage

@Entity(
    tableName = "note_image_table",
    primaryKeys = ["id", "noteId"],
)
data class NoteImageEntity(
    val id: Long,
    val noteId: Long,
    val imageName: String,
    val isDrawing: Boolean,
)

fun NoteImage.toNoteImageEntity() = NoteImageEntity(id, noteId, imageName, isDrawing)
fun NoteImageEntity.toNoteImage() = NoteImage(id, noteId, imageName, isDrawing)
