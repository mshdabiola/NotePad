package com.mshdabiola.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateDialog(
    state: DatePickerState = DatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis(),
        initialDisplayedMonthMillis = System.currentTimeMillis(),
        yearRange = DatePickerDefaults.YearRange,
        initialDisplayMode = DisplayMode.Picker,
        locale = Locale.getDefault()
    ), showDialog: Boolean = true, onDismissRequest: () -> Unit = {}, onSetDate: () -> Unit = {}
) {


    AnimatedVisibility(visible = showDialog) {
        DatePickerDialog(onDismissRequest = onDismissRequest,
            confirmButton = {
                Button(onClick = {
                    onSetDate()
                    onDismissRequest()
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
                //   dateValidator = { it > (System.currentTimeMillis() - (48 * 60 * 60 * 1000)) }
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun DateDialogPreview() {
    DateDialog()
}
