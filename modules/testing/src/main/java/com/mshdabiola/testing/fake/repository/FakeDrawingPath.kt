package com.mshdabiola.testing.fake.repository

import com.mshdabiola.data.repository.IDrawingPathRepository
import com.mshdabiola.model.DrawPath
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FakeDrawingPath @Inject constructor() : IDrawingPathRepository {
    override suspend fun delete(imageId: Long) {
    }

    override suspend fun insert(list: List<DrawPath>) {
    }

    override fun getAll(imageId: Long): Flow<List<DrawPath>> {
        return flow { emptyList<DrawPath>() }
    }
}
