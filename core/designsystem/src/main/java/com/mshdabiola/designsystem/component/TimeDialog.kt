package com.mshdabiola.designsystem.component

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.Button
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.datetime.LocalTime


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeDialog(
    hour : Int=13,
    minute:Int=55,
    showDialog: Boolean=true,
    onDismissRequest: () -> Unit={},
    onSetTime :(LocalTime)->Unit={}
) {

    val state = rememberTimePickerState(initialHour = hour, initialMinute = minute,is24Hour = true)
    AnimatedVisibility(visible =showDialog) {
        DatePickerDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = {
                Button(onClick = {
                    Log.e("Date Picker","hour ${state.hour} minute ${state.minute}")
                    val time= LocalTime(state.hour,state.minute)
                    Log.e("Date Picker","hour2 ${time.hour} minute2 ${time.minute}")
                    onSetTime(time)

                    onDismissRequest()
                })
                {
                    Text(text = "Set time")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(text = "Cancel")
                }
            }
        ) {

            TimePicker(state = state)
        }
    }


}

@Preview
@Composable
fun TimeDialogPreview() {

    TimeDialog()
}
