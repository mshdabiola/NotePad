package com.mshdabiola.testing.repository

import com.mshdabiola.data.repository.ILabelRepository
import com.mshdabiola.model.Label
import kotlinx.coroutines.flow.Flow

internal class TestLabelRepository : ILabelRepository {
    override suspend fun upsert(labels: List<Label>) {
        TODO("Not yet implemented")
    }

    override suspend fun getOneLabelList(): List<Label> {
        TODO("Not yet implemented")
    }

    override fun getAllLabels(): Flow<List<Label>> {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: Long) {
        TODO("Not yet implemented")
    }
}
