package com.mshdabiola.data.repository

import com.mshdabiola.common.network.Dispatcher
import com.mshdabiola.common.network.NoteDispatchers
import com.mshdabiola.data.model.toNoteLabel
import com.mshdabiola.data.model.toNoteLabelEntity
import com.mshdabiola.database.dao.NoteLabelDao
import com.mshdabiola.model.NoteLabel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class RealNoteLabelRepository
@Inject constructor(
    private val noteLabelDao: NoteLabelDao,
    @Dispatcher(NoteDispatchers.IO)
    private val dispatcher: CoroutineDispatcher,
) : NoteLabelRepository {
    override suspend fun upserts(labels: List<NoteLabel>): List<Long> {
        return withContext(dispatcher) {
            noteLabelDao.upserts(labels.map { it.toNoteLabelEntity() })
        }
    }

    override suspend fun upsert(label: NoteLabel): Long {
        return withContext(dispatcher) {
            noteLabelDao.upsert(label.toNoteLabelEntity())
        }
    }

    override suspend fun deleteByNoteId(id: Long) {
        withContext(dispatcher) {
            noteLabelDao.deleteByNoteId(id)
        }
    }

    override suspend fun deleteByNoteIdAndLabelId(noteId: Long, labelId: Long) {
        withContext(dispatcher) {
            noteLabelDao.deleteByNoteIdAndLabelId(noteId, labelId)
        }
    }

    override fun getAll(): Flow<List<NoteLabel>> {
        return noteLabelDao.getAll()
            .map { list -> list.map { it.toNoteLabel() } }
    }

    override fun getByNoteId(noteId: Long): Flow<List<NoteLabel>> {
        return noteLabelDao.getByNoteId(noteId)
            .map { list -> list.map { it.toNoteLabel() } }
    }

    override fun getByLabelId(labelId: Long): Flow<List<NoteLabel>> {
        return noteLabelDao
            .getByLabelId(labelId = labelId)
            .map { list -> list.map { it.toNoteLabel() } }
    }

    override fun getByNoteIds(ids: Set<Long>): Flow<List<NoteLabel>> {
        return noteLabelDao.getByNoteIds(ids)
            .map { list -> list.map { it.toNoteLabel() } }
    }
}
