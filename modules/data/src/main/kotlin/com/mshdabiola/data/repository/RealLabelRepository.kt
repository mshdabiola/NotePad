package com.mshdabiola.data.repository

import com.mshdabiola.common.network.Dispatcher
import com.mshdabiola.common.network.NoteDispatchers
import com.mshdabiola.data.model.toLabel
import com.mshdabiola.data.model.toLabelEntity
import com.mshdabiola.database.dao.LabelDao
import com.mshdabiola.model.Label
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class RealLabelRepository
@Inject constructor(
    private val labelDao: LabelDao,
    @Dispatcher(NoteDispatchers.IO)
    private val dispatcher: CoroutineDispatcher,
) : LabelRepository {
    override suspend fun upserts(labels: List<Label>): List<Long> {
        return withContext(dispatcher) {
            labelDao.upserts(labels.map { it.toLabelEntity() })
        }
    }

    override suspend fun upsert(label: Label): Long {
        return withContext(dispatcher) {
            labelDao.upsert(label.toLabelEntity())
        }
    }

    override suspend fun delete(id: Long) {
        withContext(dispatcher) {
            labelDao.delete(id)
        }
    }

    override fun getAll(): Flow<List<Label>> {
        return labelDao.getAll()
            .map { it.map { it.toLabel() } }
    }

    override fun get(id: Long): Flow<Label?> {
        return labelDao.get(id)
            .map { it?.toLabel() }
    }
}
