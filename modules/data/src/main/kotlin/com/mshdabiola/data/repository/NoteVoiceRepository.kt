package com.mshdabiola.data.repository

import com.mshdabiola.model.NoteVoice
import kotlinx.coroutines.flow.Flow

interface NoteVoiceRepository {
    suspend fun upserts(voices: List<NoteVoice>): List<Long>

    suspend fun upsert(voice: NoteVoice): Long
    suspend fun delete(id: Long)

    suspend fun deleteByNoteId(noteId: Long)

    fun getAll(): Flow<List<NoteVoice>>
    fun getByNoteId(noteId: Long): Flow<List<NoteVoice>>

    fun get(id: Long): Flow<NoteVoice?>
}
