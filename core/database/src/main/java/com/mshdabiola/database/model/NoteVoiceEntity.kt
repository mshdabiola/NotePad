package com.mshdabiola.database.model

import androidx.room.Entity
import com.mshdabiola.model.NoteVoice

@Entity(
    tableName = "note_voice_table",
    primaryKeys = ["id", "noteId"],
)
data class NoteVoiceEntity(

    val id: Long,
    val noteId: Long,
    val voiceName: String,
)

fun NoteVoice.toNoteVoiceEntity() = NoteVoiceEntity(id, noteId, voiceName)
fun NoteVoiceEntity.toNoteVoice() = NoteVoice(id, noteId, voiceName)
