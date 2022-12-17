package com.mshdabiola.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.mshdabiola.database.model.NoteCheckEntity
import com.mshdabiola.database.model.NoteEntity
import com.mshdabiola.database.model.NoteImageEntity
import com.mshdabiola.database.model.NotePadEntity
import com.mshdabiola.database.model.NoteVoiceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GeneralDao {

    @Upsert
    suspend fun addVoice(noteVoiceEntity: List<NoteVoiceEntity>)

    @Insert
    suspend fun addImage(noteImageEntity: List<NoteImageEntity>)

    @Upsert
    suspend fun addNoteCheck(noteCheckEntity: List<NoteCheckEntity>)

    @Upsert
    suspend fun addNote(noteEntity: NoteEntity): Long

    @Query("DELETE FROM note_voice_table WHERE id = :id")
    suspend fun deleteVoiceOne(id: Long)

    @Query("DELETE FROM note_voice_table WHERE noteId = :noteId")
    suspend fun deleteVoiceById(noteId: Long)

    @Query("DELETE FROM note_image_table WHERE id = :id")
    suspend fun deleteImageOne(id: Long)

    @Query("DELETE FROM note_image_table WHERE noteId = :noteId")
    suspend fun deleteImageById(noteId: Long)

    @Query("DELETE FROM note_check_table WHERE noteId = :noteId")
    suspend fun deleteCheckById(noteId: Long)

    @Query("DELETE FROM note_check_table WHERE id = :id")
    suspend fun deleteCheck(id: Long)

    @Query("DELETE FROM note_table WHERE id = :noteId")
    suspend fun deleteNote(noteId: Long)

    @Transaction
    @Query("SELECT * FROM note_table")
    fun getListOfNotePad(): Flow<List<NotePadEntity>>

    @Transaction
    @Query("SELECT * FROM note_table WHERE id = :noteId")
    suspend fun getOneNotePad(noteId: Long): NotePadEntity

//    @Query("SELECT * FROM note_table ORDER BY id DESC")
//    fun getNote(): Flow<List<NoteEntity>>
//
//    @Query("SELECT * FROM note_table WHERE id = :id ")
//    suspend fun getOneNote(id: Long): NoteEntity


}