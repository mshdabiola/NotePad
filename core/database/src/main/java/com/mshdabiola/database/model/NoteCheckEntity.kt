package com.mshdabiola.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mshdabiola.model.NoteCheck

@Entity(tableName = "note_check_table")
data class NoteCheckEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val noteId: Long,
    val content: String,
    val isCheck: Boolean
)

fun NoteCheckEntity.toNoteCheck() = NoteCheck(id, noteId, content, isCheck)
