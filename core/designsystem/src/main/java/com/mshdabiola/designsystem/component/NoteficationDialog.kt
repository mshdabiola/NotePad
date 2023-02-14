package com.mshdabiola.designsystem.component

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import kotlin.time.DurationUnit

@Composable
fun NotificationDialog(
    showDialog: Boolean = true,
    onDismissRequest: () -> Unit = {},
    remainder: Long = -1,
    interval: Long? = null,
    onSetAlarm: (Long, Long?) -> Unit = { _, _ -> },
    onDeleteAlarm: () -> Unit = {},
) {
    val now = remember {
        Clock.System.now()
    }
    var showDate by remember {
        mutableStateOf(false)
    }
    var showTime by remember {
        mutableStateOf(false)
    }
    var dateTime by remember(remainder) {
        val time = if (remainder > 0) {
            Instant.fromEpochMilliseconds(remainder)
                .toLocalDateTime(TimeZone.currentSystemDefault())
        } else {
            now.toLocalDateTime(TimeZone.currentSystemDefault())
        }

        mutableStateOf(time)
    }
    var inter by remember(interval) {
        mutableStateOf(interval)
    }
    val context = LocalContext.current

    AnimatedVisibility(visible = showDialog) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text(text = if (remainder > 0) "Edit Reminder" else "Add Reminder") },
            text = {
                TimeContent(
                    inter,
                    dateTime,
                    onDateChange = {
                        if (it.time == LocalTime(0, 0)) {

                          showTime=true
                        } else {
                            if (it.date == LocalDate(1993, 1, 1)) {
                                showDate=true
                            } else {
                                dateTime = it
                            }
                        }
                    },
                    onIntervalChange = {
                        inter = it
                    },
                )
            },
            confirmButton = {
                Button(onClick = {
                    val va = dateTime.toInstant(TimeZone.UTC).toEpochMilliseconds()
                    if (va > now.toEpochMilliseconds()) {
                        onDismissRequest()
                        onSetAlarm(
                            dateTime.toInstant(TimeZone.currentSystemDefault())
                                .toEpochMilliseconds(),
                            inter,
                        )
                    }
                }) {
                    Text(text = "Save")
                }
            },
            dismissButton = {
                Row {
                    if (remainder > 0) {
                        TextButton(onClick = {
                            onDismissRequest()
                            onDeleteAlarm()
                        }) {
                            Text(text = "Delete")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    TextButton(onClick = { onDismissRequest() }) {
                        Text(text = "Cancel")
                    }
                }
            },
        )
    }

    TimeDialog(
        showDialog=showTime,
        hour = dateTime.hour,
        minute = dateTime.minute,
        onDismissRequest = {showTime=false},
        onSetTime = {dateTime= LocalDateTime(dateTime.date,it) }
    )
    DateDialog(
        showDialog=showDate,
       currentDate = dateTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
        onDismissRequest = {showDate=false},
        onSetDate = {

            dateTime=LocalDateTime(it,dateTime.time)}
    )
}

@Preview
@Composable
fun NotificationDialogPreview() {
    NotificationDialog(
        remainder = LocalDateTime(2023,2,14,12,30).toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
    )
}

