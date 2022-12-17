package com.mshdabiola.editscreen.state

import com.mshdabiola.model.NotePad
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

data class NotePadUiState(
    val note: NoteUiState,
    val images: ImmutableList<NoteImageUiState>,
    val voices: ImmutableList<NoteVoiceUiState>,
    val checks: ImmutableList<NoteCheckUiState>
)


fun NotePad.toNotePadUiState() = NotePadUiState(
    note = note.toNoteUiState(),
    images = images.map { it.toNoteImageUiState() }.toImmutableList(),
    voices = voices.map { it.toNoteVoiceUiState() }.toImmutableList(),
    checks = checks.map { it.toNoteCheckUiState() }.toImmutableList()
)

fun NotePadUiState.toNotePad() = NotePad(
    note = note.toNote(),
    images = images.map { it.toNoteImage() },
    voices = voices.map { it.toNoteVoice() },
    checks = checks.map { it.toNoteCheck() }
)
