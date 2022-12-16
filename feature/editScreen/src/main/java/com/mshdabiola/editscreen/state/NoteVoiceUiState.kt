package com.mshdabiola.editscreen.state

import com.mshdabiola.model.NoteVoice

data class NoteVoiceUiState(
    val id: Long,
    val noteId: Long,
    val voiceName: String,
)

fun NoteVoice.toNoteVoiceUiState() = NoteVoiceUiState(id, noteId, voiceName)
