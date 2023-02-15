package com.mshdabiola.designsystem.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateDialog(
    currentDate : Long=System.currentTimeMillis()+(48*60*60*1000),
    showDialog: Boolean=true,
    onDismissRequest: () -> Unit={},
    onSetDate :(LocalDate)->Unit={}
) {
//    DatePickerDialog(
//        context,
//        { _, y, m, d ->
//            dateTime = LocalDateTime(
//                LocalDate(y, m + 1, d),
//                dateTime.time,
//            )
//        },
//        dateTime.year,
//        dateTime.monthNumber - 1,
//        dateTime.dayOfMonth,
//
//        ).show()
    val state= rememberDatePickerState(
        initialSelectedDateMillis = currentDate,
        initialDisplayedMonthMillis = currentDate

    )

    AnimatedVisibility(visible = showDialog) {
        DatePickerDialog(onDismissRequest =onDismissRequest,
            confirmButton = {
                Button(onClick = {
                    state.selectedDateMillis?.let {
                        val dateTime= Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.currentSystemDefault())
                        onSetDate(dateTime.date)
                    }
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
                dateValidator = {it>=System.currentTimeMillis()}
            )
        }
    }

}

@Preview
@Composable
fun DateDialogPreview() {
    DateDialog()
}
