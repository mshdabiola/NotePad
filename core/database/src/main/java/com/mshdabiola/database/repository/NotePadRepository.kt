package com.mshdabiola.database.repository

import com.mshdabiola.database.dao.GeneralDao
import com.mshdabiola.database.model.toNoteEntity
import com.mshdabiola.database.model.toNotePad
import com.mshdabiola.model.Note
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NotePadRepository
@Inject constructor(
    private val generalDao: GeneralDao
) {
    suspend fun insertNote(note: Note) = generalDao.addNote(note.toNoteEntity())

    suspend fun getNotePad(noteId: Long) = generalDao.getOneNotePad(noteId).toNotePad()


    fun getNotePads() = generalDao
        .getListOfNotePad().map { entities -> entities.map { it.toNotePad() } }

//    fun getNote() = generalDao.getNote().map { noteEntities -> noteEntities.map { it.toNote() } }
//
//    suspend fun getOneNotePad(id: Long): Note {
//        return generalDao.getOneNote(id).toNote()
//    }
}