package com.mshdabiola.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mshdabiola.database.model.NoteEntity

@Dao
interface NoteDao {

    @Upsert
    suspend fun upsert(noteEntity: NoteEntity): Long


    @Query("DELETE FROM note_table WHERE id = :noteId")
    suspend fun delete(noteId: Long)

}