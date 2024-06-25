package com.mshdabiola.data.repository

import com.mshdabiola.model.NoteLabel
import kotlinx.coroutines.flow.Flow

interface INoteLabelRepository {
    suspend fun upsert(labels: List<NoteLabel>)

    suspend fun delete(ids: Set<Long>, labelId: Long)

    suspend fun deleteByLabelId(id: Long)
    fun getAll(id: Long): Flow<List<NoteLabel>>
}