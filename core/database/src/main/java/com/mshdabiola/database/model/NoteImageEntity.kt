package com.mshdabiola.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mshdabiola.model.NoteImage

@Entity(tableName = "note_image_table")
data class NoteImageEntity(
    @PrimaryKey
    val id: Long,
    val noteId: Long,
    val imageName: String,
)

fun NoteImage.toNoteImageEntity() = NoteImageEntity(id, noteId, imageName)
fun NoteImageEntity.toNoteImage() = NoteImage(id, noteId, imageName)
