package com.mshdabiola.testing.repository

import com.mshdabiola.data.repository.INoteImageRepository
import com.mshdabiola.model.NoteImage
import kotlinx.coroutines.flow.Flow

internal class TestNoteImageRepository : INoteImageRepository {
    override fun getImageByNoteId(noteId: Long): Flow<List<NoteImage>> {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun upsert(noteImage: NoteImage) {
        TODO("Not yet implemented")
    }
}
