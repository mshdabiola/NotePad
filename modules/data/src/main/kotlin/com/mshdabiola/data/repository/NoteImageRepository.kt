package com.mshdabiola.data.repository

import com.mshdabiola.model.NoteImage
import kotlinx.coroutines.flow.Flow

interface NoteImageRepository {
    suspend fun upserts(images: List<NoteImage>): List<Long>

    suspend fun upsert(image: NoteImage): Long
    suspend fun delete(id: Long)

    suspend fun deleteByNoteId(noteId: Long)

    fun getAll(): Flow<List<NoteImage>>
    fun getByNoteId(noteId: Long): Flow<List<NoteImage>>

    fun get(id: Long): Flow<NoteImage?>
}
