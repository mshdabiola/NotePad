package com.mshdabiola.testing.repository

import com.mshdabiola.data.repository.IDrawingPathRepository
import com.mshdabiola.model.DrawPath
import kotlinx.coroutines.flow.Flow

internal class TestDrawingPathRepository : IDrawingPathRepository {
    override suspend fun delete(imageId: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun insert(list: List<DrawPath>) {
        TODO("Not yet implemented")
    }

    override fun getAll(imageId: Long): Flow<List<DrawPath>> {
        TODO("Not yet implemented")
    }
}
