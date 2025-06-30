package com.mshdabiola.testing.fake.repository

import com.mshdabiola.data.repository.NoteLabelRepository
import com.mshdabiola.model.NoteLabel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

internal class FakeNoteLabelRepository
@Inject constructor() : NoteLabelRepository {
    private val noteLabels = mutableListOf<NoteLabel>()
    private var nextId = 1L // Assuming NoteLabel has an 'id' field of type Long and a copy method

    override suspend fun upserts(labels: List<NoteLabel>): List<Long> {
        val ids = mutableListOf<Long>()
        labels.forEach { label ->
            ids.add(upsert(label))
        }
        return ids
    }

    override suspend fun upsert(label: NoteLabel): Long {
        // Assuming label.id is 0L for new entities and NoteLabel is a data class with a copy method

        noteLabels.add(label)
        return 1
    }

    override suspend fun deleteByNoteId(id: Long) {
        // Assuming NoteLabel has a 'noteId' field
        noteLabels.removeIf { it.noteId == id }
    }

    override suspend fun deleteByNoteIdAndLabelId(noteId: Long, labelId: Long) {
        // Assuming NoteLabel has 'noteId' and 'labelId' fields
        noteLabels.removeIf { it.noteId == noteId && it.labelId == labelId }
    }

    override fun getAll(): Flow<List<NoteLabel>> {
        return flowOf(noteLabels.toList())
    }

    override fun getByNoteId(noteId: Long): Flow<List<NoteLabel>> {
        // Assuming NoteLabel has a 'noteId' field
        return flowOf(noteLabels.filter { it.noteId == noteId }.toList())
    }

    override fun getByLabelId(labelId: Long): Flow<List<NoteLabel>> {
        // Assuming NoteLabel has a 'labelId' field
        return flowOf(noteLabels.filter { it.labelId == labelId }.toList())
    }

    override fun getByNoteIds(ids: Set<Long>): Flow<List<NoteLabel>> {
        // Assuming NoteLabel has a 'noteId' field
        return flowOf(noteLabels.filter { it.noteId in ids }.toList())
    }
}
