package com.mshdabiola.data.repository

import com.mshdabiola.model.NoteCheck
import kotlinx.coroutines.flow.Flow

interface NoteCheckRepository {

    suspend fun upserts(checks: List<NoteCheck>): List<Long>

    suspend fun upsert(check: NoteCheck): Long
    suspend fun delete(id: Long)

    suspend fun deleteByNoteId(noteId: Long)

    fun getAll(): Flow<List<NoteCheck>>
    fun getByNoteId(noteId: Long): Flow<List<NoteCheck>>

    fun get(id: Long): Flow<NoteCheck?>
}
