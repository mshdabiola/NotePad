package com.mshdabiola.model

data class NoteVoice(
    val id: Long,
    val noteId: Long = 0,
    val voiceName: String = "",
    val length: Long = 0,
    val currentProgress: Long = 0,
    val isPlaying: Boolean = false,
)
