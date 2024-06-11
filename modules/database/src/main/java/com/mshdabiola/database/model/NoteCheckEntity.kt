package com.mshdabiola.database.model

import androidx.room.Entity
import com.mshdabiola.model.NoteCheck

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

fun NoteCheckEntity.toNoteCheck() = NoteCheck(id, noteId, content, isCheck)
fun NoteCheck.toNoteCheckEntity() = NoteCheckEntity(id, noteId, content, isCheck)
