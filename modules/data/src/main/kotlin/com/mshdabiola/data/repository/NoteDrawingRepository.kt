package com.mshdabiola.data.repository

import com.mshdabiola.data.model.toDrawing
import com.mshdabiola.data.model.toEntity
import com.mshdabiola.database.dao.NoteDrawingDao
import com.mshdabiola.model.NoteVisual
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class NoteDrawingRepository
@Inject constructor(
    private val noteDrawingDao: NoteDrawingDao,
) : INoteDrawingRepository {
    override suspend fun insert(noteDrawing: NoteVisual.NoteDrawing): Long {
        return withContext(Dispatchers.IO) {
            noteDrawingDao.upsert(noteDrawing.toEntity())
        }
    }

    override suspend fun delete(id: Long) {
        withContext(Dispatchers.IO) {
            noteDrawingDao.delete(id)
        }
    }

    override fun get(id: Long): Flow<NoteVisual.NoteDrawing?> {
        return noteDrawingDao.get(id)
            .map { it?.toDrawing() }
    }

    override fun getNoteDrawing(noteId: Long): Flow<List<NoteVisual.NoteDrawing>> {
        return noteDrawingDao.getNoteDrawing(noteId)
            .map { list -> list.map { it.toDrawing() } }
    }
}
