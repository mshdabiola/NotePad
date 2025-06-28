package com.mshdabiola.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mshdabiola.database.model.NoteVoiceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteVoiceDao {

    @Upsert
    suspend fun upserts(voices: List<NoteVoiceEntity>): List<Long>

    @Upsert
    suspend fun upsert(voice: NoteVoiceEntity): Long

    @Query("DELETE FROM note_voice_table WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM note_voice_table WHERE noteId = :noteId")
    suspend fun deleteByNoteId(noteId: Long)

    @Query("SELECT * FROM note_voice_table WHERE id = :id")
    fun get(id: Long): Flow<NoteVoiceEntity?>

    @Query("SELECT * FROM note_voice_table")
    fun getAll(): Flow<List<NoteVoiceEntity>>

    @Query("SELECT * FROM note_voice_table WHERE noteId = :noteId")
    fun getByNoteId(noteId: Long): Flow<List<NoteVoiceEntity>>
}
