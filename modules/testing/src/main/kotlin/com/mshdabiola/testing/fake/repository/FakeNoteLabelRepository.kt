package com.mshdabiola.testing.fake.repository

import com.mshdabiola.data.repository.NoteLabelRepository
import com.mshdabiola.model.NoteLabel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject
private const val DELAY_MILLIS = 0L

internal class FakeNoteLabelRepository
@Inject constructor() : NoteLabelRepository {
    private val noteLabelsFlow = MutableStateFlow<LinkedHashMap<Pair<Long, Long>, NoteLabel>>(linkedMapOf())

    override suspend fun upserts(labels: List<NoteLabel>): List<Long> {
        kotlinx.coroutines.delay(DELAY_MILLIS) // Simulate network/DB delay
        val ids = mutableListOf<Long>()
        noteLabelsFlow.update { currentNoteLabels ->
            val updatedMap = LinkedHashMap(currentNoteLabels)
            labels.forEach { labelToUpsert ->
                val key = Pair(labelToUpsert.noteId, labelToUpsert.labelId)
                updatedMap[key] = labelToUpsert // Add or update using the Pair key
                ids.add(labelToUpsert.labelId) // Assuming label.id is the ID for the NoteLabel object itself
            }
            updatedMap
        }
        return ids
    }

    override suspend fun upsert(label: NoteLabel): Long {
        kotlinx.coroutines.delay(DELAY_MILLIS) // Simulate network/DB delay
        noteLabelsFlow.update { currentNoteLabels ->
            val updatedMap = LinkedHashMap(currentNoteLabels)
            val key = Pair(label.noteId, label.labelId)
            updatedMap[key] = label // Add or update using the Pair key
            updatedMap
        }
        return label.labelId // Return the ID of the NoteLabel object itself
    }

    override suspend fun deleteByNoteId(id: Long) {
        kotlinx.coroutines.delay(DELAY_MILLIS) // Simulate network/DB delay
        noteLabelsFlow.update { currentNoteLabels ->
            val updatedMap = LinkedHashMap(currentNoteLabels)
            // Remove all note labels associated with the given note ID
            updatedMap.entries.removeIf { it.key.first == id } // Check the noteId part of the Pair key
            updatedMap
        }
    }

    override suspend fun deleteByNoteIdAndLabelId(noteId: Long, labelId: Long) {
        kotlinx.coroutines.delay(DELAY_MILLIS) // Simulate network/DB delay
        noteLabelsFlow.update { currentNoteLabels ->
            val updatedMap = LinkedHashMap(currentNoteLabels)
            val keyToRemove = Pair(noteId, labelId)
            updatedMap.remove(keyToRemove) // Directly remove using the Pair key
            updatedMap
        }
    }

    override fun getAll(): Flow<List<NoteLabel>> {
        // Return all note labels
        return noteLabelsFlow.asStateFlow().map { it.values.toList() }
    }

    override fun getByNoteId(noteId: Long): Flow<List<NoteLabel>> {
        // Filter note labels by noteId
        return noteLabelsFlow.asStateFlow().map { noteLabelsMap ->
            noteLabelsMap.values.filter { it.noteId == noteId }.toList()
        }
    }

    override fun getByLabelId(labelId: Long): Flow<List<NoteLabel>> {
        // Filter note labels by labelId
        return noteLabelsFlow.asStateFlow().map { noteLabelsMap ->
            noteLabelsMap.values.filter { it.labelId == labelId }.toList()
        }
    }

    override fun getByNoteIds(ids: Set<Long>): Flow<List<NoteLabel>> {
        // Filter note labels where their noteId is in the provided set of IDs
        return noteLabelsFlow.asStateFlow().map { noteLabelsMap ->
            noteLabelsMap.values.filter { it.noteId in ids }.toList()
        }
    }

    // Helper function for tests to set initial data
    fun setData(newNoteLabels: List<NoteLabel>) {
        val notesLabelMap = LinkedHashMap<Pair<Long, Long>, NoteLabel>()
        newNoteLabels.forEach { label ->
            val key = Pair(label.noteId, label.labelId)
            notesLabelMap[key] = label
        }
        noteLabelsFlow.value = notesLabelMap
    }

    // Helper function for tests to clear data
    fun clearData() {
        noteLabelsFlow.value = linkedMapOf()
    }
}
