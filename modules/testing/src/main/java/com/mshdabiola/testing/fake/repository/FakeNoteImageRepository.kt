package com.mshdabiola.testing.fake.repository

import com.mshdabiola.data.repository.NoteImageRepository
import com.mshdabiola.model.NoteImage
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class FakeNoteImageRepository
@Inject constructor() : NoteImageRepository {
    override suspend fun upserts(images: List<NoteImage>): List<Long> {
        TODO("Not yet implemented")
    }

    override suspend fun upsert(image: NoteImage): Long {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteByNoteId(noteId: Long) {
        TODO("Not yet implemented")
    }

    override fun getAll(): Flow<List<NoteImage>> {
        TODO("Not yet implemented")
    }

    override fun getByNoteId(noteId: Long): Flow<List<NoteImage>> {
        TODO("Not yet implemented")
    }

    override fun get(id: Long): Flow<NoteImage?> {
        TODO("Not yet implemented")
    }
}
