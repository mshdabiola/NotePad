package com.mshdabiola.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mshdabiola.database.model.NoteImageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteImageDao {

    @Upsert
    suspend fun upserts(images: List<NoteImageEntity>): List<Long>

    @Upsert
    suspend fun upsert(image: NoteImageEntity): Long

    @Query("DELETE FROM note_image_table WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM note_image_table WHERE noteId = :noteId")
    suspend fun deleteByNoteId(noteId: Long)

    @Query("SELECT * FROM note_image_table WHERE id = :id")
    fun get(id: Long): Flow<NoteImageEntity?>

    @Query("SELECT * FROM note_image_table")
    fun getAll(): Flow<List<NoteImageEntity>>

    @Query("SELECT * FROM note_image_table WHERE noteId = :noteId")
    fun getByNoteId(noteId: Long): Flow<List<NoteImageEntity>>
}
