package com.mshdabiola.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "note_voice_table",
)
data class NoteVoiceEntity(
    @PrimaryKey()
    val id: Long,
    val noteId: Long,
    val voiceName: String,
)
