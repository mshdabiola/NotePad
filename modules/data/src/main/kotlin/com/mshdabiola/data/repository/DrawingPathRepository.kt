package com.mshdabiola.data.repository

import com.mshdabiola.data.model.toDrawPath
import com.mshdabiola.data.model.toDrawPathEntity
import com.mshdabiola.database.dao.PathDao
import com.mshdabiola.model.DrawPath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class DrawingPathRepository
@Inject constructor(
    private val pathDao: PathDao,
) : IDrawingPathRepository {

    override suspend fun delete(imageId: Long) = withContext(Dispatchers.IO) {
        pathDao.delete(imageId)
    }

    override suspend fun insert(list: List<DrawPath>) = withContext(Dispatchers.IO) {
        pathDao.insert(list.map { it.toDrawPathEntity() })
    }

    override fun getAll(imageId: Long) = pathDao.getPaths(imageId)
        .map { drawPathEntities -> drawPathEntities.map { it.toDrawPath() } }
}
