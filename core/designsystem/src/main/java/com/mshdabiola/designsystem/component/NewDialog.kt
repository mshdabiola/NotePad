package com.mshdabiola.designsystem.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mshdabiola.designsystem.component.state.DateDialogUiData
import com.mshdabiola.designsystem.component.state.DateListUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList


@Composable
fun NotificationDialogNew(
    dateDialogUiData: DateDialogUiData,
    showDialog: Boolean = false,
    onDismissRequest: () -> Unit = {},
    onSetAlarm: () -> Unit = { },
    onDeleteAlarm: () -> Unit = {},
    onTimeChange: (Int) -> Unit = {},
    onDateChange: (Int) -> Unit = {},
    onIntervalChange: (Int) -> Unit = {}


) {

    AnimatedVisibility(visible = showDialog) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text(text = if (dateDialogUiData.isEdit) "Edit Reminder" else "Add Reminder") },
            text = {
                Column {
                    TextDropbox(
                        currentIndex = dateDialogUiData.currentTime,
                        showError = dateDialogUiData.timeError,
                        onValueChange = onTimeChange,
                        times = dateDialogUiData.timeData
                    )
                    TextDropbox(
                        currentIndex = dateDialogUiData.currentDate,
                        showError = false,
                        onValueChange = onDateChange,
                        times = dateDialogUiData.dateData
                    )
                    TextDropbox(
                        currentIndex = dateDialogUiData.currentInterval,
                        showError = false,
                        onValueChange = onIntervalChange,
                        times = dateDialogUiData.interval
                    )
                }

            },
            confirmButton = {
                Button(onClick = {
                    onSetAlarm()
                    onDismissRequest()
                },
                    enabled = !dateDialogUiData.timeError
                ) {
                    Text(text = "Save")
                }
            },
            dismissButton = {
                Row {
                    if (dateDialogUiData.isEdit) {
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

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextDropbox(
    currentIndex: Int,
    onValueChange: (Int) -> Unit = {},
    times: ImmutableList<DateListUiState> = emptyList<DateListUiState>().toImmutableList(),
    showError: Boolean
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    ExposedDropdownMenuBox(
        modifier = Modifier,
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        TextField(
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            value = times[currentIndex].value,
            supportingText = { if (showError) Text(text = "Time as past") },
            isError = showError,
            onValueChange = {},
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            singleLine = true,

            )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = {
            expanded = false
        }) {
            times.forEachIndexed { index, pair ->
                DropdownMenuItem(
                    text = { Text(text = pair.title) },
                    onClick = {
                        onValueChange(index)
                        expanded = false
                    },
                    enabled = pair.enable,
                    trailingIcon = {
                        pair.trail?.let {
                            Text(
                                text = it,
                            )
                        }
                    },
                )
            }
        }
    }
}


@Preview
@Composable
fun NewDialogPreview() {
    val dateDialog = DateDialogUiData(
        isEdit = false,
        currentTime = 0,
        timeData = listOf(
            DateListUiState(
                title = "Morning",
                value = "8:00PM",
                trail = "8:00PM",
                isOpenDialog = false,
                enable = true
            ),
            DateListUiState(
                title = "Afternoon",
                value = "8:00PM",
                trail = "8:00PM",
                isOpenDialog = false,
                enable = true
            ),
            DateListUiState(
                title = "Evening",
                value = "8:00PM",
                trail = "8:00PM",
                isOpenDialog = false,
                enable = true
            ),
            DateListUiState(
                title = "Night",
                value = "8:00PM",
                trail = "8:00PM",
                isOpenDialog = false,
                enable = true
            ),
            DateListUiState(
                title = "Pick time",
                value = "8:00PM",
                isOpenDialog = true,
                enable = true
            )

        ).toImmutableList(),
        timeError = false,
        currentDate = 0,
        dateData = listOf(
            DateListUiState(
                title = "Today",
                value = "Today",
                isOpenDialog = false,
                enable = true
            ),
            DateListUiState(
                title = "Tomorrow",
                value = "Tomorrow",
                isOpenDialog = true,
                enable = true
            ),
            DateListUiState(
                title = "Pick date",
                value = "Jan 1",
                isOpenDialog = true,
                enable = true
            )
        ).toImmutableList(),
        currentInterval = 0,
        interval = listOf(
            DateListUiState(
                title = "Does not repeat",
                value = "Does not repeat",
                isOpenDialog = false,
                enable = true
            ),
            DateListUiState(
                title = "Daily",
                value = "Daily",
                isOpenDialog = false,
                enable = true
            ),
            DateListUiState(
                title = "Weekly",
                value = "Weekly",
                isOpenDialog = false,
                enable = true
            ),
            DateListUiState(
                title = "Monthly",
                value = "Monthly",
                isOpenDialog = false,
                enable = true
            ),
            DateListUiState(
                title = "Yearly",
                value = "Yearly",
                isOpenDialog = false,
                enable = true
            )
        ).toImmutableList()

    )

    NotificationDialogNew(
        showDialog = true,
        dateDialogUiData = dateDialog
    )
}


