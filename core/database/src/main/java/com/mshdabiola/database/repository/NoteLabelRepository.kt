package com.mshdabiola.database.repository

import com.mshdabiola.database.dao.NoteLabelDao
import com.mshdabiola.database.model.toNoteLabel
import com.mshdabiola.database.model.toNoteLabelEntity
import com.mshdabiola.model.NoteLabel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NoteLabelRepository
@Inject constructor(
    private val noteLabelDao: NoteLabelDao
) {

    suspend fun upsert(labels: List<NoteLabel>) = withContext(Dispatchers.IO) {
        noteLabelDao.upsert(labels.map { it.toNoteLabelEntity() })
    }

    suspend fun delete(ids: Set<Long>) = withContext(Dispatchers.IO) {
        noteLabelDao.delete(ids)
    }

    suspend fun deleteByLabelId(id: Long) = withContext(Dispatchers.IO) {
        noteLabelDao.deleteByLabelId(id)
    }

    fun getAll(id: Long) = noteLabelDao.getAll(id).map { labelEntityList ->
        labelEntityList.map { it.toNoteLabel() }
    }
}