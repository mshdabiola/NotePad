package com.mshdabiola.data.model

import com.mshdabiola.database.model.LabelEntity
import com.mshdabiola.database.model.NoteCheckEntity
import com.mshdabiola.database.model.NoteDrawingEntity
import com.mshdabiola.database.model.NoteEntity
import com.mshdabiola.database.model.NoteImageEntity
import com.mshdabiola.database.model.NoteLabelEntity
import com.mshdabiola.database.model.NoteVoiceEntity
import com.mshdabiola.database.model.NotificationEntity
import com.mshdabiola.model.Converter
import com.mshdabiola.model.IntervalEnd
import com.mshdabiola.model.Label
import com.mshdabiola.model.Note
import com.mshdabiola.model.NoteCheck
import com.mshdabiola.model.NoteDrawing
import com.mshdabiola.model.NoteImage
import com.mshdabiola.model.NoteLabel
import com.mshdabiola.model.NoteVoice
import com.mshdabiola.model.NotificationInterval
import com.mshdabiola.model.NotificationPlace
import com.mshdabiola.model.NotificationUiState
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

fun NoteDrawingEntity.toDrawing(): NoteDrawing {
    return NoteDrawing(
        id = id!!,
        noteId = noteId,
        drawingPaths = paths?.let { Converter.toPath(it) } ?: emptyList(),

    )
}

fun NoteDrawing.toEntity(): NoteDrawingEntity {
    return NoteDrawingEntity(
        id = id.check(),
        noteId = noteId,
        paths = if (drawingPaths.isEmpty()) {
            null
        } else {
            Converter.pathToString(drawingPaths)
        },
    )
}

fun LabelEntity.toLabel() = Label(id!!, name)
fun Label.toLabelEntity() = LabelEntity(id.check(), label)

fun NoteCheckEntity.toNoteCheck() = NoteCheck(
    id = id!!,
    noteId = noteId,
    content = content,
    isCheck = isCheck,
)

fun NoteCheck.toNoteCheckEntity() = NoteCheckEntity(id.check(), noteId, content, isCheck)

fun Note.asEntity() = NoteEntity(
    id.check(),
    title,
    detail,
    editDate,
    isCheck,
    color,
    background,
    isPin,
    noteType,
)

fun NoteEntity.toNote() = Note(
    id!!,
    title,
    detail,
    editDate,
    isCheck,
    color,
    background,
    isPin,
    noteType,
)

fun NoteImage.toNoteImageEntity() = NoteImageEntity(id, noteId)
fun NoteImageEntity.toNoteImage() =
    NoteImage(id = id, noteId = noteId)

fun NoteLabelEntity.toNoteLabel() = NoteLabel(noteId, labelId)
fun NoteLabel.toNoteLabelEntity() = NoteLabelEntity(noteId, labelId)

fun NoteVoice.toNoteVoiceEntity() = NoteVoiceEntity(id, noteId, voiceName)
fun NoteVoiceEntity.toNoteVoice() = NoteVoice(
    id,
    noteId,
    voiceName,
    length = 89, // kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
)

