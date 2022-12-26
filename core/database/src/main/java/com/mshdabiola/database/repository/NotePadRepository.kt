package com.mshdabiola.database.repository

import com.mshdabiola.database.dao.LabelDao
import com.mshdabiola.database.dao.NoteCheckDao
import com.mshdabiola.database.dao.NoteDao
import com.mshdabiola.database.dao.NoteImageDao
import com.mshdabiola.database.dao.NoteLabelDao
import com.mshdabiola.database.dao.NoteVoiceDao
import com.mshdabiola.database.dao.NotepadDao
import com.mshdabiola.database.model.toNoteCheckEntity
import com.mshdabiola.database.model.toNoteEntity
import com.mshdabiola.database.model.toNoteImageEntity
import com.mshdabiola.database.model.toNotePad
import com.mshdabiola.database.model.toNoteVoiceEntity
import com.mshdabiola.model.Note
import com.mshdabiola.model.NotePad
import com.mshdabiola.model.NoteType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NotePadRepository
@Inject constructor(
    private val labelDao: LabelDao,
    private val noteCheckDao: NoteCheckDao,
    private val noteDao: NoteDao,
    private val noteImageDao: NoteImageDao,
    private val noteLabelDao: NoteLabelDao,
    private val noteVoiceDao: NoteVoiceDao,
    private val notePadDao: NotepadDao
) {
    suspend fun insertNote(note: Note) = noteDao.addNote(note.toNoteEntity())

    suspend fun insertNotepad(notePad: NotePad): Long {

        val id = noteDao.addNote(notePad.note.toNoteEntity())
        if (notePad.checks.isNotEmpty()) {

            noteCheckDao.addNoteCheck(notePad.checks.map { it.toNoteCheckEntity() })
        }
        if (notePad.voices.isNotEmpty()) {
            noteVoiceDao.addVoice(notePad.voices.map { it.toNoteVoiceEntity() })
        }
        if (notePad.images.isNotEmpty()) {
            noteImageDao.addImage(notePad.images.map { it.toNoteImageEntity() })
        }

        return id
    }

    suspend fun deleteCheckNote(id: Long, noteId: Long) = withContext(Dispatchers.IO) {
        noteCheckDao.deleteCheck(id, noteId)
    }

    suspend fun deleteNoteCheckByNoteId(noteId: Long) = withContext(Dispatchers.IO) {
        noteCheckDao.deleteCheckById(noteId)
    }


    fun getNotePads(noteType: NoteType) = notePadDao
        .getListOfNotePad(noteType).map { entities -> entities.map { it.toNotePad() } }

    //    fun getNote() = generalDao.getNote().map { noteEntities -> noteEntities.map { it.toNote() } }
//
    suspend fun getOneNotePad(id: Long): NotePad {
        return notePadDao.getOneNotePad(id).toNotePad()
    }
}