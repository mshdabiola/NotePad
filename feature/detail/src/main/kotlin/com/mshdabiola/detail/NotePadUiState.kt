package com.mshdabiola.detail

import androidx.compose.foundation.text.input.TextFieldState
import com.mshdabiola.model.NoteCheck
import com.mshdabiola.model.NotePad
import com.mshdabiola.model.NoteType
import com.mshdabiola.ui.state.NoteCheckUiState
import com.mshdabiola.ui.state.NoteImageUiState
import com.mshdabiola.ui.state.NoteTypeUi
import com.mshdabiola.ui.state.NoteUriState
import com.mshdabiola.ui.state.NoteVoiceUiState
import com.mshdabiola.ui.state.toNoteImage
import com.mshdabiola.ui.state.toNoteImageUiState
import com.mshdabiola.ui.state.toNoteVoice
import com.mshdabiola.ui.state.toNoteVoiceUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.Clock

data class NotePadUiState(
    val id: Long = -1,
    val title: TextFieldState = TextFieldState(),
    val detail: TextFieldState = TextFieldState(),
    val editDate: Long = Clock.System.now().toEpochMilliseconds(),
    val isCheck: Boolean = false,
    val color: Int = -1,
    val background: Int = -1,
    val isPin: Boolean = false,
    val reminder: Long = 0,
    val interval: Long = 0,
    val selected: Boolean = false,
    val focus: Boolean = false,
    val date: String = "feb 1",
    val lastEdit: String = "feb 1",
    val type: NoteType = NoteType.NOTE,

    val images: ImmutableList<NoteImageUiState> = emptyList<NoteImageUiState>().toImmutableList(),
    val voices: ImmutableList<NoteVoiceUiState> = emptyList<NoteVoiceUiState>().toImmutableList(),
    val checks: ImmutableList<NoteCheckUiState2> = emptyList<NoteCheckUiState2>().toImmutableList(),
    val labels: ImmutableList<String> = emptyList<String>().toImmutableList(),
    val uris: ImmutableList<NoteUriState> = emptyList<NoteUriState>().toImmutableList(),
)

fun NotePad.toNotePadUiState(
    toPath: (Long) -> String,
) = NotePadUiState(
    id = id,
    title = TextFieldState(title),
    detail = TextFieldState(detail),
    editDate = editDate,
    isCheck = isCheck,
    color = color,
    background = background,
    isPin = isPin,
    reminder = reminder,
    interval = interval,
    selected = false,
    images = images.map { it.toNoteImageUiState(toPath) }.toImmutableList(),
    voices = voices.map { it.toNoteVoiceUiState() }.toImmutableList(),
    checks = checks.map { it.toNoteCheckUiState() }.toImmutableList(),
    labels = labels.map { s -> s.label }
        .toImmutableList(),
)

fun NotePadUiState.toNotePad() = NotePad(
    id = id,
    title = title.text.toString(),
    detail = detail.text.toString(),
    editDate = editDate,
    isCheck = isCheck,
    color = color,
    background = background,
    isPin = isPin,
    reminder = reminder,
    interval = interval,
    images = images.map { it.toNoteImage() },
    voices = voices.map { it.toNoteVoice() },
    checks = checks.map { it.toNoteCheck() },
)


data class NoteCheckUiState2(
    val id: Long,
    val noteId: Long,
    val content: TextFieldState = TextFieldState(),
    val isCheck: Boolean = false,
    val focus: Boolean = false,
)

fun NoteCheck.toNoteCheckUiState() = NoteCheckUiState2(id, noteId, TextFieldState(content), isCheck)

fun NoteCheckUiState2.toNoteCheck() = NoteCheck(id, noteId, content.text.toString(), isCheck)
