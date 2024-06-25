package com.mshdabiola.data.repository

import com.mshdabiola.database.dao.NoteDao
import com.mshdabiola.data.model.toNoteEntity
import com.mshdabiola.model.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class NoteRepository
@Inject constructor(
    private val noteDao: NoteDao,
) : INoteRepository {

    override suspend fun upsert(notes: List<Note>) = withContext(Dispatchers.IO) {
        noteDao.upsert(notes.map { it.toNoteEntity() })
    }
}
