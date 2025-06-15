package com.mshdabiola.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mshdabiola.designsystem.R
import com.mshdabiola.designsystem.component.NoteTab
import com.mshdabiola.designsystem.component.NoteTabRow
import com.mshdabiola.model.IntervalEnd
import com.mshdabiola.model.NotificationDate
import com.mshdabiola.model.NotificationInterval
import com.mshdabiola.model.NotificationPlace
import com.mshdabiola.model.NotificationTime
import com.mshdabiola.model.NotificationUiState
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

@SuppressLint("NewApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationDialogNew(
    notificationUiState: NotificationUiState,
    isEdit: Boolean = false,
    showDialog: Boolean = false,
    onDismissRequest: () -> Unit = {},
    onSetAlarm: () -> Unit = { },
    onDeleteAlarm: () -> Unit = {},

) {
    var showDateDialog by remember {
        mutableStateOf(false)
    }
    var showTimeDialog by remember {
        mutableStateOf(false)
    }
    var isError by remember {
        mutableStateOf(false)
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text(text = if (isEdit) "Edit Reminder" else "Add Reminder") },
            text = {
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSetAlarm()
                        onDismissRequest()
                    },
                    enabled = !isError,
                ) {
                    Text(text = "Save")
                }
            },
            dismissButton = {
                Row {
                    if (isEdit) {
                        TextButton(
                            onClick = {
                                onDismissRequest()
                                onDeleteAlarm()
                            },
                        ) {
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
        if (showDateDialog) {
            val dateState = rememberDatePickerState()

            DatePickerDialog(
                onDismissRequest = {
                    showDateDialog = false
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showDateDialog = false
                        },
                    ) {
                        Text(text = "Set date")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDateDialog = false
                        },
                    ) {
                        Text(text = "Cancel")
                    }
                },
            ) {
                DatePicker(
                    state = dateState,
                    //   dateValidator = { it > (System.currentTimeMillis() - (48 * 60 * 60 * 1000)) }
                )
            }
        }
        if (showTimeDialog) {
            val timeState = rememberTimePickerState()
            DatePickerDialog(
                onDismissRequest = onDismissRequest,
                confirmButton = {
                    Button(
                        onClick = {
                            showTimeDialog = false
                        },
                    ) {
                        Text(text = "Set time")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showTimeDialog = false
                        },
                    ) {
                        Text(text = "Cancel")
                    }
                },
            ) {
                TimePicker(state = timeState)
            }
        }
    }
}

@SuppressLint("NewApi")
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun NotificationDialogNewPreview() {
    val notificationUiState = NotificationUiState(
        currentTime = LocalTime(10, 30),
        currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
        currentInterval = NotificationInterval.Daily(
            interval = 1,
            intervalEnd = IntervalEnd.Forever,
        ),
        currentPlace = NotificationPlace.Home,
        times = listOf(
            NotificationTime(LocalTime(9, 0), false, true),
            NotificationTime(LocalTime(12, 0), false, true),
            NotificationTime(LocalTime(15, 0), false, true),
            NotificationTime(LocalTime(18, 0), false, true),
            NotificationTime(LocalTime(0, 0), true, true),
        ),
        dates = listOf(
            NotificationDate(
                Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.plus(
                    DatePeriod(days = 0),
                ),
                false,
            ),
            NotificationDate(
                Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.plus(
                    DatePeriod(days = 1),
                ),
                false,
            ),
            NotificationDate(
                Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.plus(
                    DatePeriod(days = 7),
                ),
                false,
            ),
            NotificationDate(
                Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
                true,
            ),
        ),
        intervals = listOf(
            NotificationInterval.DoNotRepeat,
            NotificationInterval.Daily(1, IntervalEnd.Forever),
            NotificationInterval.Weekly(1, emptyList(), IntervalEnd.Forever),
            NotificationInterval.Monthly(1, true, IntervalEnd.Forever),
            NotificationInterval.Yearly(1, IntervalEnd.Forever),
            NotificationInterval.Custom,
        ),
        places = listOf(
            NotificationPlace.Home,
            NotificationPlace.Work,
            NotificationPlace.School,
        ),
    )
    NotificationDialogNew(notificationUiState = notificationUiState, showDialog = true)
}

