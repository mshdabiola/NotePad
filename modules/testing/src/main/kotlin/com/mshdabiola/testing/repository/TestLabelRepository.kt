package com.mshdabiola.testing.repository

import com.mshdabiola.data.repository.LabelRepository
import com.mshdabiola.model.Label
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

// Simulate a delay as if accessing a database
private const val DELAY_MILLIS = 0L

class TestLabelRepository : LabelRepository {

    // Using a LinkedHashMap to store labels, keyed by their ID, for efficient updates and retrieval
    private val labelsFlow = MutableStateFlow<LinkedHashMap<Long, Label>>(linkedMapOf())

    // For generating unique IDs for new labels
    private var nextId = 1L

    // Helper to find the next available ID, ensuring uniqueness
    private fun findNextId(): Long {
        return (labelsFlow.value.keys.maxOrNull() ?: 0L) + 1L
    }

    override suspend fun upserts(labels: List<Label>): List<Long> {
        delay(DELAY_MILLIS) // Simulate network/DB delay
        val ids = mutableListOf<Long>()
        labelsFlow.update { currentLabels ->
            val updatedMap = LinkedHashMap(currentLabels)
            labels.forEach { labelToUpsert ->
                val id: Long
                if (labelToUpsert.id == -1L) {
                    // New label, assign a new ID
                    id = findNextId()
                    updatedMap[id] = labelToUpsert.copy(id = id)
                } else {
                    // Existing label, update it
                    id = labelToUpsert.id
                    updatedMap[id] = labelToUpsert
                }
                ids.add(id)
            }
            updatedMap
        }
        return ids
    }

    override suspend fun upsert(label: Label): Long {
        delay(DELAY_MILLIS) // Simulate network/DB delay
        var newId = 0L
        labelsFlow.update { currentLabels ->
            val updatedMap = LinkedHashMap(currentLabels)
            if (label.id == -1L) {
                // New label, assign a new ID
                newId = findNextId()
                updatedMap[newId] = label.copy(id = newId)
            } else {
                // Existing label, update it
                newId = label.id
                updatedMap[newId] = label
            }
            updatedMap
        }
        return newId
    }

    override suspend fun delete(id: Long) {
        delay(DELAY_MILLIS) // Simulate network/DB delay
        labelsFlow.update { currentLabels ->
            val updatedMap = LinkedHashMap(currentLabels)
            updatedMap.remove(id) // Remove the label by its ID
            updatedMap
        }
    }

    override fun getAll(): Flow<List<Label>> {
        // Emit the current list of all labels
        return labelsFlow.asStateFlow().map { it.values.toList() }
    }

    override fun get(id: Long): Flow<Label?> {
        // Emit the specific label if found, or null if not
        return labelsFlow.asStateFlow().map { it[id] }
    }

    // Helper function for tests to set initial data
    fun setData(newLabels: List<Label>) {
        val labelsMap = LinkedHashMap<Long, Label>()
        var maxId = 0L
        newLabels.forEach { label ->
            val id = if (label.id == -1L) findNextId() else label.id
            labelsMap[id] = label.copy(id = id)
            if (id > maxId) maxId = id
        }
        labelsFlow.value = labelsMap
        nextId = maxId + 1 // Ensure nextId is correctly set for subsequent upserts
    }

    // Helper function for tests to clear data
    fun clearData() {
        labelsFlow.value = linkedMapOf()
        nextId = 1L // Reset nextId as well
    }
}
