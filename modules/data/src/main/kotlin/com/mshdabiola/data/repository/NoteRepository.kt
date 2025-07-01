package com.mshdabiola.data.repository

import com.mshdabiola.model.NotePad
import com.mshdabiola.model.NoteType
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    suspend fun upserts(notes: List<NotePad>): List<Long>

    suspend fun upsert(note: NotePad): Long
    suspend fun delete(id: Long)

    suspend fun deleteIds(ids: Set<Long>)

    suspend fun deleteTrash()

    fun getAll(): Flow<List<NotePad>>
    fun get(id: Long): Flow<NotePad?>

    fun getByNoteType(noteType: NoteType): Flow<List<NotePad>>

    fun getByNoteIds(set: Set<Long>): Flow<List<NotePad>>
}
