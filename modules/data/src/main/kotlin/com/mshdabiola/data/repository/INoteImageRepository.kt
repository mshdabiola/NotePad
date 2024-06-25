package com.mshdabiola.data.repository

import com.mshdabiola.model.NoteImage
import kotlinx.coroutines.flow.Flow

interface INoteImageRepository {
    fun getImageByNoteId(noteId: Long): Flow<List<NoteImage>>

    suspend fun delete(id: Long)

    suspend fun upsert(noteImage: NoteImage)
}