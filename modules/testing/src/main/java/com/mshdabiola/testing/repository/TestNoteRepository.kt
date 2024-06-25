package com.mshdabiola.testing.repository

import com.mshdabiola.data.repository.INoteRepository
import com.mshdabiola.model.Note

internal class TestNoteRepository : INoteRepository {
    override suspend fun upsert(notes: List<Note>) {
        TODO("Not yet implemented")
    }
}
