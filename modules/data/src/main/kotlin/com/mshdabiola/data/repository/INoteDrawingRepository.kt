package com.mshdabiola.data.repository

import com.mshdabiola.model.NoteDrawing
import kotlinx.coroutines.flow.Flow

interface INoteDrawingRepository {
    suspend fun insert(noteDrawing: NoteDrawing): Long

    suspend fun delete(id: Long)

    fun get(id: Long): Flow<NoteDrawing?>

    fun getNoteDrawing(noteId: Long): Flow<List<NoteDrawing>>
}
