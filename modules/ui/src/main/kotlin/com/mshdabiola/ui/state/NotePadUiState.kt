package com.mshdabiola.ui.state

import com.mshdabiola.model.NotePad
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.Clock

data class NotePadUiState(
    val id: Long = -1,
    val title: String = "",
    val detail: String = "",
    val editDate: Long = Clock.System.now().toEpochMilliseconds(),
    val isCheck: Boolean = false,
    val color: Int = -1,
    val background: Int = -1,
    val isPin: Boolean = false,
    val reminder: Long = 0,
    val interval: Long = 0,
    val selected: Boolean = false,
    val noteType: NoteTypeUi = NoteTypeUi(),
    val focus: Boolean = false,
    val date: String = "feb 1",
    val lastEdit: String = "feb 1",
    val images: ImmutableList<NoteImageUiState> = emptyList<NoteImageUiState>().toImmutableList(),
    val voices: ImmutableList<NoteVoiceUiState> = emptyList<NoteVoiceUiState>().toImmutableList(),
    val checks: ImmutableList<NoteCheckUiState> = emptyList<NoteCheckUiState>().toImmutableList(),
    val labels: ImmutableList<String> = emptyList<String>().toImmutableList(),
    val uris: ImmutableList<NoteUriState> = emptyList<NoteUriState>().toImmutableList(),
) {
    override fun toString(): String {
        val checkString = checks.joinToString(separator = "\n") {
            if (it.isCheck) {
                "[*] ${it.content}"
            } else {
                "[ ] ${it.content}"
            }
        }
        return if (checkString.isNotBlank()) {
            "$title \n $checkString"
        } else {
            "$title \n $detail"
        }
    }

    fun isEmpty(): Boolean {
        val titleIsBlank = title.isBlank()
        val detailIsBlank = detail.isBlank()
        val emptyImage = images.isEmpty()
        val voiceEmpty = voices.isEmpty()
        val checksBlank = checks.all { it.content.isBlank() }
        val checkIsEmpty = checks.isEmpty()
        val labelsIsEmpty = labels.isEmpty()
        return titleIsBlank && detailIsBlank && emptyImage && voiceEmpty && checkIsEmpty && checksBlank && labelsIsEmpty
    }

    fun isImageOnly(): Boolean {
        val titleIsBlank = title.isBlank()
        val detailIsBlank = detail.isBlank()
        val emptyImage = images.isEmpty()
        val voiceEmpty = voices.isEmpty()
        val checksBlank = checks.all { it.content.isBlank() }
        val checkIsEmpty = checks.isEmpty()
        val labelsIsEmpty = labels.isEmpty()
        return titleIsBlank && detailIsBlank && !emptyImage && voiceEmpty && checkIsEmpty && checksBlank && labelsIsEmpty
    }
}

fun NotePad.toNotePadUiState(
    toPath: (Long) -> String,
) = NotePadUiState(
    id = id,
    title = title,
    detail = detail,
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
    title = title,
    detail = detail,
    editDate = editDate,
    isCheck = isCheck,
    color = color,
    background = background,
    isPin = isPin,
    reminder = reminder,
    interval = interval,
    noteType = noteType.type,
    images = images.map { it.toNoteImage() },
    voices = voices.map { it.toNoteVoice() },
    checks = checks.map { it.toNoteCheck() },
)
