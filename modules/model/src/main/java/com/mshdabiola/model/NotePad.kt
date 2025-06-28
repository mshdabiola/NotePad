package com.mshdabiola.model

data class NotePad(
    val note: Note = Note(),
    val notification: NotificationUiState? = null,
    val drawings: List<NoteDrawing> = emptyList(),
    val images: List<NoteImage> = emptyList(),
    val voices: List<NoteVoice> = emptyList(),
    val checks: List<NoteCheck> = emptyList(),
    val labels: List<Label> = emptyList(),
    val uris: List<NoteUri> = emptyList(),
) {
    fun getVisuals(): List<NoteVisual> {
        return (drawings + images)
            .sortedBy { it.key }
    }

    override fun toString(): String {
        return """
            ${note.title}
            ${note.detail}
            ${checks.joinToString(separator = " ")}
        """.trimIndent()
    }

    fun isEmpty(): Boolean {
        val titleIsBlank = note.title.isBlank()
        val detailIsBlank = note.detail.isBlank()
        val emptyImage = getVisuals().isEmpty()
        val voiceEmpty = voices.isEmpty()
        val checksBlank = checks.all { it.content.isBlank() }
        val checkIsEmpty = checks.isEmpty()
        val labelsIsEmpty = labels.isEmpty()
        return titleIsBlank && detailIsBlank && emptyImage && voiceEmpty && checkIsEmpty && checksBlank && labelsIsEmpty
    }

    fun isImageOnly(): Boolean {
        val titleIsBlank = note.title.isBlank()
        val detailIsBlank = note.detail.isBlank()
        val emptyImage = images.isEmpty()
        val emptyDrawing = drawings.isEmpty()
        val voiceEmpty = voices.isEmpty()
        val checksBlank = checks.all { it.content.isBlank() }
        val checkIsEmpty = checks.isEmpty()
        val labelsIsEmpty = labels.isEmpty()
        return titleIsBlank && detailIsBlank && !emptyImage && emptyDrawing && voiceEmpty && checkIsEmpty && checksBlank && labelsIsEmpty
    }

    fun isDrawingOnly(): Boolean {
        val titleIsBlank = note.title.isBlank()
        val detailIsBlank = note.detail.isBlank()
        val emptyImage = images.isEmpty()
        val emptyDrawing = drawings.isEmpty()
        val voiceEmpty = voices.isEmpty()
        val checksBlank = checks.all { it.content.isBlank() }
        val checkIsEmpty = checks.isEmpty()
        val labelsIsEmpty = labels.isEmpty()
        return titleIsBlank && detailIsBlank && emptyImage && !emptyDrawing && voiceEmpty && checkIsEmpty && checksBlank && labelsIsEmpty
    }
}
