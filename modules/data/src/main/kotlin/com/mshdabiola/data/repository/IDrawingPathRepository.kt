package com.mshdabiola.data.repository

import com.mshdabiola.model.DrawPath
import kotlinx.coroutines.flow.Flow

interface IDrawingPathRepository {
    suspend fun delete(imageId: Long)

    suspend fun insert(list: List<DrawPath>)
    fun getAll(imageId: Long): Flow<List<DrawPath>>
}
