package com.mshdabiola.data.repository

import com.mshdabiola.database.dao.NoteImageDao
import com.mshdabiola.data.model.toNoteImage
import com.mshdabiola.data.model.toNoteImageEntity
import com.mshdabiola.model.NoteImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class NoteImageRepository
@Inject constructor(
    private val noteImageDao: NoteImageDao,
) : INoteImageRepository {
    override fun getImageByNoteId(noteId: Long) = noteImageDao
        .getImageByNoteId(noteId)
        .map { imageEntities -> imageEntities.map { it.toNoteImage() } }

    override suspend fun delete(id: Long) = withContext(Dispatchers.IO) {
        noteImageDao.deleteById(id)
    }

    override suspend fun upsert(noteImage: NoteImage) = withContext(Dispatchers.IO) {
        noteImageDao.upsert(listOf(noteImage.toNoteImageEntity()))
    }
}
