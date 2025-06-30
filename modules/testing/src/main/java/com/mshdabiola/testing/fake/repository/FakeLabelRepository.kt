package com.mshdabiola.testing.fake.repository

import com.mshdabiola.data.repository.LabelRepository
import com.mshdabiola.model.Label
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class FakeLabelRepository
@Inject constructor() : LabelRepository {
    override suspend fun upserts(labels: List<Label>): List<Long> {
        TODO("Not yet implemented")
    }

    override suspend fun upsert(label: Label): Long {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: Long) {
        TODO("Not yet implemented")
    }

    override fun getAll(): Flow<List<Label>> {
        TODO("Not yet implemented")
    }

    override fun get(id: Long): Flow<Label?> {
        TODO("Not yet implemented")
    }
}
