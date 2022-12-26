package com.mshdabiola.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.mshdabiola.database.model.NotePadEntity
import com.mshdabiola.model.NoteType
import kotlinx.coroutines.flow.Flow

@Dao
interface NotepadDao {
    @Transaction
    @Query("SELECT * FROM note_table WHERE noteType = :noteType ORDER BY id DESC")
    fun getListOfNotePad(noteType: NoteType): Flow<List<NotePadEntity>>

    @Transaction
    @Query("SELECT * FROM note_table WHERE id = :noteId")
    suspend fun getOneNotePad(noteId: Long): NotePadEntity
}