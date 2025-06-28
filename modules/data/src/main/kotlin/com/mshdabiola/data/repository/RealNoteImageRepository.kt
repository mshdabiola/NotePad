package com.mshdabiola.data.repository

import com.mshdabiola.common.network.Dispatcher
import com.mshdabiola.common.network.NoteDispatchers
import com.mshdabiola.data.model.toNoteImage
import com.mshdabiola.data.model.toNoteImageEntity
import com.mshdabiola.database.dao.NoteImageDao
import com.mshdabiola.model.NoteImage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class RealNoteImageRepository
@Inject constructor(
    private val noteImageDao: NoteImageDao,
    @Dispatcher(NoteDispatchers.IO)
    private val dispatcher: CoroutineDispatcher,
) : NoteImageRepository {
    override suspend fun upserts(images: List<NoteImage>): List<Long> {
        return withContext(dispatcher) {
            noteImageDao.upserts(images.map { it.toNoteImageEntity() })
        }
    }

    override suspend fun upsert(image: NoteImage): Long {
        return withContext(dispatcher) {
            noteImageDao.upsert(image.toNoteImageEntity())
        }
    }

    override suspend fun delete(id: Long) {
        withContext(dispatcher) {
            noteImageDao.delete(id)
        }
    }

    override suspend fun deleteByNoteId(noteId: Long) {
        withContext(dispatcher) {
            noteImageDao.deleteByNoteId(noteId)
        }
    }

    override fun getAll(): Flow<List<NoteImage>> {
        return noteImageDao.getAll()
            .map { list -> list.map { it.toNoteImage() } }
    }

    override fun getByNoteId(noteId: Long): Flow<List<NoteImage>> {
        return noteImageDao.getByNoteId(noteId)
            .map { list -> list.map { it.toNoteImage() } }
    }

    override fun get(id: Long): Flow<NoteImage?> {
        return noteImageDao.get(id)
            .map { it?.toNoteImage() }
    }
}
