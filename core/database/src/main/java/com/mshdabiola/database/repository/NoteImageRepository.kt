package com.mshdabiola.database.repository

import com.mshdabiola.database.dao.NoteImageDao
import com.mshdabiola.database.model.toNoteImage
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NoteImageRepository
@Inject constructor(
    private val noteImageDao: NoteImageDao
) {
    fun getImageByNoteId(noteId: Long) = noteImageDao
        .getImageByNoteId(noteId)
        .map { imageEntities -> imageEntities.map { it.toNoteImage() } }

}