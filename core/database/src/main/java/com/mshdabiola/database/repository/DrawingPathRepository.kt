package com.mshdabiola.database.repository

import com.mshdabiola.database.dao.PathDao
import com.mshdabiola.database.model.toDrawPath
import com.mshdabiola.database.model.toDrawPathEntity
import com.mshdabiola.model.DrawPath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DrawingPathRepository
@Inject constructor(
    private val pathDao: PathDao
) {

    suspend fun delete(imageId: Long) = withContext(Dispatchers.IO) {
        pathDao.delete(imageId)
    }

    suspend fun insert(list: List<DrawPath>) = withContext(Dispatchers.IO) {
        pathDao.insert(list.map { it.toDrawPathEntity() })
    }

    fun getAll(imageId: Long) = pathDao.getPaths(imageId)
        .map { drawPathEntities -> drawPathEntities.map { it.toDrawPath() } }
}