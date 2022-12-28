package com.mshdabiola.mainscreen.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mshdabiola.mainscreen.state.NotePadUiState
import com.mshdabiola.mainscreen.state.NoteUiState
import kotlinx.collections.immutable.toImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteCard(notePadUiState: NotePadUiState, onCardClick: (Long) -> Unit = {}) {
    val unCheckNote by remember(notePadUiState.checks) {
        derivedStateOf { notePadUiState.checks.filter { !it.isCheck } }
    }
    val numberOfChecked by remember(key1 = notePadUiState.checks) {
        derivedStateOf { notePadUiState.checks.count { it.isCheck } }
    }
    OutlinedCard(
        modifier = Modifier,
        onClick = { notePadUiState.note.id?.let { onCardClick(it) } }) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = notePadUiState.note.title.ifEmpty { notePadUiState.note.detail },
                style = MaterialTheme.typography.titleMedium
            )
            if (!notePadUiState.note.isCheck) {
                if (notePadUiState.note.title.isNotEmpty()) {
                    if (notePadUiState.note.detail.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = notePadUiState.note.detail,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 10,

                            )
                    }
                }
            } else {
                unCheckNote.take(10).forEach {

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = it.isCheck, onCheckedChange = {}, enabled = false)
                        Text(it.content, style = MaterialTheme.typography.bodyMedium)

                    }
                }
                if (unCheckNote.size > 10) {
                    Text(text = "....")
                }
                if (numberOfChecked > 0) {
                    Text(text = "+ $numberOfChecked checked items")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState(), enabled = false)
            ) {
                notePadUiState.labels.forEach {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        border = BorderStroke(1.dp, Color.Gray)
                    ) {
                        Text(text = it, modifier = Modifier.padding(8.dp))
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                }
            }
        }

    }
}

@Preview
@Composable
fun NoteCardPreview() {
    NoteCard(
        notePadUiState = NotePadUiState(
            note = NoteUiState(
                id = null,
                title = "Mandy",
                detail = "Lamia ",
                date = 314L,
                isCheck = false,
                color = 8830,
                isPin = false
            ),
            labels = listOf(
                "ade",
                "food",
                "abiola",
                "kdlskdflsjfslf",
                "klslssljsl",
                "alskfk"
            ).toImmutableList()
        )
    )
}