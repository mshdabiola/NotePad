package com.mshdabiola.database.repository

import com.mshdabiola.database.dao.LabelDao
import com.mshdabiola.database.dao.NoteCheckDao
import com.mshdabiola.database.dao.NoteDao
import com.mshdabiola.database.dao.NoteImageDao
import com.mshdabiola.database.dao.NoteLabelDao
import com.mshdabiola.database.dao.NoteVoiceDao
import com.mshdabiola.database.dao.NotepadDao
import com.mshdabiola.database.dao.PathDao
import com.mshdabiola.database.model.toNoteCheckEntity
import com.mshdabiola.database.model.toNoteEntity
import com.mshdabiola.database.model.toNoteImageEntity
import com.mshdabiola.database.model.toNoteLabelEntity
import com.mshdabiola.database.model.toNotePad
import com.mshdabiola.database.model.toNoteVoiceEntity
import com.mshdabiola.model.Note
import com.mshdabiola.model.NotePad
import com.mshdabiola.model.NoteType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
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
    private val notePadDao: NotepadDao,
    private val pathDao: PathDao
) {
    suspend fun insertNote(note: Note) = noteDao.upsert(note.toNoteEntity())

    suspend fun insertNotepad(notePad: NotePad): Long {

        val id = noteDao.upsert(notePad.note.toNoteEntity())
        if (notePad.checks.isNotEmpty()) {

            noteCheckDao.upsert(notePad.checks.map { it.toNoteCheckEntity() })
        }
        if (notePad.voices.isNotEmpty()) {
            noteVoiceDao.addVoice(notePad.voices.map { it.toNoteVoiceEntity() })
        }
        if (notePad.images.isNotEmpty()) {
            noteImageDao.upsert(notePad.images.map { it.toNoteImageEntity() })
        }
        if (notePad.labels.isNotEmpty()) {
            noteLabelDao.upsert(notePad.labels.map { it.toNoteLabelEntity() })
        }

        return id
    }

    suspend fun deleteCheckNote(id: Long, noteId: Long) = withContext(Dispatchers.IO) {
        noteCheckDao.delete(id, noteId)
    }

    suspend fun deleteNoteCheckByNoteId(noteId: Long) = withContext(Dispatchers.IO) {
        noteCheckDao.deleteByNoteId(noteId)
    }


    fun getNotePads(noteType: NoteType) = notePadDao
        .getListOfNotePad(noteType).map { entities -> entities.map { it.toNotePad() } }

    fun getNotePads() = notePadDao
        .getListOfNotePad().map { entities -> entities.map { it.toNotePad() } }

    //    fun getNote() = generalDao.getNote().map { noteEntities -> noteEntities.map { it.toNote() } }
//
    suspend fun getOneNotePad(id: Long): NotePad {
        return notePadDao.getOneNotePad(id).toNotePad()
    }

    suspend fun deleteTrashType() = withContext(Dispatchers.IO) {
        val list = getNotePads(NoteType.TRASH).first()

        delete(list)
    }

    suspend fun deleteNotePad(notePads: List<NotePad>) = withContext(Dispatchers.IO) {


        delete(notePads)
    }

    private suspend fun delete(notePads: List<NotePad>) {
        notePads.forEach {

            val id = it.note.id!!
            noteDao.delete(id)
            if (it.images.isNotEmpty())
                noteImageDao.deleteByNoteId(id)
            if (it.labels.isNotEmpty())
                noteLabelDao.deleteByNoteId(id)
            if (it.voices.isNotEmpty())
                noteVoiceDao.deleteVoiceByNoteId(id)
            if (it.checks.isNotEmpty())
                noteCheckDao.deleteByNoteId(id)

        }

        notePads
            .filter { it.images.any { it.isDrawing } }
            .map { it.images.filter { it.isDrawing } }
            .flatten()
            .forEach {
                pathDao.delete(it.id)
            }
    }


}