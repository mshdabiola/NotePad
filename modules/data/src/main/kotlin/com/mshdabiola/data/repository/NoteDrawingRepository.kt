package com.mshdabiola.data.repository

import com.mshdabiola.model.NoteDrawing
import kotlinx.coroutines.flow.Flow

interface NoteDrawingRepository {
    suspend fun upserts(drawings: List<NoteDrawing>): List<Long>

    suspend fun upsert(drawing: NoteDrawing): Long
    suspend fun delete(id: Long)

    suspend fun deleteByNoteId(noteId: Long)

    fun getAll(): Flow<List<NoteDrawing>>
    fun getByNoteId(noteId: Long): Flow<List<NoteDrawing>>

    fun get(id: Long): Flow<NoteDrawing?>
}
