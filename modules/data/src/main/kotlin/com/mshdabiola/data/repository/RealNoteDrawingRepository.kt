package com.mshdabiola.data.repository

import com.mshdabiola.common.network.Dispatcher
import com.mshdabiola.common.network.NoteDispatchers
import com.mshdabiola.data.model.toDrawing
import com.mshdabiola.data.model.toEntity
import com.mshdabiola.database.dao.NoteDrawingDao
import com.mshdabiola.model.NoteDrawing
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class RealNoteDrawingRepository
@Inject constructor(
    private val noteDrawingDao: NoteDrawingDao,

    @Dispatcher(NoteDispatchers.IO)
    private val dispatcher: CoroutineDispatcher,
) : NoteDrawingRepository {
    override suspend fun upserts(drawings: List<NoteDrawing>): List<Long> {
        return withContext(dispatcher) {
            noteDrawingDao.upserts(drawings.map { it.toEntity() })
        }
    }

    override suspend fun upsert(drawing: NoteDrawing): Long {
        return withContext(dispatcher) {
            noteDrawingDao.upsert(drawing.toEntity())
        }
    }

    override suspend fun delete(id: Long) {
        withContext(dispatcher) {
            noteDrawingDao.delete(id)
        }
    }

    override suspend fun deleteByNoteId(noteId: Long) {
        withContext(dispatcher) {
            noteDrawingDao.deleteByNoteId(noteId)
        }
    }

    override fun getAll(): Flow<List<NoteDrawing>> {
        return noteDrawingDao.getAll()
            .map { list -> list.map { it.toDrawing() } }
    }

    override fun getByNoteId(noteId: Long): Flow<List<NoteDrawing>> {
        return noteDrawingDao.getByNoteId(noteId)
            .map { list -> list.map { it.toDrawing() } }
    }

    override fun get(id: Long): Flow<NoteDrawing?> {
        return noteDrawingDao.get(id)
            .map { it?.toDrawing() }
    }
}
