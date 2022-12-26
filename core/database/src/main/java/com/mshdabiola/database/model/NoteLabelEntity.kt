package com.mshdabiola.database.model

import androidx.room.Entity
import com.mshdabiola.model.NoteLabel

@Entity(
    tableName = "note_label_table",
    primaryKeys = ["id", "noteId"],
)
data class NoteLabelEntity(
    val id: Long,
    val noteId: Long,
    val label: String
)

fun NoteLabelEntity.toNoteLabel() = NoteLabel(id, noteId, label)
fun NoteLabel.toNoteLabelEntity() = NoteLabelEntity(id, noteId, label)