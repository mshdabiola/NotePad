package com.mshdabiola.data.repository

import com.mshdabiola.data.model.toLabel
import com.mshdabiola.data.model.toLabelEntity
import com.mshdabiola.database.dao.LabelDao
import com.mshdabiola.model.Label
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class LabelRepository
@Inject constructor(
    private val labelDao: LabelDao,
) : ILabelRepository {

    override suspend fun upsert(labels: List<Label>) = withContext(Dispatchers.IO) {
        labelDao.upsert(labels.map { it.toLabelEntity() })
    }

    override suspend fun getOneLabelList(): List<Label> {
        return labelDao.getAllLabelsOneShot().map { it.toLabel() }
    }

    override fun getAllLabels() =
        labelDao.getAllLabels().map { labelEntities -> labelEntities.map { it.toLabel() } }

    override suspend fun delete(id: Long) = withContext(Dispatchers.IO) {
        labelDao.delete(id)
    }
}