@Composable
private fun NotificationContent(
    modifier: Modifier = Modifier,
    notificationUiState: NotificationUiState,
) {
    val pagerState = rememberPagerState { 2 }
    Column(modifier.fillMaxSize()) {
        NoteTabRow(pagerState.currentPage) {
            NoteTab(pagerState.currentPage == 0, onClick = {}) {
                Text(text = "Time")
            }
            NoteTab(pagerState.currentPage == 1, onClick = {}) {
                Text(text = "Place")
            }
        }
        HorizontalPager(modifier = Modifier.fillMaxSize().weight(1f), state = pagerState) {
            when (it) {
                0 -> {
                    Column(
                        Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),

                    ) {
                        TimeTextDropbox(
                            modifier = Modifier.fillMaxWidth(),
                            currentTime = notificationUiState.currentTime,
                            onValueChange = {
                            },
                            times = notificationUiState.times,
                        )
                        DateTextDropbox(
                            modifier = Modifier.fillMaxWidth(),
                            currentDate = notificationUiState.currentDate,
                            onValueChange = {
                            },
                            dates = notificationUiState.dates,
                        )
                        IntervalTextDropbox(
                            modifier = Modifier.fillMaxWidth(),
                            currentInterval = notificationUiState.currentInterval,
                            onValueChange = {
                            },
                            notificationIntervals = notificationUiState.intervals,
                        )
                    }
                }

                1 -> {
                    NotificationPlace(
                        places = notificationUiState.places,
                        onValueChange = {
                        },
                        currentPlace = notificationUiState.currentPlace,
                    )
                }
            }
        }
    }
}

@Preview(backgroundColor = 0xFF968F8F)
@Composable
fun NotificationContentPreview() {
    val notificationUiState = NotificationUiState(
        currentTime = LocalTime(10, 30),
        currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
        currentInterval = NotificationInterval.Daily(
            interval = 1,
            intervalEnd = IntervalEnd.Forever,
        ),
        currentPlace = NotificationPlace.Home,
        times = listOf(
            NotificationTime(LocalTime(9, 0), false, true),
            NotificationTime(LocalTime(12, 0), false, true),
            NotificationTime(LocalTime(15, 0), false, true),
            NotificationTime(LocalTime(18, 0), false, true),
            NotificationTime(LocalTime(0, 0), true, true),
        ),
        dates = listOf(
            NotificationDate(
                Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.plus(
                    DatePeriod(days = 0),
                ),
                false,
            ),
            NotificationDate(
                Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.plus(
                    DatePeriod(days = 1),
                ),
                false,
            ),
            NotificationDate(
                Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.plus(
                    DatePeriod(days = 7),
                ),
                false,
            ),
            NotificationDate(
                Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
                true,
            ),
        ),
        intervals = listOf(
            NotificationInterval.DoNotRepeat,
            NotificationInterval.Daily(1, IntervalEnd.Forever),
            NotificationInterval.Weekly(1, emptyList(), IntervalEnd.Forever),
            NotificationInterval.Monthly(1, true, IntervalEnd.Forever),
            NotificationInterval.Yearly(1, IntervalEnd.Forever),
            NotificationInterval.Custom,
        ),
        places = listOf(NotificationPlace.Home, NotificationPlace.Work, NotificationPlace.School),
    )
    NotificationContent(notificationUiState = notificationUiState)
}

