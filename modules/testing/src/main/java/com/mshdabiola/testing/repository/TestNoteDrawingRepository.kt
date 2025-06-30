package com.mshdabiola.testing.repository

import com.mshdabiola.data.repository.NoteDrawingRepository
import com.mshdabiola.model.NoteDrawing
import kotlinx.coroutines.flow.Flow

internal class TestNoteDrawingRepository : NoteDrawingRepository {
    override suspend fun upserts(drawings: List<NoteDrawing>): List<Long> {
        TODO("Not yet implemented")
    }

    override suspend fun upsert(drawing: NoteDrawing): Long {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteByNoteId(noteId: Long) {
        TODO("Not yet implemented")
    }

    override fun getAll(): Flow<List<NoteDrawing>> {
        TODO("Not yet implemented")
    }

    override fun getByNoteId(noteId: Long): Flow<List<NoteDrawing>> {
        TODO("Not yet implemented")
    }

    override fun get(id: Long): Flow<NoteDrawing?> {
        TODO("Not yet implemented")
    }
}
