package com.mshdabiola.data.repository

import com.mshdabiola.common.IContentManager
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
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
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
    private val contentManager: IContentManager,
) : INotePadRepository {

    override suspend fun upsert(notePad: NotePad): Long {
        var id = noteDao.upsert(notePad.copy(editDate = System.currentTimeMillis()).toNoteEntity())
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
        .getListOfNotePad(noteType)
        .map { entities -> entities.map { transform(it.toNotePad()) } }

    override fun getNotePads() = notePadDao
        .getListOfNotePad().map { entities -> entities.map { transform(it.toNotePad()) } }


    //    fun getNote() = generalDao.getNote().map { noteEntities -> noteEntities.map { it.toNote() } }
//
    override fun getOneNotePad(id: Long): Flow<NotePad?> {
        return notePadDao.getOneNotePad(id).map { it?.toNotePad() }
            .map { pad ->
                if (pad != null) {
                    transform(pad)
                } else null
            }
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

    private fun timeToString(time: LocalTime): String {
        val hour = when {
            time.hour > 12 -> time.hour - 12
            time.hour == 0 -> 12
            else -> time.hour
        }
        val timeset = if (time.hour > 11) "PM" else "AM"

        return "%2d : %02d %s".format(hour, time.minute, timeset)
    }

    private fun dateToString(long: Long): String {
        val date = Instant.fromEpochMilliseconds(long)
            .toLocalDateTime(TimeZone.currentSystemDefault())

        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val month =
            date.month.name.lowercase().replaceFirstChar { it.uppercaseChar() }.substring(0..2)

        return when {
            now.date == date.date -> "Today ${timeToString(date.time)} "
            date.date == now.date.plus(1, DateTimeUnit.DAY) ->
                "Tomorrow ${
                    timeToString(
                        date.time,
                    )
                }"

            date.year != now.year ->
                "$month ${date.dayOfMonth}, ${date.year} ${
                    timeToString(
                        date.time,
                    )
                }"

            else -> "$month ${date.dayOfMonth} ${timeToString(date.time)}"
        }
    }


    fun transform(pad: NotePad): NotePad{
       return pad.copy(
            reminderString = dateToString(pad.reminder),
            editDateString = dateToString(pad.editDate),
            images = pad.images.map { it.copy(path = contentManager.getImagePath(it.id)) },
            voices = pad.voices.map { it.copy(voiceName = contentManager.getVoicePath(it.id)) },

            )
    }

}
