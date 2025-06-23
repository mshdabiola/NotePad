package com.mshdabiola.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "note_check_table",
    foreignKeys = [
        ForeignKey(
            entity = NoteEntity::class,
            parentColumns = ["id"],
            childColumns = ["noteId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class NoteCheckEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long?,
    @ColumnInfo(index = true)
    val noteId: Long,
    val content: String,
    val isCheck: Boolean,
)
