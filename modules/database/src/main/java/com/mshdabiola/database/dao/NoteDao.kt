package com.mshdabiola.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.mshdabiola.database.model.NoteEntity
import com.mshdabiola.database.model.NotePadEntity
import com.mshdabiola.model.NoteType
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Upsert
    suspend fun upsert(noteEntity: NoteEntity): Long

    @Upsert
    suspend fun upserts(noteEntity: List<NoteEntity>): List<Long>

    @Query("DELETE FROM note_table WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM note_table WHERE id in (:ids)")
    suspend fun deleteIds(ids: Set<Long>)

    @Query("DELETE FROM note_table WHERE noteType = :noteType")
    fun deleteTrash(noteType: NoteType)

    @Transaction
    @Query("SELECT * FROM note_table WHERE noteType = :noteType ORDER BY id DESC")
    fun getByNoteType(noteType: NoteType): Flow<List<NotePadEntity>>

//    @Transaction
//    @Query("SELECT * FROM note_table WHERE reminder > 0 ORDER BY id DESC")
//    fun getListOfNotePadByReminder(): Flow<List<NotePadEntity>>

    @Transaction
    @Query("SELECT * FROM note_table ORDER BY id DESC")
    fun getAll(): Flow<List<NotePadEntity>>

    @Transaction
    @Query("SELECT * FROM note_table WHERE id = :noteId")
    fun get(noteId: Long): Flow<NotePadEntity?>

    @Transaction
    @Query("SELECT * FROM note_table WHERE id IN (:ids)") // Use IN operator and match parameter name
    fun getByIds(ids: Set<Long>): Flow<List<NotePadEntity>> // Define a return type
}