// --- Mapper from NotificationUiState to NotificationEntity ---
fun NotificationUiState.toEntity(): NotificationEntity {
    val reminderTimestamp =
        this.currentDateTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()

    val placeType: Int
    val customPlaceName: String?
    when (this.currentPlace) {
        NotificationPlace.Home -> {
            placeType = 0
            customPlaceName = null
        }

        NotificationPlace.Work -> {
            placeType = 1
            customPlaceName = null
        }

        NotificationPlace.School -> {
            placeType = 2
            customPlaceName = null
        }

        is NotificationPlace.Edit -> {
            placeType = 3
            customPlaceName = (this.currentPlace as NotificationPlace.Edit).place
        }

        null -> { // Handle cases where place might not be set, map to a default or error
            placeType = -1 // Or some other indicator for "no place" if needed
            customPlaceName = null
        }
    }

    var typeIndexValue = 0
    var intervalValueStr = "1"
    var weeklyDaysStr: String? = null
    var monthlySameDayBool: Boolean? = null
    var intervalEndTypeIndexValue = 0
    var endDateEpochDayValue: Long? = null
    var numberOfTimesValue: Int? = null

    when (val interval = this.currentInterval) {
        is NotificationInterval.DoNotRepeat -> {
            typeIndexValue = 0
        }

        is NotificationInterval.Daily -> {
            typeIndexValue = 1
            intervalValueStr = interval.interval
            when (val end = interval.intervalEnd) {
                IntervalEnd.Forever -> intervalEndTypeIndexValue = 0
                is IntervalEnd.EndDate -> {
                    intervalEndTypeIndexValue = 1
                    endDateEpochDayValue = end.date.toEpochDays().toLong()
                }

                is IntervalEnd.NumberOfTimes -> {
                    intervalEndTypeIndexValue = 2
                    numberOfTimesValue = end.times
                }
            }
        }

        is NotificationInterval.Weekly -> {
            typeIndexValue = 2
            intervalValueStr = interval.interval
            weeklyDaysStr = interval.days.joinToString(",")
            when (val end = interval.intervalEnd) {
                IntervalEnd.Forever -> intervalEndTypeIndexValue = 0
                is IntervalEnd.EndDate -> {
                    intervalEndTypeIndexValue = 1
                    endDateEpochDayValue = end.date.toEpochDays().toLong()
                }

                is IntervalEnd.NumberOfTimes -> {
                    intervalEndTypeIndexValue = 2
                    numberOfTimesValue = end.times
                }
            }
        }

        is NotificationInterval.Monthly -> {
            typeIndexValue = 3
            intervalValueStr = interval.interval
            monthlySameDayBool = interval.sameDay
            when (val end = interval.intervalEnd) {
                IntervalEnd.Forever -> intervalEndTypeIndexValue = 0
                is IntervalEnd.EndDate -> {
                    intervalEndTypeIndexValue = 1
                    endDateEpochDayValue = end.date.toEpochDays().toLong()
                }

                is IntervalEnd.NumberOfTimes -> {
                    intervalEndTypeIndexValue = 2
                    numberOfTimesValue = end.times
                }
            }
        }

        is NotificationInterval.Yearly -> {
            typeIndexValue = 4
            intervalValueStr = interval.interval
            when (val end = interval.intervalEnd) {
                IntervalEnd.Forever -> intervalEndTypeIndexValue = 0
                is IntervalEnd.EndDate -> {
                    intervalEndTypeIndexValue = 1
                    endDateEpochDayValue = end.date.toEpochDays().toLong()
                }

                is IntervalEnd.NumberOfTimes -> {
                    intervalEndTypeIndexValue = 2
                    numberOfTimesValue = end.times
                }
            }
        }

        is NotificationInterval.Custom -> { // Ensure your NotificationInterval.Custom has necessary fields
            typeIndexValue = 5
            // Populate fields based on NotificationInterval.Custom structure
        }
    }

    return NotificationEntity(
        id = noteId, // Use 0 for new, or pass existing ID for updates
        noteId = noteId,
        reminderDateTimeStamp = reminderTimestamp,
        placeType = placeType,
        customPlaceName = customPlaceName,
        typeIndex = typeIndexValue,
        intervalValue = intervalValueStr,
        weeklyDays = weeklyDaysStr,
        monthlySameDay = monthlySameDayBool,
        intervalEndTypeIndex = intervalEndTypeIndexValue,
        endDateEpochDay = endDateEpochDayValue,
        numberOfTimes = numberOfTimesValue,
    )
}

// --- Mapper from NotificationEntity to NotificationUiState ---
fun NotificationEntity.toNotificationUiState(): NotificationUiState {
    val currentDateTime = Instant.fromEpochMilliseconds(this.reminderDateTimeStamp)
        .toLocalDateTime(TimeZone.currentSystemDefault())

    val currentPlace: NotificationPlace? = when (this.placeType) {
        0 -> NotificationPlace.Home
        1 -> NotificationPlace.Work
        2 -> NotificationPlace.School
        3 -> NotificationPlace.Edit(this.customPlaceName ?: "")
        else -> null // Or handle error/default for unknown placeType
    }

    val intervalEnd = when (this.intervalEndTypeIndex) {
        0 -> IntervalEnd.Forever
        1 -> IntervalEnd.EndDate(LocalDate.fromEpochDays(this.endDateEpochDay!!.toInt())) // Ensure not null
        2 -> IntervalEnd.NumberOfTimes(this.numberOfTimes!!) // Ensure not null
        else -> IntervalEnd.Forever // Default or error handling
    }

    val currentInterval: NotificationInterval = when (this.typeIndex) {
        0 -> NotificationInterval.DoNotRepeat
        1 -> NotificationInterval.Daily(
            interval = this.intervalValue,
            intervalEnd = intervalEnd,
        )

        2 -> NotificationInterval.Weekly(
            interval = this.intervalValue,
            days = this.weeklyDays?.split(',')?.mapNotNull { it.toIntOrNull() }?.toSet()
                ?: emptySet(),
            intervalEnd = intervalEnd,
        )

        3 -> NotificationInterval.Monthly(
            interval = this.intervalValue,
            sameDay = this.monthlySameDay ?: false, // Provide default if null
            intervalEnd = intervalEnd,
        )

        4 -> NotificationInterval.Yearly(
            interval = this.intervalValue,
            intervalEnd = intervalEnd,
        )

        5 -> NotificationInterval.Custom // Ensure your NotificationInterval.Custom can be reconstructed
        else -> NotificationInterval.DoNotRepeat // Default or error handling
    }

    return NotificationUiState(
        currentDateTime = currentDateTime,
        currentInterval = currentInterval,
        currentPlace = currentPlace,
    )
}

fun Long.check() = if (this == -1L) null else this
