package com.mshdabiola.testing.fake.repository

import com.mshdabiola.data.repository.ILabelRepository
import com.mshdabiola.model.Label
import com.mshdabiola.model.NoteLabel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FakeLabelRepository @Inject constructor() : ILabelRepository {
    override suspend fun upsert(labels: List<Label>) {
    }

    override suspend fun upsertNoteLabel(notelabels: List<NoteLabel>) {
    }

    override fun getNoteLabel(id: Long): Flow<List<NoteLabel>> {
        return flow { emptyList<NoteLabel>() }
    }

    override suspend fun deleteNoteLabel(noteIds: Set<Long>, labelId: Long) {
    }

    override suspend fun getOneLabelList(): List<Label> {
        return emptyList()
    }

    override fun getAllLabels(): Flow<List<Label>> {
        return flow { emptyList<Label>() }
    }

    override suspend fun delete(id: Long) {
    }
}
