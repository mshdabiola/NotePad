package com.mshdabiola.database.model

import androidx.room.Entity

@Entity(
    tableName = "note_voice_table",
    primaryKeys = ["id", "noteId"],
)
data class NoteVoiceEntity(

    val id: Long,
    val noteId: Long,
    val voiceName: String,
)