@Composable
fun NotificationPlace(
    modifier: Modifier = Modifier,
    places: List<NotificationPlace> = emptyList(),
    onValueChange: (NotificationPlace) -> Unit = {},
    currentPlace: NotificationPlace? = null,
) {
    val placeStringArray = stringArrayResource(R.array.modules_designsystem_notification_places)
    Column(modifier = modifier) {
        places.forEachIndexed { index, place ->
            if (place !is NotificationPlace.Edit) {
                Row {
                    RadioButton(selected = place == currentPlace, onClick = {})
                    Text(text = placeStringArray[index])
                }
            } else {
                Row {
                    RadioButton(selected = place == currentPlace, onClick = { })
                    TextField(
                        state = rememberTextFieldState(),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeTextDropbox(
    modifier: Modifier = Modifier,
    currentTime: LocalTime,
    onValueChange: (NotificationTime) -> Unit = {},
    times: List<NotificationTime> = emptyList(),
) {
    var expanded by remember {
        mutableStateOf(false)
    }
    var showError by remember {
        mutableStateOf(false)
    }
    val state = rememberTextFieldState()
    LaunchedEffect(key1 = currentTime) {
        state.clearText()
        state.edit {
            append("${currentTime.hour}:${currentTime.minute} ")
            append(if (currentTime.hour < 12) "AM" else "PM")
        }
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        showError = currentTime <= now.time
    }

    val timeStringArray = stringArrayResource(R.array.modules_designsystem_notification_times)

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.SecondaryEditable, true),
            readOnly = true,
            state = state,
            supportingText = { if (showError) Text(text = "Time as past") },
            isError = showError,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            lineLimits = TextFieldLineLimits.SingleLine,

        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
        ) {
            times.forEachIndexed { index, notificationTime ->
                DropdownMenuItem(
                    text = { Text(text = timeStringArray[index]) },
                    onClick = {
                        onValueChange(notificationTime)
                        expanded = false
                    },
                    enabled = notificationTime.isEnable,
                    trailingIcon = {
                        Text(
                            text = if (notificationTime.isPickTime) {
                                ""
                            } else {
                                "${notificationTime.time.hour}:${notificationTime.time.minute} " +
                                    if (notificationTime.time.hour < 12) "AM" else "PM"
                            },

                        )
                    },
                )
            }
        }
    }
}

@Preview
@Composable
fun TimeTextDropboxPreview() {
    val currentTime = LocalTime(10, 30)
    val times = listOf(
        NotificationTime(LocalTime(9, 0), false, true),
        NotificationTime(LocalTime(12, 0), false, true),
        NotificationTime(LocalTime(15, 0), false, true),
        NotificationTime(LocalTime(18, 0), false, true),
        NotificationTime(LocalTime(0, 0), true, true),
    )
    TimeTextDropbox(currentTime = currentTime, times = times)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTextDropbox(
    modifier: Modifier = Modifier,
    currentDate: LocalDate,
    onValueChange: (NotificationDate) -> Unit = {},
    dates: List<NotificationDate> = emptyList(),
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    val state = rememberTextFieldState()
    LaunchedEffect(key1 = currentDate) {
        state.clearText()
        state.edit {
            append("${currentDate.month.name} ${currentDate.dayOfMonth}")
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            if (currentDate.year != now.year) {
                append(", ${currentDate.year}")
            }
        }
    }

    val dateStringArray = stringArrayResource(R.array.modules_designsystem_notification_days)

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.SecondaryEditable, true),
            readOnly = true,
            state = state,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            lineLimits = TextFieldLineLimits.SingleLine,

        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
        ) {
            dates.forEachIndexed { index, notificationTime ->
                DropdownMenuItem(
                    text = { Text(text = dateStringArray[index]) },
                    onClick = {
                        onValueChange(notificationTime)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Preview
@Composable
fun DateTextDropboxPreview() {
    val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val dates = listOf(
        NotificationDate(currentTime.plus(DatePeriod(days = 0)), false),
        NotificationDate(currentTime.plus(DatePeriod(days = 1)), false),
        NotificationDate(currentTime.plus(DatePeriod(days = 7)), false),
        NotificationDate(currentTime, true),
    )
    DateTextDropbox(
        currentDate = currentTime,
        dates = dates,
    )
}

@Preview
@Composable
fun IntervalTextDropboxPreview() {
    val currentInterval = NotificationInterval.Daily(
        interval = 1,
        intervalEnd = IntervalEnd.Forever,
    )
    val notificationIntervals = listOf(
        NotificationInterval.DoNotRepeat,
        NotificationInterval.Daily(1, IntervalEnd.Forever),
        NotificationInterval.Weekly(1, emptyList(), IntervalEnd.Forever),
        NotificationInterval.Monthly(1, true, IntervalEnd.Forever),
        NotificationInterval.Yearly(1, IntervalEnd.Forever),
        NotificationInterval.Custom,
    )
    IntervalTextDropbox(
        currentInterval = currentInterval,
        notificationIntervals = notificationIntervals,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntervalTextDropbox(
    modifier: Modifier = Modifier,
    currentInterval: NotificationInterval,
    onValueChange: (NotificationInterval) -> Unit = {},
    notificationIntervals: List<NotificationInterval> = emptyList(),
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    val state = rememberTextFieldState()
    LaunchedEffect(key1 = currentInterval) {
        state.clearText()
        state.edit {
        }
    }

    val intervalStringArray = stringArrayResource(
        R.array.modules_designsystem_notification_interval,
    )

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.SecondaryEditable, true),
            readOnly = true,
            state = state,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            lineLimits = TextFieldLineLimits.SingleLine,

        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
        ) {
            notificationIntervals.forEachIndexed { index, notificationTime ->
                DropdownMenuItem(
                    text = { Text(text = intervalStringArray[index]) },
                    onClick = {
                        onValueChange(notificationTime)
                        expanded = false
                    },
                )
            }
        }
    }
}
