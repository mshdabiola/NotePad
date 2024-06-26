package com.mshdabiola.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mshdabiola.model.NoteType

@Entity(tableName = "note_table")
data class NoteEntity(
    @PrimaryKey(true)
    val id: Long?,
    val title: String,
    val detail: String,
    val editDate: Long,
    val isCheck: Boolean,
    val color: Int,
    val background: Int,
    val isPin: Boolean,
    val reminder: Long,
    val interval: Long,
    val noteType: NoteType,
)
