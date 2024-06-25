/*
 *abiola 2024
 */

package com.mshdabiola.testing.repository

import com.mshdabiola.data.repository.NoteRepository
import com.mshdabiola.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class TestNoteRepository : NoteRepository {

    private val data = MutableList(4) { index ->
        Note(index.toLong(), "title", "Content")
    }
    override suspend fun upsert(note: Note): Long {
        val id = data.size.toLong()
        data.add(note.copy(id = id))
        return id
    }

    override fun getAll(): Flow<List<Note>> {
        return flowOf(data)
    }

    override fun getOne(id: Long): Flow<Note?> {
        return flowOf(data.singleOrNull { it.id == id })
    }

    override suspend fun delete(id: Long) {
        data.removeIf { it.id == id }
    }
}
