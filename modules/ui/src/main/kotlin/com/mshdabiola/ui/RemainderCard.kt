package com.mshdabiola.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mshdabiola.model.NotificationInterval
import com.mshdabiola.model.NotificationUiState
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReminderCard(
    notification: NotificationUiState,
    color: Color,
    style: TextStyle = MaterialTheme.typography.bodySmall,
    onClick: (() -> Unit)? = null,
) {
    Surface(
        modifier = Modifier
            .clickable(enabled = onClick != null) { onClick?.invoke() },
        shape = RoundedCornerShape(8.dp),
        color = color,
        border = BorderStroke(1.dp, Color.Gray),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(4.dp),
        ) {
            if (notification.currentInterval !is NotificationInterval.DoNotRepeat) {
                Icon(
                    modifier = Modifier.size(16.dp),
                    imageVector = Icons.Default.Repeat,
                    contentDescription = "",
                )
                Spacer(modifier = Modifier.width(2.dp))
            } else {
                Icon(
                    modifier = Modifier.size(16.dp),
                    imageVector = Icons.Outlined.Alarm,
                    contentDescription = "",
                )
                Spacer(modifier = Modifier.width(2.dp))
            }
            if (notification.currentPlace != null) {
                Text(
                    text = "Place",
                    style = style,
                    maxLines = 1,
                )
            } else {
                Text(
                    modifier = Modifier.basicMarquee(),
                    text = notification.currentDateTime.myFormat(),
                    style = style,
                    maxLines = 1,

                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalTime::class)
@Preview
@Composable
fun ReminderCardPreview() {
    val notification = NotificationUiState(
        currentDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
        currentInterval = NotificationInterval.DoNotRepeat,
        currentPlace = null,
    )
    ReminderCard(
        notification = notification,
        color = Color.White,
    )
}

@Composable
fun LabelCard(
    name: String,
    color: Color,
    style: TextStyle = MaterialTheme.typography.bodySmall,
    onClick: (() -> Unit)? = null,
) {
    Surface(
        modifier = Modifier.clickable(enabled = onClick != null, onClick = { onClick?.invoke() }),
        shape = RoundedCornerShape(8.dp),
        color = color,
        border = BorderStroke(1.dp, Color.Gray),
    ) {
        Text(
            text = name,
            style = style,
            modifier = Modifier.padding(4.dp),
        )
    }
}

@Preview
@Composable
fun LabelCardPreview() {
    LabelCard(
        name = "Food",
        color = Color.Red,
    )
}

@OptIn(ExperimentalTime::class)
fun LocalDateTime.myFormat(): String {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    return when (this.date) {
        now.date -> {
            "Today, ${this.time.format(timeFormater)}"
        }
        now.date.minus(1, DateTimeUnit.DAY) -> {
            "Yesterday, ${this.time.format(timeFormater)}"
        }
        now.date.plus(1, DateTimeUnit.DAY) -> {
            "Tomorrow, ${this.time.format(timeFormater)}"
        }
        else -> {
            "${this.date}, ${this.time.format(timeFormater)}"
        }
    }
}

val timeFormater = LocalTime.Format {

    this.amPmHour(Padding.SPACE)
    char(':')
    minute(Padding.SPACE)
    char(' ')
    amPmMarker("AM", "PM")
}
