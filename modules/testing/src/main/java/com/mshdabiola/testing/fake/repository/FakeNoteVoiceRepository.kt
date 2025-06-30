package com.mshdabiola.testing.fake.repository

import com.mshdabiola.data.repository.NoteVoiceRepository
import com.mshdabiola.model.NoteVoice
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class FakeNoteVoiceRepository
@Inject constructor() : NoteVoiceRepository {
    override suspend fun upserts(voices: List<NoteVoice>): List<Long> {
        TODO("Not yet implemented")
    }

    override suspend fun upsert(voice: NoteVoice): Long {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteByNoteId(noteId: Long) {
        TODO("Not yet implemented")
    }

    override fun getAll(): Flow<List<NoteVoice>> {
        TODO("Not yet implemented")
    }

    override fun getByNoteId(noteId: Long): Flow<List<NoteVoice>> {
        TODO("Not yet implemented")
    }

    override fun get(id: Long): Flow<NoteVoice?> {
        TODO("Not yet implemented")
    }
}
