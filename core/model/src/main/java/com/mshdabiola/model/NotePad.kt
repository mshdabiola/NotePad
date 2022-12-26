package com.mshdabiola.model

data class NotePad(
    val note: Note = Note(),
    val images: List<NoteImage> = emptyList(),
    val voices: List<NoteVoice> = emptyList(),
    val checks: List<NoteCheck> = emptyList(),
    val labels: List<NoteLabel> = emptyList()
)
