package com.mshdabiola.mainscreen.component

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.mshdabiola.mainscreen.state.NoteUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteCard(noteUiState: NoteUiState, onCardClick: (Long) -> Unit = {}) {

    OutlinedCard(
        modifier = Modifier,
        onClick = { noteUiState.id?.let { onCardClick(it) } }) {
//        Column(Modifier.padding(16.dp)) {
//            Text(text = noteUiState.title, style = MaterialTheme.typography.titleMedium)
//            Spacer(modifier = Modifier.height(8.dp))
//            Text(text = noteUiState.detail, style = MaterialTheme.typography.bodyMedium)
//        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            ListItem(
                headlineText = {
                    Text(text = noteUiState.title.ifEmpty { noteUiState.detail })
                },
                supportingText = {
                    if (noteUiState.title.isNotEmpty()) {
                        if (noteUiState.detail.isNotEmpty()) {
                            Text(
                                text = noteUiState.detail,

                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview
@Composable
fun NoteCardPreview() {
    NoteCard(noteUiState = NoteUiState())
}