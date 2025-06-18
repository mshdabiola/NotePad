package com.mshdabiola.ui

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
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.InputChip
import androidx.compose.material3.RadioButton
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationDialogInterval(
    modifier: Modifier = Modifier,
    initInterval: NotificationInterval,
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
                                suffix = "days",
                                state = daily.interval,
                            )

                            IntervalRepeatEnd(
                                currentIntervalEnd = daily.intervalEnd,
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
                                suffix = "weeks",
                                state = weekly.interval,
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
                                                if (isSelected)
                                                    newDays.remove(index)
                                                else
                                                    newDays.add(index)
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
                                onValueChange = {
                                    currentInterval = weekly.copy(intervalEnd = it)

                                },
                            )
                        }

                        is NotificationInterval.Monthly -> {
                            val monthly = currentInterval as NotificationInterval.Monthly
                            IntervalTextField(
                                prefix = "Every",
                                suffix = "months",
                                state = monthly.interval,
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                RadioButton(monthly.sameDay, onClick = {})
                                Text(
                                    modifier = Modifier.weight(1f),
                                    text = "On same day each month",
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                RadioButton(!monthly.sameDay, onClick = {})
                                Text(modifier = Modifier.weight(1f), text = "On Third of Tuesday")
                            }

                            IntervalRepeatEnd(
                                currentIntervalEnd = monthly.intervalEnd,
                                onValueChange = {
                                    currentInterval = monthly.copy(intervalEnd = it)

                                },
                            )
                        }

                        is NotificationInterval.Yearly -> {
                            val yearly = currentInterval as NotificationInterval.Yearly
                            IntervalTextField(
                                prefix = "Every",
                                suffix = "years",
                                state = yearly.interval,
                            )

                            IntervalRepeatEnd(
                                currentIntervalEnd = yearly.intervalEnd,
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

@OptIn(ExperimentalMaterial3Api::class)
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
        inputTransformation = DigitsOnlyInputTransformation(2),
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
                DateTextDropbox(
                    modifier = Modifier.weight(2f),
                    currentDate = currentIntervalEnd.date,
                    onValueChange = {
                        onValueChange(currentIntervalEnd.copy(it))
                    },
                )
            }

            is IntervalEnd.NumberOfTimes -> {
                val numberOfTimesState = rememberTextFieldState(currentIntervalEnd.times.toString())
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
