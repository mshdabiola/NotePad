package com.mshdabiola.data.repository

import android.media.MediaMetadataRetriever
import androidx.core.net.toUri
import com.mshdabiola.common.IContentManager
import com.mshdabiola.data.model.toEntity
import com.mshdabiola.data.model.toNoteCheckEntity
import com.mshdabiola.data.model.toNoteEntity
import com.mshdabiola.data.model.toNoteImageEntity
import com.mshdabiola.data.model.toNotePad
import com.mshdabiola.data.model.toNoteVoiceEntity
import com.mshdabiola.database.dao.NoteCheckDao
import com.mshdabiola.database.dao.NoteDao
import com.mshdabiola.database.dao.NoteDrawingDao
import com.mshdabiola.database.dao.NoteImageDao
import com.mshdabiola.database.dao.NoteLabelDao
import com.mshdabiola.database.dao.NoteVoiceDao
import com.mshdabiola.database.dao.NotepadDao
import com.mshdabiola.database.dao.NotificationDao
import com.mshdabiola.database.model.NoteLabelEntity
import com.mshdabiola.model.NoteDisplayCategory
import com.mshdabiola.model.NotePad
import com.mshdabiola.model.NoteType
import com.mshdabiola.model.NoteUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
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
    private val noteDrawingDao: NoteDrawingDao,
    private val notificationDao: NotificationDao,
    private val contentManager: IContentManager,
) : INotePadRepository {

    override suspend fun upsert(notePad: NotePad): Long {
        var id = noteDao.upsert(
            notePad.copy(
                editDate =
                Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
            ).toNoteEntity(),
        )
        if (id == -1L) {
            id = notePad.id
        }
        notePad.notification?.let {
            notificationDao.upsertNotification(it.toEntity(id))
        }
        noteCheckDao.upsert(notePad.checks.map { it.copy(noteId = id).toNoteCheckEntity() })

        noteVoiceDao.addVoice(notePad.voices.map { it.copy(noteId = id).toNoteVoiceEntity() })

        noteImageDao.upsert(notePad.images.map { it.copy(noteId = id).toNoteImageEntity() })

        noteDrawingDao.upserts(notePad.drawing.map { it.toEntity() })

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

    override fun getNotePadsWithMainData(noteDisplayCategory: NoteDisplayCategory): Flow<List<NotePad>> {
        return when (noteDisplayCategory.noteType) {
            NoteType.LABEL -> {
                notePadDao.getListOfNotePad()
                    .map { notes ->

                        notes
                            .filter {
                                it.noteEntity.noteType != NoteType.TRASH ||
                                    it.noteEntity.noteType != NoteType.ARCHIVE
                            }
                            .filter { note ->
                                note.labels
                                    .any { it.label.id == noteDisplayCategory.labelId }
                            }
                    }
            }

            NoteType.REMINDER -> {
                notePadDao.getListOfNotePad()
                    .map {
                        it.filter {
                            it.notification != null
                        }
                    }
            }

            else -> notePadDao.getListOfNotePadByNoteType(noteDisplayCategory.noteType)
        }
            .map { entities -> entities.map { transform(it.toNotePad()) } }
    }
//
//    override fun getNotePads(noteType: NoteType) = notePadDao
//        .getListOfNotePad(noteType)
//        .map { entities -> entities.map { transform(it.toNotePad()) } }

    override fun getNotePads() = notePadDao
        .getListOfNotePad().map { entities -> entities.map { transform(it.toNotePad()) } }

    override fun getNotePadsByIds(ids: Set<Long>): Flow<List<NotePad>> {
        return notePadDao.getByIds(ids).map { entities -> entities.map { transform(it.toNotePad()) } }
    }

    //    fun getNote() = generalDao.getNote().map { noteEntities -> noteEntities.map { it.toNote() } }
//
    override fun getOneNotePad(id: Long): Flow<NotePad?> {
        return notePadDao.getOneNotePad(id).map { it?.toNotePad() }
            .map { pad ->
                if (pad != null) {
                    transform(pad)
                } else {
                    null
                }
            }
    }

    override suspend fun deleteTrashType() = withContext(Dispatchers.IO) {
        noteDao.deleteTrash(NoteType.TRASH)
    }

    override suspend fun delete(ids: Set<Long>) {
        withContext(Dispatchers.IO) {
            noteDao.delete(ids)
        }
    }

    override fun timeToString(time: LocalTime): String {
        val hour = when {
            time.hour > 12 -> time.hour - 12
            time.hour == 0 -> 12
            else -> time.hour
        }
        val timeset = if (time.hour > 11) "PM" else "AM"

        return "%2d : %02d %s".format(hour, time.minute, timeset)
    }

    override fun dateToString(date: LocalDate): String {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val month = date.month.name.lowercase().replaceFirstChar { it.uppercaseChar() }

        return when {
            now.date == date -> "Today"
            date == now.date.plus(1, DateTimeUnit.DAY) -> "Tomorrow"
            date.year != now.year -> "$month ${date.dayOfMonth}, ${date.year}"
            else -> "$month ${date.dayOfMonth}"
        }
    }

    override fun dateToString(long: Long): String {
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

    override suspend fun deleteVoiceNote(id: Long) {
        noteVoiceDao.deleteVoiceOne(id)
    }

    override fun saveImage(uri: String): Long {
        return contentManager.saveImage(uri)
    }

    override fun saveVoice(uri: String): Long {
        return contentManager.saveVoice(uri)
    }

    override fun getUri(): String {
        return contentManager.pictureUri()
    }

    override fun getVoicePath(id: Long): String {
        return contentManager.getVoicePath(id)
    }

    override fun getImagePath(id: Long): String {
        return contentManager.getImagePath(id)
    }

    override suspend fun deleteImageNote(id: Long) {
        noteImageDao.deleteById(id)
    }

    private fun transform(pad: NotePad): NotePad {
        return pad.copy(

            images = pad.images.map { it.copy(path = contentManager.getImagePath(it.id)) },
            voices = pad.voices.map {
                it.copy(
                    voiceName = contentManager.getVoicePath(it.id),
                    length = getAudioLength(contentManager.getVoicePath(it.id)),
                )
            },
            uris = getUriFromDetail(pad.detail),

        )
    }

    private fun getAudioLength(filePath: String): Long {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(filePath)
            return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.toLongOrNull() ?: 0
        } catch (e: IllegalArgumentException) {
            // Handle the exception, log error, etc.
            e.printStackTrace()
            return 0
        } finally {
            retriever.release()
        }
    }

    private val regex =
        "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)"

    private fun getUriFromDetail(detail: String): List<NoteUri> {
        return if (detail.contains(regex.toRegex())) {
            detail.split("\\s".toRegex())
                .filter { it.trim().matches(regex.toRegex()) }
                .mapIndexed { index, s ->
                    val path = s.toUri().authority ?: ""
                    val icon = "https://icon.horse/icon/$path"
                    NoteUri(
                        id = index,
                        icon = icon,
                        path = path,
                        uri = s,
                    )
                }
        } else {
            emptyList()
        }
    }
}
