package com.mshdabiola.testing.repository

import com.mshdabiola.data.repository.INoteLabelRepository
import com.mshdabiola.model.NoteLabel
import kotlinx.coroutines.flow.Flow

internal class TestNoteLabelRepository :
    INoteLabelRepository {
    override suspend fun upsert(labels: List<NoteLabel>) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(ids: Set<Long>, labelId: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteByLabelId(id: Long) {
        TODO("Not yet implemented")
    }

    override fun getAll(id: Long): Flow<List<NoteLabel>> {
        TODO("Not yet implemented")
    }
}
