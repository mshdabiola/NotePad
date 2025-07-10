package com.mshdabiola.data.repository

import com.mshdabiola.common.network.Dispatcher
import com.mshdabiola.common.network.NoteDispatchers
import com.mshdabiola.data.model.toNoteCheck
import com.mshdabiola.data.model.toNoteCheckEntity
import com.mshdabiola.database.dao.NoteCheckDao
import com.mshdabiola.model.NoteCheck
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class RealNoteCheckRepository
@Inject constructor(
    private val noteCheckDao: NoteCheckDao,
    @Dispatcher(NoteDispatchers.IO)
    private val dispatcher: CoroutineDispatcher,
) : NoteCheckRepository {
    override suspend fun upserts(checks: List<NoteCheck>): List<Long> {
        return withContext(dispatcher) {
            noteCheckDao.upserts(checks.map { it.toNoteCheckEntity() })
        }
    }

    override suspend fun upsert(check: NoteCheck): Long {
        return withContext(dispatcher) {
            noteCheckDao.upsert(check.toNoteCheckEntity())
        }
    }

    override suspend fun delete(id: Long) {
        withContext(dispatcher) {
            noteCheckDao.delete(id)
        }
    }
    override suspend fun deleteCheckedItems(noteId: Long) {
        withContext(dispatcher) {
            noteCheckDao.deleteCheckedItems(noteId)
        }
    }

    override suspend fun deleteByNoteId(noteId: Long) {
        withContext(dispatcher) {
            noteCheckDao.deleteByNoteId(noteId)
        }
    }

    override fun getAll(): Flow<List<NoteCheck>> {
        return noteCheckDao.getAll()
            .map { list -> list.map { it.toNoteCheck() } }
    }

    override fun getByNoteId(noteId: Long): Flow<List<NoteCheck>> {
        return noteCheckDao.getByNoteId(noteId)
            .map { list -> list.map { it.toNoteCheck() } }
    }

    override fun get(id: Long): Flow<NoteCheck?> {
        return noteCheckDao.get(id)
            .map { it?.toNoteCheck() }
    }
}
