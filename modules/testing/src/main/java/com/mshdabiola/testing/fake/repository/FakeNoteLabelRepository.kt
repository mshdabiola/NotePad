package com.mshdabiola.testing.fake.repository

import com.mshdabiola.data.repository.NoteLabelRepository
import com.mshdabiola.model.NoteLabel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class FakeNoteLabelRepository
@Inject constructor() : NoteLabelRepository {
    override suspend fun upserts(labels: List<NoteLabel>): List<Long> {
        TODO("Not yet implemented")
    }

    override suspend fun upsert(label: NoteLabel): Long {
        TODO("Not yet implemented")
    }

    override suspend fun deleteByNoteId(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteByNoteIdAndLabelId(noteId: Long, labelId: Long) {
        TODO("Not yet implemented")
    }

    override fun getAll(): Flow<List<NoteLabel>> {
        TODO("Not yet implemented")
    }

    override fun getByNoteId(noteId: Long): Flow<List<NoteLabel>> {
        TODO("Not yet implemented")
    }

    override fun getByLabelId(labelId: Long): Flow<List<NoteLabel>> {
        TODO("Not yet implemented")
    }

    override fun getByNoteIds(ids: Set<Long>): Flow<List<NoteLabel>> {
        TODO("Not yet implemented")
    }
}
