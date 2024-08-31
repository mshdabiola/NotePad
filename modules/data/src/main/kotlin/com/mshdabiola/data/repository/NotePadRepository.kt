package com.mshdabiola.data.repository

import com.mshdabiola.data.model.toNoteCheckEntity
import com.mshdabiola.data.model.toNoteEntity
import com.mshdabiola.data.model.toNoteImageEntity
import com.mshdabiola.data.model.toNotePad
import com.mshdabiola.data.model.toNoteVoiceEntity
import com.mshdabiola.database.dao.NoteCheckDao
import com.mshdabiola.database.dao.NoteDao
import com.mshdabiola.database.dao.NoteImageDao
import com.mshdabiola.database.dao.NoteLabelDao
import com.mshdabiola.database.dao.NoteVoiceDao
import com.mshdabiola.database.dao.NotepadDao
import com.mshdabiola.database.dao.PathDao
import com.mshdabiola.database.model.NoteLabelEntity
import com.mshdabiola.model.NotePad
import com.mshdabiola.model.NoteType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class NotePadRepository
@Inject constructor(
    private val noteCheckDao: NoteCheckDao,
    private val noteDao: NoteDao,
    private val noteImageDao: NoteImageDao,
    private val noteLabelDao: NoteLabelDao,
    private val noteVoiceDao: NoteVoiceDao,
    private val notePadDao: NotepadDao,
    private val pathDao: PathDao,
) : INotePadRepository {

    override suspend fun upsert(notePad: NotePad): Long {
        var id = noteDao.upsert(notePad.toNoteEntity())
        if (id == -1L) {
            id = notePad.id
        }
        noteCheckDao.upsert(notePad.checks.map { it.copy(noteId = id).toNoteCheckEntity() })

        noteVoiceDao.addVoice(notePad.voices.map { it.copy(noteId = id).toNoteVoiceEntity() })

        noteImageDao.upsert(notePad.images.map { it.copy(noteId = id).toNoteImageEntity() })

        noteLabelDao.upsert(notePad.labels.map { NoteLabelEntity(id, it.id) })

        return id
    }

    override suspend fun upsert(notePads: List<NotePad>) {
        notePads.forEach {
            upsert(it)
        }
    }

    override suspend fun deleteCheckNote(id: Long, noteId: Long) = withContext(Dispatchers.IO) {
        noteCheckDao.delete(id, noteId)
    }

    override suspend fun deleteNoteCheckByNoteId(noteId: Long) = withContext(Dispatchers.IO) {
        noteCheckDao.deleteByNoteId(noteId)
    }

    override fun getNotePads(noteType: NoteType) = notePadDao
        .getListOfNotePad(noteType).map { entities -> entities.map { it.toNotePad() } }

    override fun getNotePads() = notePadDao
        .getListOfNotePad().map { entities -> entities.map { it.toNotePad() } }

    //    fun getNote() = generalDao.getNote().map { noteEntities -> noteEntities.map { it.toNote() } }
//
    override fun getOneNotePad(id: Long): Flow<NotePad?> {
        return notePadDao.getOneNotePad(id).map { it?.toNotePad() }
    }

    override suspend fun deleteTrashType() = withContext(Dispatchers.IO) {
        val list = getNotePads(NoteType.TRASH).first()

        delete(list)
    }

    override suspend fun deleteNotePad(notePads: List<NotePad>) = withContext(Dispatchers.IO) {
        delete(notePads)
    }

    override suspend fun delete(notePads: List<NotePad>) {
        notePads.forEach {
            val id = it.id
            noteDao.delete(id)
            if (it.images.isNotEmpty()) {
                noteImageDao.deleteByNoteId(id)
            }
            if (it.labels.isNotEmpty()) {
                noteLabelDao.deleteByNoteId(id)
            }
            if (it.voices.isNotEmpty()) {
                noteVoiceDao.deleteVoiceByNoteId(id)
            }
            if (it.checks.isNotEmpty()) {
                noteCheckDao.deleteByNoteId(id)
            }
        }

        notePads
            .filter { it -> it.images.any { it.isDrawing } }
            .map { it -> it.images.filter { it.isDrawing } }
            .flatten()
            .forEach {
                pathDao.delete(it.id)
            }
    }
}
