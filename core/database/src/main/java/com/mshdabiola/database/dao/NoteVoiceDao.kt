package com.mshdabiola.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mshdabiola.database.model.NoteVoiceEntity

@Dao
interface NoteVoiceDao {

    @Upsert
    suspend fun addVoice(noteVoiceEntity: List<NoteVoiceEntity>)

    @Query("DELETE FROM note_voice_table WHERE id = :id")
    suspend fun deleteVoiceOne(id: Long)

    @Query("DELETE FROM note_voice_table WHERE noteId = :noteId")
    suspend fun deleteVoiceByNoteId(noteId: Long)
}
