package com.mshdabiola.model

data class NotePad(
    val note: Note,
    val images: List<NoteImage>,
    val voices: List<NoteVoice>
)
