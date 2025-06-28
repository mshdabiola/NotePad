package com.mshdabiola.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mshdabiola.database.model.NoteCheckEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteCheckDao {

    @Upsert
    suspend fun upserts(checks: List<NoteCheckEntity>): List<Long>

    @Upsert
    suspend fun upsert(check: NoteCheckEntity): Long

    @Query("DELETE FROM note_check_table WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM note_check_table WHERE noteId = :noteId")
    suspend fun deleteByNoteId(noteId: Long)

    @Query("SELECT * FROM note_check_table WHERE id = :id")
    fun get(id: Long): Flow<NoteCheckEntity?>

    @Query("SELECT * FROM note_check_table")
    fun getAll(): Flow<List<NoteCheckEntity>>

    @Query("SELECT * FROM note_check_table WHERE noteId = :noteId")
    fun getByNoteId(noteId: Long): Flow<List<NoteCheckEntity>>
}