@Composable
fun TimeContent(
    interval: Long? = null,
    dateTime: LocalDateTime,
    onDateChange: (LocalDateTime) -> Unit = {},
    onIntervalChange: (Long?) -> Unit = {},
) {
    val instant = dateTime.toInstant(TimeZone.UTC)

    Column {
        TimeDropbox(
            value = instant.toEpochMilliseconds(),
            onValueChange = {
                onDateChange(LocalDateTime(dateTime.date, it))
            },
        )
        DateDropbox(
            value = instant.toEpochMilliseconds(),
            onValueChange = {
                onDateChange(LocalDateTime(it, dateTime.time))
            },
        )
        RepeatDropbox(
            value = interval,
            onValueChange = onIntervalChange,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeDropbox(value: Long, onValueChange: (LocalTime) -> Unit = {}) {
    var expanded by remember {
        mutableStateOf(false)
    }

    val options =
        remember {
            listOf(
                Pair("Morning", LocalTime(7, 0)),
                Pair("Afternoon", LocalTime(13, 0)),
                Pair("Evening", LocalTime(19, 0)),
                Pair("Night", LocalTime(20, 0)),
                Pair("Pick time", LocalTime(0, 0)),
            )
        }
    val lastIndex = remember {
        options.lastIndex
    }
    val now = remember {
        Clock.System.now()
    }
    val nowtime = remember {
        now.toLocalDateTime(TimeZone.UTC).time
    }

    ExposedDropdownMenuBox(
        modifier = Modifier,
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        TextField(
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            value = value.toTimeString(),
            supportingText = { if (now.toEpochMilliseconds() > value) Text(text = "Time as past") },
            isError = now.toEpochMilliseconds() > value,
            onValueChange = {},
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            singleLine = true,

        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = {
            expanded = false
        }) {
            options.forEachIndexed { index, pair ->
                DropdownMenuItem(
                    text = { Text(text = pair.first) },
                    onClick = {
                        onValueChange(pair.second)
                        expanded = false
                    },
                    enabled = if (index != lastIndex) pair.second > nowtime else true,
                    trailingIcon = {
                        if (index != lastIndex) {
                            Text(
                                text = pair.second.toMillisecondOfDay().toLong().toTimeString(),
                            )
                        }
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateDropbox(value: Long, onValueChange: (LocalDate) -> Unit = {}) {
    var expanded by remember {
        mutableStateOf(false)
    }

    val date = remember(value) {
        Clock.System.todayIn(TimeZone.UTC)
    }
    val options = remember {
        listOf(
            Pair("Today", date),
            Pair("Tomorrow", date.plus(DateTimeUnit.DAY)),
            Pair("Pick date", LocalDate(1993, 1, 1)),
        )
    }
    ExposedDropdownMenuBox(
        modifier = Modifier,
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        TextField(
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            value = value.toDateString(),
            onValueChange = {},
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            singleLine = true,

        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = {
            expanded = false
        }) {
            options.forEach { string ->
                DropdownMenuItem(
                    text = { Text(text = string.first) },
                    onClick = {
                        onValueChange(string.second)
                        expanded = false
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepeatDropbox(value: Long?, onValueChange: (Long?) -> Unit = {}) {
    var expanded by remember {
        mutableStateOf(false)
    }

    val options = remember {
        listOf(
            Pair("Does not repeat", null),
            Pair("Daily", DateTimeUnit.HOUR.times(24).duration.toLong(DurationUnit.MILLISECONDS)),
            Pair(
                "Weekly",
                DateTimeUnit.HOUR.times(24 * 7).duration.toLong(DurationUnit.MILLISECONDS),
            ),
            Pair(
                "Monthly",
                DateTimeUnit.HOUR.times(24 * 7 * 30).duration.toLong(DurationUnit.MILLISECONDS),
            ),
            Pair(
                "Yearly",
                DateTimeUnit.HOUR.times(24 * 7 * 30).duration.toLong(DurationUnit.MILLISECONDS),
            ),
            //  Pair("Pick time", 0L)
        )
    }

    val index = remember(value) {
        val i = options.indexOfFirst { it.second == value }
        if (value == null || i == -1) {
            0
        } else {
            i
        }
    }

    ExposedDropdownMenuBox(
        modifier = Modifier,
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        TextField(
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            value = options[index].first,
            onValueChange = {},
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            singleLine = true,

        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = {
            expanded = false
        }) {
            options.forEach { string ->
                DropdownMenuItem(
                    text = { Text(text = string.first) },
                    onClick = {
                        onValueChange(string.second)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Preview
@Composable
fun TimeColumnPreview() {
    TimeContent(
        dateTime = LocalDateTime(2021, 4, 5, 0, 0, 0),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateDialog(
    currentDate : Long=System.currentTimeMillis()+(48*60*60*1000),
    showDialog: Boolean=true,
    onDismissRequest: () -> Unit={},
    onSetDate :(LocalDate)->Unit={}
) {
//    DatePickerDialog(
//        context,
//        { _, y, m, d ->
//            dateTime = LocalDateTime(
//                LocalDate(y, m + 1, d),
//                dateTime.time,
//            )
//        },
//        dateTime.year,
//        dateTime.monthNumber - 1,
//        dateTime.dayOfMonth,
//
//        ).show()
    val state= rememberDatePickerState(
        initialSelectedDateMillis = currentDate,
        initialDisplayedMonthMillis = currentDate

    )

    AnimatedVisibility(visible = showDialog) {
        DatePickerDialog(onDismissRequest =onDismissRequest,
            confirmButton = {
                Button(onClick = {
                    state.selectedDateMillis?.let {
                        val dateTime=Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.currentSystemDefault())
                        onSetDate(dateTime.date)
                    }
                }) {
                    Text(text = "Set date")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(text = "Cancel")
                }
            }
        ) {
            DatePicker(
                state = state,
                dateValidator = {it>=System.currentTimeMillis()}
            )
        }
    }

}

@Preview
@Composable
fun DateDialogPreview() {
    DateDialog()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeDialog(
    hour : Int=13,
    minute:Int=55,
    showDialog: Boolean=true,
    onDismissRequest: () -> Unit={},
    onSetTime :(LocalTime)->Unit={}
) {

//    TimePickerDialog(
//        context,
//        { _, hourOfDay, minute ->
//            dateTime = LocalDateTime(
//                dateTime.date,
//                LocalTime(hourOfDay, minute),
//            )
//        },
//        dateTime.hour,
//        dateTime.minute,
//        false,
//    ).show()

    val state = rememberTimePickerState(initialHour = hour, initialMinute = minute,is24Hour = false)
   AnimatedVisibility(visible =showDialog) {
       DatePickerDialog(
           onDismissRequest = onDismissRequest,
           confirmButton = {
               Button(onClick = {
                   Log.e("Date Picker","hour ${state.hour} minute ${state.minute}")
                   val time=LocalTime(state.hour,state.minute)
                   Log.e("Date Picker","hour2 ${time.hour} minute2 ${time.minute}")
                   onSetTime(time)

                   onDismissRequest()
               })
               {
                   Text(text = "Set time")
               }
           },
           dismissButton = {
               TextButton(onClick = onDismissRequest) {
                   Text(text = "Cancel")
               }
           }
           ) {

           TimePicker(state = state)
       }
   }


}

@Preview
@Composable
fun TimeDialogPreview() {

    TimeDialog()
}
