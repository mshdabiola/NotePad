package com.mshdabiola.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mshdabiola.designsystem.icon.NoteIcon
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus

@Composable
fun ReminderCard(
    remainder: Long,
    interval: Long,
    color: Color,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        color = color,
        border = BorderStroke(1.dp, Color.Gray)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(4.dp)
        ) {
            if (interval > 0) {
                Icon(
                    modifier = Modifier.size(16.dp),
                    painter = painterResource(id = NoteIcon.Repeat),
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.width(2.dp))
            } else {
                Icon(
                    modifier = Modifier.size(16.dp),
                    painter = painterResource(id = NoteIcon.Alarm),
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.width(2.dp))
            }
            Text(
                text = "${remainder.toDateString()}, ${
                    remainder.toTimeString(
                        true
                    )
                }",
                style = MaterialTheme.typography.bodySmall

            )
        }
    }
}

@Preview
@Composable
fun RemainderCardPreview() {
    val time = Clock.System.now().minus(24, DateTimeUnit.HOUR)
    ReminderCard(remainder = time.toEpochMilliseconds(), interval = -1, color = Color.Red)
}

@Composable
fun LabelCard(
    name: String,
    color: Color,
    style: TextStyle = MaterialTheme.typography.bodySmall,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        color = color,
        border = BorderStroke(1.dp, Color.Gray)
    ) {

        Text(
            text = name,
            style = style,
            modifier = Modifier.padding(4.dp)
        )
    }
}

@Preview
@Composable
fun LabelCardPreview() {
    LabelCard(
        name = "Food",
        color = Color.Red
    )
}