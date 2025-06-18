package com.mshdabiola.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mshdabiola.designsystem.R
import com.mshdabiola.designsystem.component.NoteTab
import com.mshdabiola.designsystem.component.NoteTabRow
import com.mshdabiola.ui.state.IntervalEnd
import com.mshdabiola.ui.state.NotificationDate
import com.mshdabiola.ui.state.NotificationInterval
import com.mshdabiola.ui.state.NotificationPlace
import com.mshdabiola.ui.state.NotificationTime
import com.mshdabiola.ui.state.NotificationUiState
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

@SuppressLint("NewApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationDialogNew(
    initState: NotificationUiState? = null,
    isEdit: Boolean = false,
    showDialog: Boolean = false,
    onDismissRequest: () -> Unit = {},
    onSetAlarm: (NotificationUiState) -> Unit = { },
    onDeleteAlarm: () -> Unit = {},

) {
    val today = remember {
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    }
    val pagerState = rememberPagerState { 2 }
    val coroutineScope = rememberCoroutineScope()
    var notificationUiState by remember(initState) {
        val value = initState ?: NotificationUiState(
            currentPlace = null,
            currentInterval = NotificationInterval.DoNotRepeat,
            currentDateTime = Clock
                .System
                .now().plus(1, DateTimeUnit.HOUR)
                .toLocalDateTime(TimeZone.currentSystemDefault()),
        )
        mutableStateOf(value)
    }

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
                        NoteTab(
                            pagerState.currentPage == 0,
                            onClick = {
                                coroutineScope.launch { pagerState.animateScrollToPage(0) }
                            },
                        ) {
                            Text(text = "Time")
                        }
                        NoteTab(
                            pagerState.currentPage == 1,
                            onClick = {
                                coroutineScope.launch { pagerState.animateScrollToPage(1) }
                            },
                        ) {
                            Text(text = "Place")
                        }
                    }
                    HorizontalPager(
                        modifier = Modifier,
                        state = pagerState,
                        userScrollEnabled = false,
                    ) { index ->
                        when (index) {
                            0 -> {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    TimeTextDropbox(
                                        modifier = Modifier.fillMaxWidth(),
                                        currentTime = notificationUiState.currentDateTime.time,
                                        onValueChange = {
                                            notificationUiState = notificationUiState.copy(
                                                currentDateTime = LocalDateTime(
                                                    date = notificationUiState.currentDateTime.date,
                                                    time = it,
                                                ),
                                            )
                                        },
                                        onErrorMessage = {
                                            isError = it
                                        },
                                    )
                                    DateTextDropbox(
                                        modifier = Modifier.fillMaxWidth(),
                                        currentDate = notificationUiState.currentDateTime.date,
                                        todayDate = today.date,
                                        onValueChange = {
                                            notificationUiState = notificationUiState.copy(
                                                currentDateTime = LocalDateTime(
                                                    date = it,
                                                    time = notificationUiState.currentDateTime.time,
                                                ),
                                            )
                                        },
                                    )
                                    IntervalTextDropbox(
                                        modifier = Modifier.fillMaxWidth(),
                                        currentInterval = notificationUiState.currentInterval,
                                        onValueChange = {
                                            notificationUiState = notificationUiState.copy(
                                                currentInterval = it,
                                            )
                                        },
                                    )
                                }
                            }

                            1 -> {
                                NotificationPlace(
                                    onValueChange = {
                                        notificationUiState = notificationUiState.copy(
                                            currentPlace = it,
                                        )
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
                        onSetAlarm(notificationUiState)
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
            intervalEnd = IntervalEnd.Forever,
        ),
        currentPlace = NotificationPlace.Home,

    )
    NotificationDialogNew(initState = notificationUiState, showDialog = true)
}

@Composable
fun NotificationPlace(
    modifier: Modifier = Modifier,
    onValueChange: (NotificationPlace) -> Unit = {},
    currentPlace: NotificationPlace? = null,
) {
    val places = remember {
        listOf(
            NotificationPlace.Home,
            NotificationPlace.Work,
            NotificationPlace.School,
            NotificationPlace.Edit(TextFieldState()), // Default TextFieldState
        )
    }
    val placeStringArray = stringArrayResource(R.array.modules_designsystem_notification_places)
    Column(modifier = modifier) {
        places.forEachIndexed { index, place ->
            if (place is NotificationPlace.Edit) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = place == currentPlace,
                        onClick = {
                            onValueChange(place) // Still allow click to select
                        },
                    )
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                if (focusState.isFocused && currentPlace != place) {
                                    onValueChange(place)
                                }
                            },
                        state = place.place, // Use the TextFieldState from the place object
                        lineLimits = TextFieldLineLimits.SingleLine,
                        placeholder = { Text(text = placeStringArray[index]) },
                    )
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onValueChange(place) }, // Make the whole row clickable
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = place == currentPlace,
                        onClick = {
                            onValueChange(place)
                        },
                    )
                    Text(modifier = Modifier.weight(1f), text = placeStringArray[index])
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
    val formatter = LocalTime.Format {
        amPmHour(Padding.ZERO) // hh (01-12) with zero padding
        char(':')
        minute(Padding.ZERO) // mm (00-59) with zero padding
        char(' ')
        amPmMarker("AM", "PM") // AM/PM marker in uppercase)
    }

    LaunchedEffect(key1 = currentTime) {
        state.clearText()
        state.edit {
            append(currentTime.format(formatter))
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
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.SecondaryEditable, true),
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
                                    notificationTime.localTime.format(formatter),
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
    todayDate: LocalDate,
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
            append(
                "${
                    currentDate.month.name.lowercase().replaceFirstChar { it.uppercase() }
                } ${currentDate.dayOfMonth}",
            )
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            if (currentDate.year != now.year) {
                append(", ${currentDate.year}")
            }
        }
    }

    val dateStringArray = stringArrayResource(R.array.modules_designsystem_notification_days)
    val daysOfWeeks = stringArrayResource(R.array.modules_designsystem_days_of_weeks)

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.SecondaryEditable, true),
            readOnly = true,
            state = state,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
            ),
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
                    text = { Text(text = dateStringArray[index] + " " + if (index == 2)daysOfWeeks[todayDate.dayOfWeek.ordinal] else "") },
                    onClick = {
                        if (notificationTime is NotificationDate.Date) {
                            onValueChange(notificationTime.localDate)
                        } else {
                            showDateDialog = true
                        }
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

                        val date = dateState.selectedDateMillis?.let { millis ->
                            Instant.fromEpochMilliseconds(millis)
                                .toLocalDateTime(TimeZone.currentSystemDefault())
                                .date
                        } ?: nowDate
                        onValueChange(
                            date,
                        )
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
        todayDate = currentTime,
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

    var showIntervalDialog by remember {
        mutableStateOf(false)
    }

    val notificationIntervals = remember(nowDate) {
        listOf(
            NotificationInterval.DoNotRepeat,
            NotificationInterval.Daily(
                intervalEnd = IntervalEnd.Forever,
            ),
            NotificationInterval.Weekly(
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
    val intervalStringArray = stringArrayResource(
        R.array.modules_designsystem_notification_interval,
    )

    val state = rememberTextFieldState()
    LaunchedEffect(key1 = currentInterval) {
        state.clearText()
        state.edit {
            append(intervalStringArray[currentInterval.index])
        }
    }

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.SecondaryEditable, true),
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
                        if (notificationTime is NotificationInterval.Custom) {
                            showIntervalDialog = true
                        } else {
                            onValueChange(notificationTime)
                        }
                        expanded = false
                    },
                )
            }
        }
    }

    if (showIntervalDialog) {
        NotificationDialogInterval(
            initInterval = currentInterval,
            intervals = notificationIntervals.toMutableList().apply {
                removeAt(5)
            },
            onValueChange = {
                onValueChange(it)
                showIntervalDialog = false
            },
            onDismiss = { showIntervalDialog = false },
            todayDate = nowDate,
        )
    }
}

@Preview
@Composable
fun IntervalTextDropboxPreview() {
    val currentInterval = NotificationInterval.Daily(
        intervalEnd = IntervalEnd.Forever,
    )

    IntervalTextDropbox(
        currentInterval = currentInterval,
    )
}
