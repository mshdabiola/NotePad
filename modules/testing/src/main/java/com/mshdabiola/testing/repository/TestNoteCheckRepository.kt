package com.mshdabiola.testing.repository

import com.mshdabiola.data.repository.NoteCheckRepository
import com.mshdabiola.model.NoteCheck
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal class TestNoteCheckRepository : NoteCheckRepository {
    private val noteChecks = mutableListOf<NoteCheck>()
    private var nextId = 1L

    override suspend fun upserts(checks: List<NoteCheck>): List<Long> {
        val ids = mutableListOf<Long>()
        checks.forEach { check ->
            ids.add(upsert(check))
        }
        return ids
    }

    override suspend fun upsert(check: NoteCheck): Long {
        return if (check.id == -1L) {
            val newCheck = check.copy(id = nextId++)
            noteChecks.add(newCheck)
            newCheck.id
        } else {
            val index = noteChecks.indexOfFirst { it.id == check.id }
            if (index != -1) {
                noteChecks[index] = check
                check.id
            } else {
                // If ID is non-zero but not found, treat as a new insert with a new ID
                val newCheck = check.copy(id = nextId++)
                noteChecks.add(newCheck)
                newCheck.id
            }
        }
    }

    override suspend fun delete(id: Long) {
        noteChecks.removeIf { it.id == id }
    }

    override suspend fun deleteCheckedItems(noteId: Long) {
        noteChecks.removeIf { it.noteId == noteId && it.isCheck }
    }

    override suspend fun deleteByNoteId(noteId: Long) {
        noteChecks.removeIf { it.noteId == noteId }
    }

    override fun getAll(): Flow<List<NoteCheck>> {
        return flowOf(noteChecks.toList())
    }

    override fun getByNoteId(noteId: Long): Flow<List<NoteCheck>> {
        return flowOf(noteChecks.filter { it.noteId == noteId }.toList())
    }

    override fun get(id: Long): Flow<NoteCheck?> {
        return flowOf(noteChecks.find { it.id == id })
    }
}
