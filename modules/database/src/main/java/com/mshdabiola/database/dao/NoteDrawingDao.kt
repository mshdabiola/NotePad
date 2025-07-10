package com.mshdabiola.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mshdabiola.database.model.NoteDrawingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDrawingDao {

    @Upsert
    suspend fun upserts(drawings: List<NoteDrawingEntity>): List<Long>

    @Upsert
    suspend fun upsert(drawing: NoteDrawingEntity): Long

    @Query("DELETE FROM note_drawing_table WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM note_drawing_table WHERE note_id = :noteId")
    suspend fun deleteByNoteId(noteId: Long)

    @Query("SELECT * FROM note_drawing_table WHERE id = :id")
    fun get(id: Long): Flow<NoteDrawingEntity?>

    @Query("SELECT * FROM note_drawing_table")
    fun getAll(): Flow<List<NoteDrawingEntity>>

    @Query("SELECT * FROM note_drawing_table WHERE note_id = :noteId")
    fun getByNoteId(noteId: Long): Flow<List<NoteDrawingEntity>>
}
