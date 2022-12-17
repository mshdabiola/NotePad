package com.mshdabiola.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mshdabiola.model.NoteVoice

@Entity("note_voice_table")
data class NoteVoiceEntity(
    @PrimaryKey
    val id: Long,
    val noteId: Long,
    val voiceName: String,
)

fun NoteVoice.toNoteVoiceEntity() = NoteVoiceEntity(id, noteId, voiceName)
fun NoteVoiceEntity.toNoteVoice() = NoteVoice(id, noteId, voiceName)
