package com.mshdabiola.testing.fake.repository

import com.mshdabiola.data.repository.NoteCheckRepository
import com.mshdabiola.model.NoteCheck
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class FakeNoteCheckRepository
@Inject constructor() : NoteCheckRepository {
    override suspend fun upserts(checks: List<NoteCheck>): List<Long> {
        TODO("Not yet implemented")
    }

    override suspend fun upsert(check: NoteCheck): Long {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCheckedItems(noteId: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteByNoteId(noteId: Long) {
        TODO("Not yet implemented")
    }

    override fun getAll(): Flow<List<NoteCheck>> {
        TODO("Not yet implemented")
    }

    override fun getByNoteId(noteId: Long): Flow<List<NoteCheck>> {
        TODO("Not yet implemented")
    }

    override fun get(id: Long): Flow<NoteCheck?> {
        TODO("Not yet implemented")
    }
}
