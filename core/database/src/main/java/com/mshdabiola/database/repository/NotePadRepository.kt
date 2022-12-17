package com.mshdabiola.database.repository

import com.mshdabiola.database.dao.GeneralDao
import com.mshdabiola.database.model.toNoteCheckEntity
import com.mshdabiola.database.model.toNoteEntity
import com.mshdabiola.database.model.toNoteImageEntity
import com.mshdabiola.database.model.toNotePad
import com.mshdabiola.database.model.toNoteVoiceEntity
import com.mshdabiola.model.Note
import com.mshdabiola.model.NotePad
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NotePadRepository
@Inject constructor(
    private val generalDao: GeneralDao
) {
    suspend fun insertNote(note: Note) = generalDao.addNote(note.toNoteEntity())

    suspend fun insertNotepad(notePad: NotePad): Long {

        val id = generalDao.addNote(notePad.note.toNoteEntity())
        if (notePad.checks.isNotEmpty()) {

            generalDao.addNoteCheck(notePad.checks.map { it.toNoteCheckEntity() })
        }
        if (notePad.voices.isNotEmpty()) {
            generalDao.addVoice(notePad.voices.map { it.toNoteVoiceEntity() })
        }
        if (notePad.images.isNotEmpty()) {
            generalDao.addImage(notePad.images.map { it.toNoteImageEntity() })
        }

        return id
    }

    suspend fun deleteCheckNote(id: Long) = withContext(Dispatchers.IO) {
        generalDao.deleteCheck(id)
    }


    fun getNotePads() = generalDao
        .getListOfNotePad().map { entities -> entities.map { it.toNotePad() } }

    //    fun getNote() = generalDao.getNote().map { noteEntities -> noteEntities.map { it.toNote() } }
//
    suspend fun getOneNotePad(id: Long): NotePad {
        return generalDao.getOneNotePad(id).toNotePad()
    }
}