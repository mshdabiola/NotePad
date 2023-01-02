package com.mshdabiola.editscreen.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mshdabiola.bottomsheet.ModalBottomSheet
import com.mshdabiola.bottomsheet.ModalState
import com.mshdabiola.bottomsheet.rememberModalState
import com.mshdabiola.designsystem.icon.NoteIcon
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime


@Composable
fun NotificationBottomSheet(
    modalState: ModalState,
    onAlarm: (Long, Long?) -> Unit = { _, _ -> },
    showDialog: () -> Unit = {}
) {

    val coroutineScope = rememberCoroutineScope()

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

    //7.22pm,19.22
    //if now 19.22> morning 7
    //later today 10pm22/tomorrow morning 7am
    //Tomorrow morning 10am/Tomorrow evening 7pm 19
    ModalBottomSheet(modalState = modalState) {
        Surface(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(
                    bottom = 36.dp,
                    start = 16.dp,
                    end = 16.dp,
                    top = 8.dp
                )
            ) {
                NotificationItem(icon = NoteIcon.Time,
                    title = if (pastToday) "Tomorrow morning" else "Later today",
                    time = if (pastToday) morning.toTimeString() else evening.toTimeString(),
                    onClick = {
                        coroutineScope.launch { modalState.hide() }

                        val time = if (pastToday)
                            morningTom.toInstant(TimeZone.UTC).toEpochMilliseconds()
                        else
                            evening.toInstant(TimeZone.UTC).toEpochMilliseconds()

                        onAlarm(time, null)
                    }
                )
                NotificationItem(
                    icon = NoteIcon.Time,
                    title = if (pastToday) "Tomorrow evening" else "Tomorrow morning",
                    time = if (pastToday) evening.toTimeString() else morning.toTimeString(),
                    onClick = {
                        coroutineScope.launch { modalState.hide() }

                        val time = if (pastToday)
                            eveningTom.toInstant(TimeZone.UTC).toEpochMilliseconds()
                        else
                            morningTom.toInstant(TimeZone.UTC).toEpochMilliseconds()

                        onAlarm(time, null)
                    }

                )
                NotificationItem(
                    icon = NoteIcon.Time,
                    title = "$dayOfWeek morning",
                    time = "${dayOfWeek.subSequence(0..2)} ${nextWk.toTimeString()}",
                    onClick = {
                        onAlarm(nextWk.toInstant(TimeZone.UTC).toEpochMilliseconds(), null)
                    }
                )
                NotificationItem(
                    icon = NoteIcon.Time,
                    title = "Pick a date & time",
                    time = "",
                    onClick = {

                        showDialog()
                        coroutineScope.launch { modalState.hide() }
                    }
                )
            }
        }

    }
}

@Composable
fun NotificationItem(
    icon: Int,
    title: String,
    time: String,
    onClick: () -> Unit = {}
) {
    Row(
        Modifier
            .clickable { onClick() }
            .fillMaxWidth()
            .height(36.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(painter = painterResource(id = icon), contentDescription = "")
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, Modifier.weight(1f))
        Text(text = time)
    }
}


@Preview
@Composable
fun NotificationBottomSheetPreview() {
    NotificationBottomSheet(
        modalState = rememberModalState(),
    )
}

fun LocalDateTime.toTimeString(): String {
    val hr = hour % 12L
    val a = if (hour > 11) "PM" else "AM"
    return "%02d : %02d %s".format(hr, minute, a)
}
