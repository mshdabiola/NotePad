package com.mshdabiola.data.repository

import com.mshdabiola.model.NoteLabel
import kotlinx.coroutines.flow.Flow

interface NoteLabelRepository {
    suspend fun upserts(labels: List<NoteLabel>): List<Long>

    suspend fun upsert(label: NoteLabel): Long
    suspend fun deleteByNoteId(id: Long)

    suspend fun deleteByNoteIdAndLabelId(noteId: Long, labelId: Long)

    fun getAll(): Flow<List<NoteLabel>>
    fun getByNoteId(noteId: Long): Flow<List<NoteLabel>>

    fun getByLabelId(labelId: Long): Flow<List<NoteLabel>>
    fun getByNoteIds(ids: Set<Long>): Flow<List<NoteLabel>>
}
