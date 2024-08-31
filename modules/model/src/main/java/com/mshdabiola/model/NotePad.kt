package com.mshdabiola.model

data class NotePad(
    val id: Long = -1,
    val title: String = "",
    val detail: String = "",
    val editDate: Long = 0,
    val isCheck: Boolean = false,
    val color: Int = -1,
    val background: Int = -1,
    val isPin: Boolean = false,
    val reminder: Long = 0,
    val interval: Long = 0,
    val noteType: NoteType = NoteType.NOTE,
    val images: List<NoteImage> = emptyList(),
    val voices: List<NoteVoice> = emptyList(),
    val checks: List<NoteCheck> = emptyList(),
    val labels: List<Label> = emptyList(),
)
