package com.mshdabiola.model

sealed class NoteVisual(val key: Long) {

    data class NoteImage(
        val id: Long = -1,
        val noteId: Long = 0,
        val path: String = "",
    ) : NoteVisual(id)

    data class NoteDrawing(
        val id: Long = -1,
        val noteId: Long,
        val drawingPaths: List<DrawingPath> = emptyList(),
    ) : NoteVisual(id)
}
