package com.mshdabiola.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mshdabiola.model.Note

@Entity(tableName = "note_table")
data class NoteEntity(
    @PrimaryKey(true)
    val id: Long?,
    val title: String,
    val detail: String,
    val date: Long,
    val isCheck: Boolean,
    val color: Int,
    val isPin: Boolean
)

fun Note.toNoteEntity() = NoteEntity(id, title, detail, date, isCheck, color, isPin)

fun NoteEntity.toNote() = Note(id, title, detail, date, isCheck, color, isPin)
