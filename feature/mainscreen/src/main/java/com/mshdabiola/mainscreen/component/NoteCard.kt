package com.mshdabiola.mainscreen.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mshdabiola.mainscreen.state.NoteUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteCard(noteUiState: NoteUiState, onCardClick: (Long) -> Unit = {}) {

    OutlinedCard(
        modifier = Modifier,
        onClick = { noteUiState.id?.let { onCardClick(it) } }) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = noteUiState.title.ifEmpty { noteUiState.detail },
                style = MaterialTheme.typography.titleMedium
            )

            if (noteUiState.title.isNotEmpty()) {
                if (noteUiState.detail.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = noteUiState.detail, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

    }
}

@Preview
@Composable
fun NoteCardPreview() {
    NoteCard(noteUiState = NoteUiState())
}