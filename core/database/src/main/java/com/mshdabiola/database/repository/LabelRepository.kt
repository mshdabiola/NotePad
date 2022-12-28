package com.mshdabiola.database.repository

import com.mshdabiola.database.dao.LabelDao
import com.mshdabiola.database.model.toLabel
import com.mshdabiola.database.model.toLabelEntity
import com.mshdabiola.model.Label
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LabelRepository
@Inject constructor(
    private val labelDao: LabelDao
) {

    suspend fun upsert(labels: List<Label>) = withContext(Dispatchers.IO) {
        labelDao.upsert(labels.map { it.toLabelEntity() })
    }

    suspend fun getOneLabelList(): List<Label> {
        return labelDao.getAllLabelsOneShot().map { it.toLabel() }
    }

    fun getAllLabels() =
        labelDao.getAllLabels().map { labelEntities -> labelEntities.map { it.toLabel() } }

    suspend fun delete(id: Long) = withContext(Dispatchers.IO) {
        labelDao.delete(id)
    }
}