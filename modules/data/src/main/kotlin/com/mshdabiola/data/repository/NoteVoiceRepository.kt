package com.mshdabiola.data.repository

import com.mshdabiola.database.dao.NoteVoiceDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class NoteVoiceRepository
@Inject constructor(
    private val noteVoiceDao: NoteVoiceDao,
) : INoteVoiceRepository {
    override suspend fun delete(id: Long) = withContext(Dispatchers.IO) {
        noteVoiceDao.deleteVoiceOne(id)
    }
}
