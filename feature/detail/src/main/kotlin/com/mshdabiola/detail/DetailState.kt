package com.mshdabiola.detail

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.mshdabiola.model.NotePad

@Stable // Good practice for Compose state classes
data class DetailState(
    val notePad: NotePad = NotePad(),
    val title: TextFieldState = TextFieldState(),
    val detail: TextFieldState = TextFieldState(),
    val checks: SnapshotStateList<NoteCheckUiState> = mutableStateListOf(),
    val unChecks: SnapshotStateList<NoteCheckUiState> = mutableStateListOf(),
    val updateAt: String = "Today, 12 : 45 AM",
)
