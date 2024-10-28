package com.mshdabiola.data.repository

import com.mshdabiola.data.model.toLabel
import com.mshdabiola.data.model.toLabelEntity
import com.mshdabiola.data.model.toNoteLabel
import com.mshdabiola.data.model.toNoteLabelEntity
import com.mshdabiola.database.dao.LabelDao
import com.mshdabiola.database.dao.NoteLabelDao
import com.mshdabiola.model.Label
import com.mshdabiola.model.NoteLabel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class LabelRepository
@Inject constructor(
    private val labelDao: LabelDao,
    private val noteLabelDao: NoteLabelDao,
) : ILabelRepository {

    override suspend fun upsert(labels: List<Label>) = withContext(Dispatchers.IO) {
        labelDao.upsert(labels.map { it.toLabelEntity() })
    }

    override suspend fun upsertNoteLabel(notelabels: List<NoteLabel>) {
        noteLabelDao.upsert(notelabels.map { it.toNoteLabelEntity() })
    }

    override fun getNoteLabel(id: Long): Flow<List<NoteLabel>> {
        return noteLabelDao.getAll(id).map { it.map { it.toNoteLabel() } }
    }

    override suspend fun deleteNoteLabel(noteIds: Set<Long>, labelId: Long) {
        noteLabelDao.delete(noteIds, labelId)
    }

    override suspend fun getOneLabelList(): List<Label> {
        return labelDao.getAllLabelsOneShot().map { it.toLabel() }
    }

    override fun getAllLabels() =
        labelDao.getAllLabels().map { labelEntities -> labelEntities.map { it.toLabel() } }

    override suspend fun delete(id: Long) = withContext(Dispatchers.IO) {
        labelDao.delete(id)
        noteLabelDao.deleteByLabelId(id)
    }
}
