package com.mshdabiola.model

data class NoteDrawing(
    val id: Long = -1,
    val noteId: Long,
    val drawingPaths: List<DrawingPath> = emptyList(),
)
