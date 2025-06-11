package com.mshdabiola.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.mshdabiola.designsystem.component.NoteButton
import com.mshdabiola.designsystem.component.NoteTextButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeDialog(
    state: TimePickerState = TimePickerState(12, 4, is24Hour = false),
    showDialog: Boolean = true,
    onDismissRequest: () -> Unit = {},
    onSetTime: () -> Unit = {},
) {
    AnimatedVisibility(visible = showDialog) {
        DatePickerDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = {
                NoteButton(onClick = {
                    onSetTime()

                    onDismissRequest()
                }) {
                    Text(text = "Set time")
                }
            },
            dismissButton = {
                NoteTextButton(onClick = onDismissRequest) {
                    Text(text = "Cancel")
                }
            },
        ) {
            TimePicker(state = state)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun TimeDialogPreview() {
    TimeDialog()
}
