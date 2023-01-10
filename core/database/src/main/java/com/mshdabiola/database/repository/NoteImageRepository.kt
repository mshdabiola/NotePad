package com.mshdabiola.database.repository

import com.mshdabiola.database.dao.NoteImageDao
import com.mshdabiola.database.model.toNoteImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NoteImageRepository
@Inject constructor(
    private val noteImageDao: NoteImageDao
) {
    fun getImageByNoteId(noteId: Long) = noteImageDao
        .getImageByNoteId(noteId)
        .map { imageEntities -> imageEntities.map { it.toNoteImage() } }

    suspend fun delete(id: Long) = withContext(Dispatchers.IO) {
        noteImageDao.deleteById(id)
    }

}