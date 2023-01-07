package com.mshdabiola.database.repository

import com.mshdabiola.database.dao.NoteDao
import com.mshdabiola.database.model.toNoteEntity
import com.mshdabiola.model.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NoteRepository
@Inject constructor(
    private val noteDao: NoteDao
) {

    suspend fun upsert(notes: List<Note>) = withContext(Dispatchers.IO) {
        noteDao.upsert(notes.map { it.toNoteEntity() })
    }
}