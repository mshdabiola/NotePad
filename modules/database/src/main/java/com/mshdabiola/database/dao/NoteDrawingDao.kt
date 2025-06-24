package com.mshdabiola.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mshdabiola.database.model.NoteDrawingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDrawingDao {
    @Upsert
    suspend fun upsert(noteDrawing: NoteDrawingEntity): Long

    @Upsert
    suspend fun upserts(noteDrawings: List<NoteDrawingEntity>): List<Long>

    @Query("DELETE FROM note_drawing_table WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT * FROM note_drawing_table WHERE id = :id")
    fun get(id: Long): Flow<NoteDrawingEntity?>

    @Query("SELECT * FROM note_drawing_table WHERE note_id = :noteId")
    fun getNoteDrawing(noteId: Long): Flow<List<NoteDrawingEntity>>
}
