package com.mshdabiola.data.repository

import com.mshdabiola.model.Note
import com.mshdabiola.model.NotePad
import com.mshdabiola.model.NoteType
import kotlinx.coroutines.flow.Flow

interface INotePadRepository {
    suspend fun insertNote(note: Note): Long

    suspend fun insertNotepad(notePad: NotePad): Long

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
}
