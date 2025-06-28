package com.mshdabiola.testing.fake.repository

import com.mshdabiola.data.repository.NoteDrawingRepository
import com.mshdabiola.model.NoteDrawing
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FakeNoteDrawingRepository @Inject constructor() : NoteDrawingRepository {
    override suspend fun delete(imageId: Long) {
    }

    override suspend fun insert(list: List<NoteDrawing>) {
    }

    override fun getAll(imageId: Long): Flow<List<NoteDrawing>> {
        return flow { emptyList<NoteDrawing>() }
    }
}
