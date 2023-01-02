package com.mshdabiola.editscreen.component

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NotificationDialog(
    showDialog: Boolean = true,
    onDismissRequest: () -> Unit = {},
    remainder: Long = -1,
    interval: Long? = null,
    onSetAlarm: (Long, Long?) -> Unit = { _, _ -> },
    onDeleteAlarm: () -> Unit = {}
) {

    val state = rememberPagerState(0)
    val coroutineScope = rememberCoroutineScope()
    var dateTime by remember(remainder) {
        val time = if (remainder > -1)
            Instant.fromEpochMilliseconds(remainder)
                .toLocalDateTime(TimeZone.currentSystemDefault())
        else
            Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

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

                Column {
                    TabRow(selectedTabIndex = state.currentPage) {
                        Tab(
                            selected = state.currentPage == 0,
                            onClick = {

                                coroutineScope.launch {
                                    state.animateScrollToPage(0)
                                }
                            },
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text(text = "Time")
                        }
                        Tab(
                            selected = state.currentPage == 1, onClick = {

                                coroutineScope.launch {
                                    //   state.animateScrollToPage(1)
                                }
                            },
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text(text = "Place")
                        }
                    }
                    HorizontalPager(
                        pageCount = 1,
                        state = state
                    ) { index ->

                        if (index == 0) {
                            TimeContent(inter, dateTime,
                                onDateChange = {
                                    if (it.time == LocalTime(0, 0)) {
                                        TimePickerDialog(
                                            context,
                                            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                                                dateTime = LocalDateTime(
                                                    dateTime.date,
                                                    LocalTime(hourOfDay, minute)
                                                )
                                            },
                                            dateTime.hour,
                                            dateTime.minute,
                                            false
                                        ).show()
                                    } else
                                        if (it.date == LocalDate(1993, 1, 1)) {
                                            DatePickerDialog(
                                                context,
                                                { _, y, m, d ->
                                                    dateTime = LocalDateTime(
                                                        LocalDate(y, m + 1, d),
                                                        dateTime.time
                                                    )
                                                },
                                                dateTime.year,
                                                dateTime.monthNumber - 1,
                                                dateTime.dayOfMonth

                                            ).show()

                                        } else {
                                            dateTime = it
                                        }

                                },
                                onIntervalChange = {
                                    inter = it
                                }
                            )
                        } else {
                            Column {
                                Text(text = "Location")
                            }

                        }


                    }
                }


            },
            confirmButton = {
                Button(onClick = {
                    onDismissRequest()
                    onSetAlarm(
                        dateTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
                        inter
                    )
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

            }
        )
    }

}

@Preview
@Composable
fun NotificationDialogPreview() {
    NotificationDialog()
}


@Composable
fun TimeContent(
    interval: Long? = null,
    dateTime: LocalDateTime,
    onDateChange: (LocalDateTime) -> Unit = {},
    onIntervalChange: (Long?) -> Unit = {}
) {

    val instant = dateTime.toInstant(TimeZone.UTC)

    Column {
        TimeDropbox(
            value = instant.toEpochMilliseconds(),
            onValueChange = {
                onDateChange(LocalDateTime(dateTime.date, it))
            }
        )
        DateDropbox(
            value = instant.toEpochMilliseconds(),
            onValueChange = {
                onDateChange(LocalDateTime(it, dateTime.time))
            }
        )
        RepeatDropbox(
            value = interval,
            onValueChange = onIntervalChange
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeDropbox(value: Long, onValueChange: (LocalTime) -> Unit = {}) {

    var expanded by remember {
        mutableStateOf(false)
    }

    val options = listOf(
        Pair("Morning", LocalTime(7, 0)),
        Pair("Afternoon", LocalTime(13, 0)),
        Pair("Evening", LocalTime(19, 0)),
        Pair("Night", LocalTime(20, 0)),
        Pair("Pick time", LocalTime(0, 0))
    )

    ExposedDropdownMenuBox(
        modifier = Modifier,
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            value = value.toTimeString(),
            onValueChange = {},
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            singleLine = true

        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = {
            expanded = false
        }) {
            options.forEach { pair ->
                DropdownMenuItem(
                    text = { Text(text = pair.first) },
                    onClick = {

                        onValueChange(pair.second)
                        expanded = false
                    },
                    trailingIcon = {
                        Text(
                            text = pair.second.toMillisecondOfDay().toLong().toTimeString()
                        )
                    }
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
            Pair("Pick date", LocalDate(1993, 1, 1))
        )
    }
    ExposedDropdownMenuBox(
        modifier = Modifier,
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            value = value.toDateString(),
            onValueChange = {},
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            singleLine = true

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
                    }
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
            Pair("Daily", DateTimeUnit.HOUR.times(24).nanoseconds.times(1000)),
            Pair("Weekly", DateTimeUnit.HOUR.times(24 * 7).nanoseconds.times(1000)),
            Pair("Monthly", DateTimeUnit.HOUR.times(24 * 7 * 30).nanoseconds.times(1000)),
            Pair("Yearly", DateTimeUnit.HOUR.times(24 * 7 * 30).nanoseconds.times(1000)),
            //  Pair("Pick time", 0L)
        )
    }


    val index = remember(value) {

        val i = options.indexOfFirst { it.second == value }
        if (value == null || i == -1)
            0
        else {
            i
        }

    }

    ExposedDropdownMenuBox(
        modifier = Modifier,
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            value = options[index].first,
            onValueChange = {},
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            singleLine = true

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
                    }
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


fun Long.toTimeString(isCurr: Boolean = false): String {
    val instant =
        Instant.fromEpochMilliseconds(this)

    val dateTime = instant.toLocalDateTime(
        if (isCurr) TimeZone.currentSystemDefault() else TimeZone.UTC
    )
    val hour = dateTime.hour % 12L
    val a = if (dateTime.hour > 11) "PM" else "AM"
    return "%02d : %02d %s".format(hour, dateTime.minute, a)
}




fun Long.toDateString(): String {
    val instant =
        Instant.fromEpochMilliseconds(this)
    val dateTime = instant.toLocalDateTime(TimeZone.UTC)



    return "${
        dateTime.month.name.lowercase().replaceFirstChar { it.uppercase() }
    } ${dateTime.dayOfMonth}, ${dateTime.year}"
}