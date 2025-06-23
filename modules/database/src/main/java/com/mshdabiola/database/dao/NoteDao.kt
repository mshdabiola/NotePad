package com.mshdabiola.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mshdabiola.database.model.NoteEntity
import com.mshdabiola.model.NoteType

@Dao
interface NoteDao {

    @Upsert
    suspend fun upsert(noteEntity: NoteEntity): Long

    @Upsert
    suspend fun upsert(noteEntity: List<NoteEntity>)

    @Query("DELETE FROM note_table WHERE id = :noteId")
    suspend fun delete(noteId: Long)

    @Query("DELETE FROM note_table WHERE id in (:ids)")
    suspend fun delete(ids: Set<Long>)

    @Query("DELETE FROM note_table WHERE noteType = :noteType")
    fun deleteTrash(noteType: NoteType)
}
