package com.mshdabiola.data.repository

import com.mshdabiola.model.Label
import com.mshdabiola.model.NoteLabel
import kotlinx.coroutines.flow.Flow

interface ILabelRepository {
    suspend fun upsert(labels: List<Label>)
    suspend fun upsertNoteLabel(notelabels: List<NoteLabel>)
    fun getNoteLabel(id: Long): Flow<List<NoteLabel>>
    suspend fun deleteNoteLabel(noteIds: Set<Long>, labelId: Long)

    suspend fun getOneLabelList(): List<Label>
    fun getAllLabels(): Flow<List<Label>>

    suspend fun delete(id: Long)
}
