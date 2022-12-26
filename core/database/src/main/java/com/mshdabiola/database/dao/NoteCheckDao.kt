package com.mshdabiola.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mshdabiola.database.model.NoteCheckEntity

@Dao
interface NoteCheckDao {


    @Upsert
    suspend fun upsert(noteCheckEntity: List<NoteCheckEntity>)


    @Query("DELETE FROM note_check_table WHERE noteId = :noteId")
    suspend fun deleteByNoteId(noteId: Long)

    @Query("DELETE FROM note_check_table WHERE id = :id AND noteId = :noteId")
    suspend fun delete(id: Long, noteId: Long)
}