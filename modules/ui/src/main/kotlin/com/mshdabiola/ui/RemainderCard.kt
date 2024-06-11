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
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReminderCard(
    date: String,
    interval: Long,
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
            if (interval > 0) {
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
            Text(
                modifier = Modifier.basicMarquee(),
                text = date,
                style = style,
                maxLines = 1,

            )
        }
    }
}

@Preview
@Composable
fun RemainderCardPreview() {
    val time = Clock.System.now().minus(24, DateTimeUnit.HOUR)
    ReminderCard(date = "Today, 1:29 AM", interval = -1, color = Color.Red)
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
