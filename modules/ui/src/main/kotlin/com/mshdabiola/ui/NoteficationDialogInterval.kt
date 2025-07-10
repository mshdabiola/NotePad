package com.mshdabiola.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.RadioButton
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mshdabiola.designsystem.R
import com.mshdabiola.model.IntervalEnd
import com.mshdabiola.model.NotificationInterval
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationDialogInterval(
    modifier: Modifier = Modifier,
    initInterval: NotificationInterval,
    todayDate: LocalDate,
    intervals: List<NotificationInterval>,
    onValueChange: (NotificationInterval) -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    var currentInterval by remember(initInterval) {
        mutableStateOf(initInterval)
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

    BasicAlertDialog(
        modifier = modifier,
        onDismissRequest = { },
    ) {
        Surface(
            shape = ShapeDefaults.Small,
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ExposedDropdownMenuBox(
                    modifier = Modifier,
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
                        intervals.forEachIndexed { index, interval ->
                            DropdownMenuItem(
                                text = { Text(text = intervalStringArray[index]) },
                                onClick = {
                                    currentInterval = interval
                                    expanded = false
                                },
                            )
                        }
                    }
                }
                when (currentInterval) {
                    is NotificationInterval.Daily -> {
                        val daily = currentInterval as NotificationInterval.Daily
                        IntervalTextField(
                            prefix = "Every",
                            suffix = "day",
                            suffixPlural = "days",
                            text = daily.interval,
                            onValueChange = {
                                currentInterval = daily.copy(interval = it)
                            },

                        )

                        IntervalRepeatEnd(
                            currentIntervalEnd = daily.intervalEnd,
                            todayDate = todayDate,
                            onValueChange = {
                                currentInterval = daily.copy(intervalEnd = it)
                            },
                        )
                    }

                    is NotificationInterval.Weekly -> {
                        val daysOfWeek =
                            stringArrayResource(R.array.modules_designsystem_days_of_weeks)
                        val weekly = currentInterval as NotificationInterval.Weekly
                        IntervalTextField(
                            prefix = "Every",
                            suffix = "week",
                            suffixPlural = "weeks",
                            text = weekly.interval,
                            onValueChange = {
                                currentInterval = weekly.copy(interval = it)
                            },
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            daysOfWeek
                                .forEachIndexed { index, days ->
                                    val isSelected = index in weekly.days
                                    InputChip(
                                        selected = isSelected,
                                        onClick = {
                                            val newDays = weekly.days.toMutableSet()
                                            if (isSelected) {
                                                newDays.remove(index)
                                            } else {
                                                newDays.add(index)
                                            }
                                            currentInterval =
                                                weekly.copy(
                                                    days = newDays,

                                                )
                                        },
                                        label = { Text(days) },
                                    )
                                }
                        }

                        IntervalRepeatEnd(
                            currentIntervalEnd = weekly.intervalEnd,
                            todayDate = todayDate,
                            onValueChange = {
                                currentInterval = weekly.copy(intervalEnd = it)
                            },
                        )
                    }

                    is NotificationInterval.Monthly -> {
                        val monthly = currentInterval as NotificationInterval.Monthly
                        val daysOfWeek = stringArrayResource(R.array.modules_designsystem_days_of_weeks)
                        IntervalTextField(
                            prefix = "Every",
                            suffix = "month",
                            suffixPlural = "months",
                            text = monthly.interval,
                            onValueChange = {
                                currentInterval = monthly.copy(interval = it)
                            },
                        )
                        Row(
                            modifier = Modifier.clickable {
                                currentInterval = monthly.copy(sameDay = true)
                            },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(monthly.sameDay, onClick = {
                                currentInterval = monthly.copy(sameDay = true)
                            })
                            Text(
                                modifier = Modifier.weight(1f),
                                text = "On same day each month",
                            )
                        }
                        Row(
                            modifier = Modifier.clickable {
                                currentInterval = monthly.copy(sameDay = false)
                            },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(!monthly.sameDay, onClick = {
                                currentInterval = monthly.copy(sameDay = false)
                            })
                            Text(modifier = Modifier.weight(1f), text = "On Third of ${daysOfWeek[todayDate.dayOfWeek.ordinal]}")
                        }

                        IntervalRepeatEnd(
                            currentIntervalEnd = monthly.intervalEnd,
                            todayDate = todayDate,
                            onValueChange = {
                                currentInterval = monthly.copy(intervalEnd = it)
                            },
                        )
                    }

                    is NotificationInterval.Yearly -> {
                        val yearly = currentInterval as NotificationInterval.Yearly
                        IntervalTextField(
                            prefix = "Every",
                            suffix = "year",
                            suffixPlural = "years",
                            text = yearly.interval,
                            onValueChange = {
                                currentInterval = yearly.copy(interval = it)
                            },
                        )

                        IntervalRepeatEnd(
                            currentIntervalEnd = yearly.intervalEnd,
                            todayDate = todayDate,
                            onValueChange = {
                                currentInterval = yearly.copy(intervalEnd = it)
                            },
                        )
                    }

                    is NotificationInterval.DoNotRepeat -> {
                        Spacer(modifier = Modifier.height(64.dp))
                    }

                    is NotificationInterval.Custom -> {
                    }
                }

                HorizontalDivider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Close")
                    }
                    Button(
                        onClick = {
                            onValueChange(currentInterval)
                        },
                    ) {
                        Text("Set repeat")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Preview
@Composable
fun NotificationDialogIntervalPreview() {
    val nowDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val currentInterval = NotificationInterval.Monthly(
        intervalEnd = IntervalEnd.EndDate(LocalDate(2023, 1, 1)),
        sameDay = true,
        //  days = setOf(0, 4, 6),
    )
    val intervals = listOf(
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
    NotificationDialogInterval(
        initInterval = currentInterval,
        intervals = intervals,
        onValueChange = {},
        todayDate = nowDate,
    )
}

@Composable
fun IntervalTextField(
    modifier: Modifier = Modifier,
    prefix: String = "",
    suffix: String = "",
    suffixPlural: String = "",
    text: String = "",
    onValueChange: (String) -> Unit = {},
) {
    val state: TextFieldState = rememberTextFieldState(text)
    LaunchedEffect(key1 = state.text) {
        onValueChange(state.text.toString())
    }

    TextField(
        modifier = modifier.width(172.dp),
        state = state,
        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
        lineLimits = TextFieldLineLimits.SingleLine,
        inputTransformation = DigitsOnlyInputTransformation(2),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done,
            showKeyboardOnFocus = true,
        ),
        prefix = { Text(text = prefix) },
        suffix = {
            Text(
                text = when {
                    state.text.isBlank() -> suffix
                    state.text.toString().toInt() > 1 -> suffixPlural
                    else -> suffix },
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,

        ),
    )
}

@Preview
@Composable
fun IntervalTextFieldPreview() {
    IntervalTextField(
        prefix = "Every",
        suffix = "days",
        text = "44",
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun IntervalRepeatEnd(
    modifier: Modifier = Modifier,
    currentIntervalEnd: IntervalEnd,
    todayDate: LocalDate,
    onValueChange: (IntervalEnd) -> Unit = {},
) {
    val intervalEndStringArray = stringArrayResource(
        R.array.modules_designsystem_notification_interval_end2,
    )

    val intervalsEnds = remember {
        listOf(
            IntervalEnd.Forever,
            IntervalEnd.EndDate(todayDate),
            IntervalEnd.NumberOfTimes(1),
        )
    }
    var expanded by remember {
        mutableStateOf(false)
    }
    val state = rememberTextFieldState()
    LaunchedEffect(key1 = currentIntervalEnd) {
        state.clearText()
        state.edit {
            append(intervalEndStringArray[currentIntervalEnd.index])
        }
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ExposedDropdownMenuBox(
            modifier = Modifier.weight(3f),
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {
            TextField(
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.SecondaryEditable, true),
                readOnly = true,
                state = state,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,

                ),
                lineLimits = TextFieldLineLimits.SingleLine,

            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                },
            ) {
                intervalsEnds.forEach { intervalsEnds ->
                    DropdownMenuItem(
                        text = { Text(text = intervalEndStringArray[intervalsEnds.index]) },
                        onClick = {
                            onValueChange(intervalsEnds)

                            expanded = false
                        },
                    )
                }
            }
        }

        when (currentIntervalEnd) {
            IntervalEnd.Forever -> {}
            is IntervalEnd.EndDate -> {
                var showDateDialog by remember {
                    mutableStateOf(false)
                }
                val dateTextFiledState = rememberTextFieldState()
                LaunchedEffect(key1 = currentIntervalEnd.date) {
                    dateTextFiledState.clearText()
                    dateTextFiledState.edit {
                        append(currentIntervalEnd.date.toString())
                    }
                }
                TextField(
                    modifier = Modifier.weight(3f),
                    state = dateTextFiledState,
                    readOnly = true,
                    lineLimits = TextFieldLineLimits.SingleLine,

                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,

                    ),
                    trailingIcon = {
                        IconButton(onClick = { showDateDialog = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Date")
                        }
                    },
                )
                if (showDateDialog) {
                    val dateState =
                        rememberDatePickerState(initialSelectedDate = currentIntervalEnd.date.toJavaLocalDate())

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
                                    } ?: LocalDate(1970, 1, 1)
                                    onValueChange(
                                        currentIntervalEnd.copy(date = date),

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

            is IntervalEnd.NumberOfTimes -> {
                val numberOfTimesState = rememberTextFieldState(currentIntervalEnd.times.toString())
                LaunchedEffect(key1 = numberOfTimesState.text) {
                    onValueChange(
                        currentIntervalEnd.copy(times = numberOfTimesState.text.toString().toInt()),
                    )
                }
                TextField(
                    modifier = Modifier.weight(2f),
                    state = numberOfTimesState,
                    lineLimits = TextFieldLineLimits.SingleLine,
                    inputTransformation = DigitsOnlyInputTransformation(2),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done,
                        showKeyboardOnFocus = true,
                    ),
                    suffix = { Text(text = "Events") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,

                    ),
                )
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Preview
@Composable
private fun InvervalRepeatEndPreview() {
    IntervalRepeatEnd(
        currentIntervalEnd = IntervalEnd.Forever,
        todayDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
    )
}

class DigitsOnlyInputTransformation(private val maxLength: Int = Int.MAX_VALUE) :
    InputTransformation {

    override fun TextFieldBuffer.transformInput() {
        val originalText = asCharSequence().toString()
        val newText = originalText.filter { it.isDigit() }

        // If newText is longer than maxLength, truncate it
        val finalText = if (newText.length > maxLength) {
            newText.substring(0, maxLength)
        } else {
            newText
        }

        // Only update if the filtered/truncated text is different from what's already in the buffer
        // or if the original text had non-digit characters that were removed.
        if (finalText != asCharSequence().toString() || originalText.any { !it.isDigit() }) {
            // Replace the entire buffer with the filtered and potentially truncated text
            replace(0, length, finalText)
        }
    }
}
