package com.mshdabiola.designsystem.component.state

import com.mshdabiola.model.NoteVoice

data class NoteVoiceUiState(
    val id: Long,
    val noteId: Long,
    val voiceName: String,
    val length: Long = 1,
    val currentProgress: Long = 0
)

fun NoteVoice.toNoteVoiceUiState() = NoteVoiceUiState(id, noteId, voiceName)

fun NoteVoiceUiState.toNoteVoice() = NoteVoice(id, noteId, voiceName)
