package com.mshdabiola.detail

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import com.mshdabiola.designsystem.icon.NoteIcon
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationBottomSheet(
    onAlarm: (Long, Long?) -> Unit = { _, _ -> },
    showDialog: () -> Unit = {},
    show: Boolean,
    currentColor: Int,
    currentImage: Int,
    onDismissRequest: () -> Unit,
) {
    val background = if (currentImage != -1) {
        NoteIcon.background[currentImage].fgColor
    } else {
        if (currentColor != -1) {
            NoteIcon.noteColors[currentColor]
        } else {
            MaterialTheme.colorScheme.surface
        }
    }
    val dateTime = remember {
        Clock.System.now().toLocalDateTime(TimeZone.UTC)
    }

    val morning = remember {
        LocalDateTime(dateTime.date, LocalTime(8, 0, 0))
    }

    val evening = remember {
        LocalDateTime(dateTime.date, LocalTime(22, 0, 0))
    }
    val morningTom = remember {
        LocalDateTime(dateTime.date.plus(DateTimeUnit.DAY), LocalTime(8, 0, 0))
    }
    val eveningTom = remember {
        LocalDateTime(dateTime.date.plus(DateTimeUnit.DAY), LocalTime(22, 0, 0))
    }

    val nextWk = remember {
        LocalDateTime(dateTime.date.plus(DateTimeUnit.WEEK), LocalTime(8, 0, 0))
    }

    val pastToday = remember {
        dateTime > morning && dateTime > evening
    }

    val dayOfWeek = remember {
        nextWk.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }
    }

    // 7.22pm,19.22
    // if now 19.22> morning 7
    // later today 10pm22/tomorrow morning 7am
    // Tomorrow morning 10am/Tomorrow evening 7pm 19
    if (show) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            containerColor = background,
        ) {
            NotificationItem(
                title = if (pastToday) "Tomorrow morning" else "Later today",
                time = if (pastToday) morning.toTimeString() else evening.toTimeString(),
                onClick = {
                    onDismissRequest()

                    val time = if (pastToday) {
                        morningTom.toInstant(TimeZone.currentSystemDefault())
                            .toEpochMilliseconds()
                    } else {
                        evening.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
                    }

                    onAlarm(time, null)
                },
            )
            NotificationItem(
                title = if (pastToday) "Tomorrow evening" else "Tomorrow morning",
                time = if (pastToday) evening.toTimeString() else morning.toTimeString(),
                onClick = {
                    onDismissRequest()

                    val time = if (pastToday) {
                        eveningTom.toInstant(TimeZone.currentSystemDefault())
                            .toEpochMilliseconds()
                    } else {
                        morningTom.toInstant(TimeZone.currentSystemDefault())
                            .toEpochMilliseconds()
                    }

                    onAlarm(time, null)
                },

            )
            NotificationItem(
                title = "$dayOfWeek morning",
                time = "${dayOfWeek.subSequence(0..2)} ${nextWk.toTimeString()}",
                onClick = {
                    onDismissRequest()
                    onAlarm(
                        nextWk.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
                        null,
                    )
                },
            )
            NotificationItem(
                title = "Pick a date & time",
                time = "",
                onClick = {
                    showDialog()
                    onDismissRequest()
                },
            )
        }
    }
}

@Composable
fun NotificationItem(
    icon: ImageVector = Icons.Outlined.AccessTime,
    title: String,
    time: String,
    onClick: () -> Unit = {},
) {
    DropdownMenuItem(
        leadingIcon = {
            Icon(imageVector = icon, contentDescription = "time")
        },
        text = { Text(text = title) },
        onClick = onClick,
        trailingIcon = { Text(text = time) },
    )
}

fun LocalDateTime.toTimeString(): String {
    val hr = hour % 12L
    val a = if (hour > 11) "PM" else "AM"
    return "%02d : %02d %s".format(hr, minute, a)
}
