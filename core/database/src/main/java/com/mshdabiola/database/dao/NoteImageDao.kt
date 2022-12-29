package com.mshdabiola.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mshdabiola.database.model.NoteImageEntity

@Dao
interface NoteImageDao {


    @Upsert
    suspend fun upsert(noteImageEntity: List<NoteImageEntity>)


    @Query("DELETE FROM note_image_table WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM note_image_table WHERE noteId = :noteId")
    suspend fun deleteByNoteId(noteId: Long)
}