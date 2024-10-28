package com.mshdabiola.data.model

import com.mshdabiola.database.model.DrawPathEntity
import com.mshdabiola.database.model.FullLabel
import com.mshdabiola.database.model.LabelEntity
import com.mshdabiola.database.model.NoteCheckEntity
import com.mshdabiola.database.model.NoteEntity
import com.mshdabiola.database.model.NoteImageEntity
import com.mshdabiola.database.model.NoteLabelEntity
import com.mshdabiola.database.model.NotePadEntity
import com.mshdabiola.database.model.NoteVoiceEntity
import com.mshdabiola.model.DrawPath
import com.mshdabiola.model.Label
import com.mshdabiola.model.Note
import com.mshdabiola.model.NoteCheck
import com.mshdabiola.model.NoteImage
import com.mshdabiola.model.NoteLabel
import com.mshdabiola.model.NotePad
import com.mshdabiola.model.NoteVoice

fun DrawPathEntity.toDrawPath() = DrawPath(imageId, pathId, color, width, join, alpha, cap, paths)
fun DrawPath.toDrawPathEntity() =
    DrawPathEntity(imageId, pathId, color, width, join, alpha, cap, paths)

fun LabelEntity.toLabel() = Label(id!!, name)
fun Label.toLabelEntity() = LabelEntity(id.check(), label)

fun NoteCheckEntity.toNoteCheck() = NoteCheck(
    id = id!!,
    noteId = noteId,
    content = content,
    isCheck = isCheck,
)
fun NoteCheck.toNoteCheckEntity() = NoteCheckEntity(id.check(), noteId, content, isCheck)

fun NotePad.toNoteEntity() =
    NoteEntity(
        id.check(),
        title,
        detail,
        editDate,
        isCheck,
        color,
        background,
        isPin,
        reminder,
        interval,
        noteType,
    )

fun NoteEntity.toNote() = Note(
    id,
    title,
    detail,
    editDate,
    isCheck,
    color,
    background,
    isPin,
    reminder,
    interval,
    noteType,
)

fun NoteImage.toNoteImageEntity() = NoteImageEntity(id, noteId, isDrawing, timestamp)
fun NoteImageEntity.toNoteImage() =
    NoteImage(id = id, noteId = noteId, isDrawing = isDrawing, timestamp = timestamp)

fun NoteLabelEntity.toNoteLabel() = NoteLabel(noteId, labelId)
fun NoteLabel.toNoteLabelEntity() = NoteLabelEntity(noteId, labelId)

fun NotePadEntity.toNotePad() = NotePad(
    id = noteEntity.id!!,
    title = noteEntity.title,
    detail = noteEntity.detail,
    editDate = noteEntity.editDate,
    isCheck = noteEntity.isCheck,
    color = noteEntity.color,
    background = noteEntity.background,
    isPin = noteEntity.isPin,
    reminder = noteEntity.reminder,
    interval = noteEntity.interval,
    noteType = noteEntity.noteType,
    images = images.map { it.toNoteImage() },
    voices = voices.map { it.toNoteVoice() },
    checks = checks.map { it.toNoteCheck() },
    labels = labels.map { it.toLabel() },
)

fun FullLabel.toLabel() = label.toLabel()
fun NoteVoice.toNoteVoiceEntity() = NoteVoiceEntity(id, noteId, voiceName)
fun NoteVoiceEntity.toNoteVoice() = NoteVoice(
    id,
    noteId,
    voiceName,
    length = 89, // kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
)

fun Long.check() = if (this == -1L) null else this
