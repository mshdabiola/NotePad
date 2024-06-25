package com.mshdabiola.data.model

import com.mshdabiola.database.model.DrawPathEntity
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

fun LabelEntity.toLabel() = Label(id, name)
fun Label.toLabelEntity() = LabelEntity(id, label)

fun NoteCheckEntity.toNoteCheck() = NoteCheck(id, noteId, content, isCheck)
fun NoteCheck.toNoteCheckEntity() = NoteCheckEntity(id, noteId, content, isCheck)

fun Note.toNoteEntity() =
    NoteEntity(
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
fun NoteImageEntity.toNoteImage() = NoteImage(id, noteId, isDrawing, timestamp)

fun NoteLabelEntity.toNoteLabel() = NoteLabel(noteId, labelId)
fun NoteLabel.toNoteLabelEntity() = NoteLabelEntity(noteId, labelId)

fun NotePadEntity.toNotePad() = NotePad(
    note = noteEntity.toNote(),
    images = images.map { it.toNoteImage() },
    voices = voices.map { it.toNoteVoice() },
    checks = checks.map { it.toNoteCheck() },
    labels = labels.map { it.toNoteLabel() },
)

fun NoteVoice.toNoteVoiceEntity() = NoteVoiceEntity(id, noteId, voiceName)
fun NoteVoiceEntity.toNoteVoice() = NoteVoice(id, noteId, voiceName)
