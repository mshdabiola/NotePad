package com.mshdabiola.editscreen.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NotificationDialog(
    showDialog: Boolean = true,
    onDismissRequest: () -> Unit = {}
) {

    val state = rememberPagerState(0)
    val coroutineScope = rememberCoroutineScope()
    AnimatedVisibility(visible = showDialog) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text(text = "Edit Reminder") },
            text = {

                Column {
                    TabRow(selectedTabIndex = state.currentPage) {
                        Tab(selected = state.currentPage == 0, onClick = {

                            coroutineScope.launch {
                                state.animateScrollToPage(0)
                            }

                        }) {
                            Text(text = "Time")
                        }
                        Tab(selected = state.currentPage == 1, onClick = {

                            coroutineScope.launch {
                                state.animateScrollToPage(1)
                            }
                        }) {
                            Text(text = "Place")
                        }
                    }
                    HorizontalPager(
                        pageCount = 2,
                        state = state
                    ) {

                        if (it == 0) {
                            TimeContent()
                        } else {
                            Column {
                                Text(text = "Location")
                            }

                        }


                    }
                }


            },
            confirmButton = {},
            dismissButton = {}
        )
    }

}

@Preview
@Composable
fun NotificationDialogPreview() {
    NotificationDialog()
}


@Composable
fun TimeContent() {
    val time = LocalDateTime(LocalDate(2022, 12, 31), LocalTime(18, 43))

    val instant = time.toInstant(TimeZone.UTC)

    Column {
        TimeDropbox(value = instant.toEpochMilliseconds())
        DateDropbox(value = instant.toEpochMilliseconds())
        RepeatDropbox(value = 0)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeDropbox(value: Long, onValueChange: (Int) -> Unit = {}) {

    var expanded by remember {
        mutableStateOf(false)
    }

    val options = listOf(
        Pair("Morning", LocalTime(7, 0).toMillisecondOfDay()),
        Pair("Afternoon", LocalTime(13, 0).toMillisecondOfDay()),
        Pair("Evening", LocalTime(19, 0).toMillisecondOfDay()),
        Pair("Night", LocalTime(20, 0).toMillisecondOfDay()),
        Pair("Pick time", 0)
    )

    ExposedDropdownMenuBox(
        modifier = Modifier,
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            value = value.toTimeString(),
            onValueChange = {},
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            singleLine = true

        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = {
            expanded = false
        }) {
            options.forEach { pair ->
                DropdownMenuItem(
                    text = { Text(text = pair.first) },
                    onClick = {

                        onValueChange(pair.second)
                        expanded = false
                    },
                    trailingIcon = { Text(text = pair.second.toLong().toTimeString()) }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateDropbox(value: Long, onValueChange: (Int) -> Unit = {}) {
    var expanded by remember {
        mutableStateOf(false)
    }
    var curr by remember {
        mutableStateOf(0)
    }
    val options = listOf(
        Pair("Today", LocalDate(2, 4, 5).toEpochDays()),
        Pair("Tomorrow", LocalDate(2, 4, 5).toEpochDays()),
        Pair("Pick date", 0)
    )
    ExposedDropdownMenuBox(
        modifier = Modifier,
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            value = value.toDateString(),
            onValueChange = {},
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            singleLine = true

        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = {
            expanded = false
        }) {
            options.forEachIndexed { index, string ->
                DropdownMenuItem(
                    text = { Text(text = string.first) },
                    onClick = {

                        onValueChange(index)
                        expanded = false
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepeatDropbox(value: Int, onValueChange: (Int) -> Unit = {}) {
    var expanded by remember {
        mutableStateOf(false)
    }

    val options = listOf(
        Pair("Daily", DateTimeUnit.HOUR.times(24).nanoseconds.times(1000)),
        Pair("Weekly", DateTimeUnit.HOUR.times(24 * 7).nanoseconds.times(1000)),
        Pair("Monthly", DateTimeUnit.HOUR.times(24 * 7 * 30).nanoseconds.times(1000)),
        Pair("Yearly", DateTimeUnit.HOUR.times(24 * 7 * 30).nanoseconds.times(1000)),
        Pair("Pick time", 0)
    )

    ExposedDropdownMenuBox(
        modifier = Modifier,
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            value = options[value].first,
            onValueChange = {},
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            singleLine = true

        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = {
            expanded = false
        }) {
            options.forEachIndexed { index, string ->
                DropdownMenuItem(
                    text = { Text(text = string.first) },
                    onClick = {

                        onValueChange(index)
                        expanded = false
                    }
                )
            }
        }
    }
}


@Preview
@Composable
fun TimeColumnPreview() {
    TimeContent()
}


fun Long.toTimeString(): String {
    val instant =
        Instant.fromEpochMilliseconds(this)

    val dateTime = instant.toLocalDateTime(TimeZone.UTC)
    val hour = dateTime.hour % 12L
    val a = if (dateTime.hour > 11) "PM" else "AM"
    return "%02d : %02d %s".format(hour, dateTime.minute, a)
}


fun Long.toDateString(): String {
    val instant =
        Instant.fromEpochMilliseconds(this)
    val dateTime = instant.toLocalDateTime(TimeZone.UTC)



    return "${
        dateTime.month.name.lowercase().replaceFirstChar { it.uppercase() }
    } ${dateTime.dayOfMonth}, ${dateTime.year}"
}