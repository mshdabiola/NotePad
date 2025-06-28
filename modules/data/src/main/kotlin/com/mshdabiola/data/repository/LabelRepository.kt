package com.mshdabiola.data.repository

import com.mshdabiola.model.Label
import kotlinx.coroutines.flow.Flow

interface LabelRepository {
    suspend fun upserts(labels: List<Label>): List<Long>

    suspend fun upsert(label: Label): Long
    suspend fun delete(id: Long)

    fun getAll(): Flow<List<Label>>
    fun get(id: Long): Flow<Label?>
}
