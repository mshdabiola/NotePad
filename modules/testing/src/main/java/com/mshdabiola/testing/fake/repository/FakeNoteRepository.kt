package com.mshdabiola.testing.fake.repository

import com.mshdabiola.data.repository.NoteRepository
import com.mshdabiola.model.Note
import com.mshdabiola.model.NotePad
import com.mshdabiola.model.NoteType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class FakeNoteRepository
@Inject constructor() : NoteRepository {
    override suspend fun upserts(notes: List<Note>): List<Long> {
        TODO("Not yet implemented")
    }

    override suspend fun upsert(note: Note): Long {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteIds(ids: Set<Long>) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTrash() {
        TODO("Not yet implemented")
    }

    override fun getAll(): Flow<List<NotePad>> {
        TODO("Not yet implemented")
    }

    override fun get(id: Long): Flow<NotePad?> {
        TODO("Not yet implemented")
    }

    override fun getByNoteType(noteType: NoteType): Flow<List<NotePad>> {
        TODO("Not yet implemented")
    }

    override fun getByNoteIds(set: Set<Long>): Flow<List<NotePad>> {
        TODO("Not yet implemented")
    }
}
