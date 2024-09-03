package com.mshdabiola.data.repository

import com.mshdabiola.model.NotePad
import com.mshdabiola.model.NoteType
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

interface INotePadRepository {

    suspend fun upsert(notePad: NotePad): Long
    suspend fun upsert(notePads: List<NotePad>)

    suspend fun deleteCheckNote(id: Long, noteId: Long)

    suspend fun deleteNoteCheckByNoteId(noteId: Long)
    fun getNotePads(noteType: NoteType): Flow<List<NotePad>>
    fun getNotePads(): Flow<List<NotePad>>

    //    fun getNote() = generalDao.getNote().map { noteEntities -> noteEntities.map { it.toNote() } }
//
    fun getOneNotePad(id: Long): Flow<NotePad?>

    suspend fun deleteTrashType()

    suspend fun deleteNotePad(notePads: List<NotePad>)

    suspend fun delete(notePads: List<NotePad>)

    fun timeToString(time: LocalTime): String
    fun dateToString(date: LocalDate): String
    fun dateToString(long: Long): String
    suspend fun deleteVoiceNote(id: Long)

    fun saveImage(uri: String): Long
    fun saveVoice(uri: String): Long
    fun getUri(): String
}
