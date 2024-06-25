package com.mshdabiola.data.repository

import com.mshdabiola.model.Label
import kotlinx.coroutines.flow.Flow

interface ILabelRepository {
    suspend fun upsert(labels: List<Label>)

    suspend fun getOneLabelList(): List<Label>
    fun getAllLabels(): Flow<List<Label>>

    suspend fun delete(id: Long)
}