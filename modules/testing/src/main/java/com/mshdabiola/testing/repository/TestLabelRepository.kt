package com.mshdabiola.testing.repository

import com.mshdabiola.data.repository.LabelRepository
import com.mshdabiola.model.Label
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal class TestLabelRepository : LabelRepository {
    private val labels = mutableListOf<Label>()
    private var nextId = 1L

    override suspend fun upserts(labels: List<Label>): List<Long> {
        val ids = mutableListOf<Long>()
        labels.forEach { label ->
            ids.add(upsert(label))
        }
        return ids
    }

    override suspend fun upsert(label: Label): Long {
        return if (label.id == -1L) {
            val newLabel = label.copy(id = nextId++)
            labels.add(newLabel)
            newLabel.id
        } else {
            val index = labels.indexOfFirst { it.id == label.id }
            if (index != -1) {
                labels[index] = label
                label.id
            } else {
                val newLabel = label.copy(id = nextId++)
                labels.add(newLabel)
                newLabel.id
            }
        }
    }

    override suspend fun delete(id: Long) {
        labels.removeIf { it.id == id }
    }

    override fun getAll(): Flow<List<Label>> {
        return flowOf(labels.toList())
    }

    override fun get(id: Long): Flow<Label?> {
        return flowOf(labels.find { it.id == id })
    }
}
