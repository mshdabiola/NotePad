package com.mshdabiola.ui

import android.annotation.SuppressLint
import android.os.Build
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
import androidx.compose.foundation.text.input.TextFieldState
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
import androidx.compose.material3.getSelectedDate
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
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toKotlinLocalDate
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
    val pagerState = rememberPagerState { 2 }

    var isError by remember {
        mutableStateOf(false)
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text(text = if (isEdit) "Edit Reminder" else "Add Reminder") },
            text = {
                Column {
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
                                        currentTime = notificationUiState.currentDateTime.time,
                                        onValueChange = {
                                        },
                                        onErrorMessage = {
                                            isError = it
                                        },
                                    )
                                    DateTextDropbox(
                                        modifier = Modifier.fillMaxWidth(),
                                        currentDate = notificationUiState.currentDateTime.date,
                                        onValueChange = {
                                        },
                                    )
                                    IntervalTextDropbox(
                                        modifier = Modifier.fillMaxWidth(),
                                        currentInterval = notificationUiState.currentInterval,
                                        onValueChange = {
                                        },
                                    )
                                }
                            }

                            1 -> {
                                NotificationPlace(
                                    onValueChange = {
                                    },
                                    currentPlace = notificationUiState.currentPlace,
                                )
                            }
                        }
                    }
                }
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
    }
}

@SuppressLint("NewApi")
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun NotificationDialogNewPreview() {
    val notificationUiState = NotificationUiState(
        currentDateTime = LocalDateTime(2026, 6, 16, 22, 1),
        currentInterval = NotificationInterval.Daily(
            interval = 1,
            intervalEnd = IntervalEnd.Forever,
        ),
        currentPlace = NotificationPlace.Home,

    )
    NotificationDialogNew(notificationUiState = notificationUiState, showDialog = true)
}

@Composable
fun NotificationPlace(
    modifier: Modifier = Modifier,
    onValueChange: (NotificationPlace) -> Unit = {},
    currentPlace: NotificationPlace? = null,
    editState: TextFieldState = rememberTextFieldState(),
) {
    val places = remember {
        listOf(
            NotificationPlace.Home,
            NotificationPlace.Work,
            NotificationPlace.School,
            NotificationPlace.Edit(""),
        )
    }
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
                        state = editState,
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
    onValueChange: (LocalTime) -> Unit = {},
    onErrorMessage: (Boolean) -> Unit = {},
) {
    val nowTime = remember {
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time
    }
    val times = remember {
        listOf(
            NotificationTime.Time(LocalTime(7, 0, 0)),
            NotificationTime.Time(LocalTime(13, 0, 0)),
            NotificationTime.Time(LocalTime(19, 0, 0)),
            NotificationTime.Time(LocalTime(20, 0, 0)),
            NotificationTime.PickTime,
        )
    }
    var expanded by remember {
        mutableStateOf(false)
    }
    var showError by remember {
        mutableStateOf(false)
    }
    var showTimeDialog by remember {
        mutableStateOf(false)
    }
    val state = rememberTextFieldState()
    LaunchedEffect(key1 = currentTime) {
        state.clearText()
        state.edit {
            append("${currentTime.hour}:${currentTime.minute} ")
            append(if (currentTime.hour < 12) "AM" else "PM")
        }
        showError = currentTime <= nowTime
        onErrorMessage(showError)
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

                when (notificationTime) {
                    is NotificationTime.Time -> {
                        DropdownMenuItem(
                            text = { Text(text = timeStringArray[index]) },
                            onClick = {
                                onValueChange(notificationTime.localTime)
                                expanded = false
                            },
                            enabled = notificationTime.localTime > nowTime,
                            trailingIcon = {
                                Text(
                                    "${notificationTime.localTime.hour}:${notificationTime.localTime.minute} " +
                                        if (notificationTime.localTime.hour < 12) "AM" else "PM",
                                )
                            },
                        )
                    }
                    is NotificationTime.PickTime -> {
                        DropdownMenuItem(
                            text = { Text(text = timeStringArray[index]) },
                            onClick = {
                                showTimeDialog = true
                                expanded = false
                            },
                        )
                    }
                }
            }
        }
    }

    if (showTimeDialog) {
        val timeState = rememberTimePickerState()
        DatePickerDialog(
            onDismissRequest = { showTimeDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        showTimeDialog = false
                        onValueChange(LocalTime(timeState.hour, timeState.minute))
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

@Preview
@Composable
fun TimeTextDropboxPreview() {
    val currentTime = LocalTime(10, 30)

    TimeTextDropbox(currentTime = currentTime)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTextDropbox(
    modifier: Modifier = Modifier,
    currentDate: LocalDate,
    onValueChange: (LocalDate) -> Unit = {},
) {
    var expanded by remember {
        mutableStateOf(false)
    }
    var showDateDialog by remember {
        mutableStateOf(false)
    }
    val nowDate = remember {
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    }

    val dates = remember(nowDate) {
        listOf(
            NotificationDate.Date(nowDate),
            NotificationDate.Date(nowDate.plus(1, DateTimeUnit.DAY)),
            NotificationDate.Date(nowDate.plus(1, DateTimeUnit.WEEK)),
            NotificationDate.PickDate,
        )
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
                        showDateDialog = true
                        expanded = false
                    },
                )
            }
        }
    }

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
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            onValueChange(
                                dateState.getSelectedDate()?.toKotlinLocalDate() ?: nowDate,
                            )
                        } else {
                            onValueChange(nowDate)
                        }
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
}

@Preview
@Composable
fun DateTextDropboxPreview() {
    val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    DateTextDropbox(
        currentDate = currentTime,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntervalTextDropbox(
    modifier: Modifier = Modifier,
    currentInterval: NotificationInterval,
    onValueChange: (NotificationInterval) -> Unit = {},
) {
    var expanded by remember {
        mutableStateOf(false)
    }
    val nowDate = remember {
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    }

    val notificationIntervals = remember(nowDate) {
        listOf(
            NotificationInterval.DoNotRepeat,
            NotificationInterval.Daily(
                intervalEnd = IntervalEnd.Forever,
            ),
            NotificationInterval.Weekly(
                days = listOf(nowDate.dayOfWeek),
                intervalEnd = IntervalEnd.Forever,
            ),
            NotificationInterval.Monthly(
                sameDay = true,
                intervalEnd = IntervalEnd.Forever,
            ),
            NotificationInterval.Yearly(
                intervalEnd = IntervalEnd.Forever,
            ),
            NotificationInterval.Custom,
        )
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

@Preview
@Composable
fun IntervalTextDropboxPreview() {
    val currentInterval = NotificationInterval.Daily(
        interval = 1,
        intervalEnd = IntervalEnd.Forever,
    )

    IntervalTextDropbox(
        currentInterval = currentInterval,
    )
}
