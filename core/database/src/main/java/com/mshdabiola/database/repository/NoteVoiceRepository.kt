package com.mshdabiola.database.repository

import com.mshdabiola.database.dao.NoteVoiceDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NoteVoiceRepository
@Inject constructor(
    private val noteVoiceDao: NoteVoiceDao,
) {
    suspend fun delete(id: Long) = withContext(Dispatchers.IO) {
        noteVoiceDao.deleteVoiceOne(id)
    }
}
