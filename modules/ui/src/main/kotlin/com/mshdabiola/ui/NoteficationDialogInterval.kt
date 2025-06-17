package com.mshdabiola.ui

import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.getSelectedDate
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mshdabiola.designsystem.R
import com.mshdabiola.ui.state.IntervalEnd
import com.mshdabiola.ui.state.NotificationInterval
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationDialogInterval(
    modifier: Modifier = Modifier,
    currentInterval: NotificationInterval,
    intervals: List<NotificationInterval>,
    showDialog: Boolean = false,
    onValueChange: (NotificationInterval) -> Unit = {},
) {
    var expanded by remember {
        mutableStateOf(false)
    }
    val nowDate = remember {
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    }

    val intervalStringArray = stringArrayResource(
        R.array.modules_designsystem_notification_interval,
    )

    val state = rememberTextFieldState()
    LaunchedEffect(key1 = currentInterval) {
        state.clearText()
        val index = intervals.indexOf(currentInterval)
        state.edit {
            append(intervalStringArray[index])
        }
    }

//    BasicAlertDialog(
//        modifier = modifier,
//        onDismissRequest = { },
//    ) {
    Surface(
        shape = ShapeDefaults.Small,
    ) {
        Column {
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
                    intervals.forEachIndexed { index, notificationTime ->
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
            when (currentInterval) {
                is NotificationInterval.Daily -> {
                    IntervalTextField(
                        prefix = "Every",
                        suffix = "days",
                        state = currentInterval.interval,
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    IntervalRepeatEnd(
                        currentIntervalEnd = currentInterval.intervalEnd,
                        onValueChange = {},
                    )
                }

                is NotificationInterval.Weekly -> {
                    val daysOfWeek = stringArrayResource(R.array.modules_designsystem_days_of_weeks)
                    IntervalTextField(
                        prefix = "Every",
                        suffix = "weeks",
                        state = currentInterval.interval,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow {
                        daysOfWeek
                            .map { it.take(3) }
                            .forEachIndexed { index, days ->
                                val contain = index in currentInterval.days
                                Surface(
                                    shape = CircleShape,
                                    border = BorderStroke(4.dp, MaterialTheme.colorScheme.primaryContainer),
                                    color = if (contain) {
                                        MaterialTheme.colorScheme.primaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.surface
                                    },
                                    contentColor = if (contain) {
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    },

                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(days)
                                    }
                                }
                            }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    IntervalRepeatEnd(
                        currentIntervalEnd = currentInterval.intervalEnd,
                        onValueChange = {},
                    )
                }

                is NotificationInterval.Monthly -> {
                    IntervalTextField(
                        prefix = "Every",
                        suffix = "months",
                        state = currentInterval.interval,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(currentInterval.sameDay, onClick = {})
                        Text(modifier = Modifier.weight(1f), text = "On same day each month")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(!currentInterval.sameDay, onClick = {})
                        Text(modifier = Modifier.weight(1f), text = "On Third of Tuesday")
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    IntervalRepeatEnd(
                        currentIntervalEnd = currentInterval.intervalEnd,
                        onValueChange = {},
                    )
                }

                is NotificationInterval.Yearly -> {
                    IntervalTextField(
                        prefix = "Every",
                        suffix = "years",
                        state = currentInterval.interval,
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    IntervalRepeatEnd(
                        currentIntervalEnd = currentInterval.intervalEnd,
                        onValueChange = {},
                    )
                }

                is NotificationInterval.DoNotRepeat -> {
                    Spacer(modifier = Modifier.height(64.dp))
                }

                is NotificationInterval.Custom -> {
                }
            }
        }
    }

//    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun NotificationDialogIntervalPreview() {
    val nowDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val currentInterval = NotificationInterval.Weekly(
        intervalEnd = IntervalEnd.EndDate(LocalDate(2023, 1, 1)),
        days = setOf(0, 4, 6),
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
        currentInterval = currentInterval,
        intervals = intervals,
        showDialog = true,
        onValueChange = {},
    )
}

@Composable
fun IntervalTextField(
    modifier: Modifier = Modifier,
    prefix: String = "",
    suffix: String = "",
    state: TextFieldState = rememberTextFieldState(),
) {
    TextField(
        modifier = modifier,
        state = state,
        lineLimits = TextFieldLineLimits.SingleLine,
        inputTransformation = DigitsOnlyInputTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done,
            showKeyboardOnFocus = true,
        ),
        prefix = { Text(text = prefix) },
        suffix = { Text(text = suffix) },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,

        ),
    )
}

@Preview
@Composable
fun IntervalTextFieldPreview() {
    IntervalTextField(
        prefix = "Every",
        suffix = "days",
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntervalRepeatEnd(
    modifier: Modifier = Modifier,
    currentIntervalEnd: IntervalEnd,
    onValueChange: (IntervalEnd) -> Unit = {},
) {
    val intervalEndStringArray = stringArrayResource(
        R.array.modules_designsystem_notification_interval_end2,
    )

    val intervalsEnds: Map<IntervalEnd, String> = remember {
        mapOf(
            IntervalEnd.Forever to intervalEndStringArray[0],
            IntervalEnd.EndDate(LocalDate(2023, 1, 1)) to intervalEndStringArray[1],
            IntervalEnd.NumberOfTimes(1) to intervalEndStringArray[2],
        )
    }
    var expanded by remember {
        mutableStateOf(false)
    }
    val state = rememberTextFieldState()
    LaunchedEffect(key1 = currentIntervalEnd) {
        val current = intervalsEnds[currentIntervalEnd]
        state.clearText()
        state.edit {
            append(current)
        }
    }

    Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        ExposedDropdownMenuBox(
            modifier = modifier,
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
                        text = { Text(text = intervalsEnds.value) },
                        onClick = {
                            onValueChange(intervalsEnds.key)
                            state.clearText()
                            state.edit {
                                append(intervalsEnds.value)
                            }
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
                TextField(
                    modifier = modifier.clickable {
                        showDateDialog = true
                    },
                    state = dateTextFiledState,
                    readOnly = true,
                    lineLimits = TextFieldLineLimits.SingleLine,
                    inputTransformation = DigitsOnlyInputTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done,
                        showKeyboardOnFocus = true,
                    ),
                    suffix = { Text(text = "Events") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,

                    ),
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
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        dateState.getSelectedDate()?.toKotlinLocalDate()?.let {
                                            onValueChange(IntervalEnd.EndDate(it))
                                        }
                                    } else {
                                        onValueChange(IntervalEnd.Forever)
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

            is IntervalEnd.NumberOfTimes -> {
                TextField(
                    modifier = modifier,
                    state = state,
                    lineLimits = TextFieldLineLimits.SingleLine,
                    inputTransformation = DigitsOnlyInputTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done,
                        showKeyboardOnFocus = true,
                    ),
                    suffix = { Text(text = "Events") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,

                    ),
                )
            }
        }
    }
}

@Preview
@Composable
private fun InvervalRepeatEndPreview() {
    IntervalRepeatEnd(
        currentIntervalEnd = IntervalEnd.Forever,
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
