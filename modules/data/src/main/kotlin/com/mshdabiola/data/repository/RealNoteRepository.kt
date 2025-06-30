package com.mshdabiola.data.repository

import com.mshdabiola.common.network.Dispatcher
import com.mshdabiola.common.network.NoteDispatchers
import com.mshdabiola.data.model.asEntity
import com.mshdabiola.data.model.toNotePad
import com.mshdabiola.database.dao.NoteDao
import com.mshdabiola.model.Note
import com.mshdabiola.model.NotePad
import com.mshdabiola.model.NoteType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class RealNoteRepository
@Inject constructor(
    private val noteDao: NoteDao,
    @Dispatcher(NoteDispatchers.IO)
    private val dispatcher: CoroutineDispatcher,
) : NoteRepository {
    override suspend fun upserts(notes: List<Note>): List<Long> {
        return withContext(dispatcher) {
            noteDao.upserts(notes.map { it.asEntity() })
        }
    }

    override suspend fun upsert(note: Note): Long {
        return withContext(dispatcher) {
            noteDao.upsert(note.asEntity())
        }
    }

    override suspend fun delete(id: Long) {
        withContext(dispatcher) {
            noteDao.delete(id)
        }
    }

    override suspend fun deleteIds(ids: Set<Long>) {
        withContext(dispatcher) {
            noteDao.deleteIds(ids)
        }
    }

    override suspend fun deleteTrash() {
        withContext(dispatcher) {
            noteDao.deleteTrash(NoteType.TRASH)
        }
    }

    override fun getAll(): Flow<List<NotePad>> {
        return noteDao.getAll()
            .map { list -> list.map { it.toNotePad() } }
    }

    override fun get(id: Long): Flow<NotePad?> {
        return noteDao.get(id)
            .map { it?.toNotePad() }
    }

    override fun getByNoteType(noteType: NoteType): Flow<List<NotePad>> {
        return noteDao.getByNoteType(noteType)
            .map { list -> list.map { it.toNotePad() } }
    }

    override fun getByNoteIds(set: Set<Long>): Flow<List<NotePad>> {
        return noteDao.getByIds(set)
            .map { list -> list.map { it.toNotePad() } }
    }
}
