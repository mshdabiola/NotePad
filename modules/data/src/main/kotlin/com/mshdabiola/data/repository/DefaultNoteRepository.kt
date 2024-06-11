/*
 *abiola 2024
 */

package com.mshdabiola.data.repository

import com.mshdabiola.common.network.Dispatcher
import com.mshdabiola.common.network.SkDispatchers
import com.mshdabiola.database.dao.NoteDao
import com.mshdabiola.model.Note
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultNoteRepository @Inject constructor(
    private val noteDao: NoteDao,
    @Dispatcher(SkDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : NoteRepository {
    override suspend fun upsert(note: Note): Long {
        return TODO()
    }

    override fun getAll(): Flow<List<Note>> {
        return TODO()
    }

    override fun getOne(id: Long): Flow<Note?> {
        return TODO()
    }

    override suspend fun delete(id: Long) {
        withContext(ioDispatcher) {
            noteDao.delete(id)
        }
    }
}
