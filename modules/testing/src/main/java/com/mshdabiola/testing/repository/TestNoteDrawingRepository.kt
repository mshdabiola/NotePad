package com.mshdabiola.testing.repository

import com.mshdabiola.data.repository.INoteDrawingRepository
import com.mshdabiola.model.NoteDrawing
import kotlinx.coroutines.flow.Flow

internal class TestNoteDrawingRepository : INoteDrawingRepository {
    override suspend fun delete(imageId: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun insert(list: List<NoteDrawing>) {
        TODO("Not yet implemented")
    }

    override fun getAll(imageId: Long): Flow<List<NoteDrawing>> {
        TODO("Not yet implemented")
    }
}
