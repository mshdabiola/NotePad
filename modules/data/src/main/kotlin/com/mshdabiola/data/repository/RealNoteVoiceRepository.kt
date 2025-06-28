package com.mshdabiola.data.repository

import com.mshdabiola.common.network.Dispatcher
import com.mshdabiola.common.network.NoteDispatchers
import com.mshdabiola.data.model.toNoteVoice
import com.mshdabiola.data.model.toNoteVoiceEntity
import com.mshdabiola.database.dao.NoteVoiceDao
import com.mshdabiola.model.NoteVoice
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class RealNoteVoiceRepository
@Inject constructor(
    private val noteVoiceDao: NoteVoiceDao,
    @Dispatcher(NoteDispatchers.IO)
    private val dispatcher: CoroutineDispatcher,
) : NoteVoiceRepository {
    override suspend fun upserts(voices: List<NoteVoice>): List<Long> {
        return withContext(dispatcher) {
            noteVoiceDao.upserts(voices.map { it.toNoteVoiceEntity() })
        }
    }

    override suspend fun upsert(voice: NoteVoice): Long {
        return withContext(dispatcher) {
            noteVoiceDao.upsert(voice.toNoteVoiceEntity())
        }
    }

    override suspend fun delete(id: Long) {
        withContext(dispatcher) {
            noteVoiceDao.delete(id)
        }
    }

    override suspend fun deleteByNoteId(noteId: Long) {
        withContext(dispatcher) {
            noteVoiceDao.deleteByNoteId(noteId)
        }
    }

    override fun getAll(): Flow<List<NoteVoice>> {
        return noteVoiceDao.getAll()
            .map { list -> list.map { it.toNoteVoice() } }
    }

    override fun getByNoteId(noteId: Long): Flow<List<NoteVoice>> {
        return noteVoiceDao.getByNoteId(noteId)
            .map { list -> list.map { it.toNoteVoice() } }
    }

    override fun get(id: Long): Flow<NoteVoice?> {
        return noteVoiceDao.get(id)
            .map { it?.toNoteVoice() }
    }
}
