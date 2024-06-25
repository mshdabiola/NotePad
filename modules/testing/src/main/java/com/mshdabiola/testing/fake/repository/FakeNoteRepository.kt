/*
 *abiola 2024
 */

package com.mshdabiola.testing.fake.repository

import com.mshdabiola.data.repository.NoteRepository
import com.mshdabiola.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FakeNoteRepository @Inject constructor() : NoteRepository {

    private val data = mutableListOf<Note>()
    override suspend fun upsert(note: Note): Long {
        data.add(note)
        val lastIndex = data.lastIndex

        return note.id ?: lastIndex.toLong()
    }

    override fun getAll(): Flow<List<Note>> {
        return flow { data }
    }

    override fun getOne(id: Long): Flow<Note?> {
        return flow { data.find { it.id == id } }
    }

    override suspend fun delete(id: Long) {
        data.removeIf { it.id == id }
    }
}
