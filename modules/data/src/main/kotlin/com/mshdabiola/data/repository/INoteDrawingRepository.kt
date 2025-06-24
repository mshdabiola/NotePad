package com.mshdabiola.data.repository

import com.mshdabiola.model.NoteVisual
import kotlinx.coroutines.flow.Flow

interface INoteDrawingRepository {
    suspend fun insert(noteDrawing: NoteVisual.NoteDrawing): Long

    suspend fun delete(id: Long)

    fun get(id: Long): Flow<NoteVisual.NoteDrawing>

    fun getNoteDrawing(noteId: Long): Flow<List<NoteVisual.NoteDrawing>>
}
