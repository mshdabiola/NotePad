package com.mshdabiola.data.repository

import com.mshdabiola.database.dao.NoteLabelDao
import com.mshdabiola.data.model.toNoteLabel
import com.mshdabiola.data.model.toNoteLabelEntity
import com.mshdabiola.model.NoteLabel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class NoteLabelRepository
@Inject constructor(
    private val noteLabelDao: NoteLabelDao,
) : INoteLabelRepository {

    override suspend fun upsert(labels: List<NoteLabel>) = withContext(Dispatchers.IO) {
        noteLabelDao.upsert(labels.map { it.toNoteLabelEntity() })
    }

    override suspend fun delete(ids: Set<Long>, labelId: Long) = withContext(Dispatchers.IO) {
        noteLabelDao.delete(ids, labelId)
    }

    override suspend fun deleteByLabelId(id: Long) = withContext(Dispatchers.IO) {
        noteLabelDao.deleteByLabelId(id)
    }

    override fun getAll(id: Long) = noteLabelDao.getAll(id).map { labelEntityList ->
        labelEntityList.map { it.toNoteLabel() }
    }
}
