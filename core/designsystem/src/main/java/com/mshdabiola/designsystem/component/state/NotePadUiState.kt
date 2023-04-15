package com.mshdabiola.designsystem.component.state

import com.mshdabiola.model.Label
import com.mshdabiola.model.NotePad
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

data class NotePadUiState(
    val note: NoteUiState = NoteUiState(),
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
            "${note.title} \n $checkString"
        } else {
            "${note.title} \n ${note.detail}"
        }
    }

    fun isEmpty(): Boolean {
        val titleIsBlank = note.title.isBlank()
        val detailIsBlank = note.detail.isBlank()
        val emptyImage = images.isEmpty()
        val voiceEmpty = voices.isEmpty()
        val checksBlank = checks.all { it.content.isBlank() }
        val checkIsEmpty = checks.isEmpty()
        val labelsIsEmpty = labels.isEmpty()
        return titleIsBlank && detailIsBlank && emptyImage && voiceEmpty && checkIsEmpty && checksBlank && labelsIsEmpty
    }

    fun isImageOnly(): Boolean {
        val titleIsBlank = note.title.isBlank()
        val detailIsBlank = note.detail.isBlank()
        val emptyImage = images.isEmpty()
        val voiceEmpty = voices.isEmpty()
        val checksBlank = checks.all { it.content.isBlank() }
        val checkIsEmpty = checks.isEmpty()
        val labelsIsEmpty = labels.isEmpty()
        return titleIsBlank && detailIsBlank && !emptyImage && voiceEmpty && checkIsEmpty && checksBlank && labelsIsEmpty
    }
}

fun NotePad.toNotePadUiState(list: List<Label> = emptyList(),
                             getTime:(Long)->String,
                             toPath:(Long)->String
                             ) = NotePadUiState(
    note = note.toNoteUiState(getDate=getTime),
    images = images.map { it.toNoteImageUiState(toPath) }.toImmutableList(),
    voices = voices.map { it.toNoteVoiceUiState() }.toImmutableList(),
    checks = checks.map { it.toNoteCheckUiState() }.toImmutableList(),
    labels = labels.map { s -> list.singleOrNull { it.id == s.labelId }?.label ?: "" }
        .toImmutableList()
)

fun NotePadUiState.toNotePad() = NotePad(
    note = note.toNote(),
    images = images.map { it.toNoteImage() },
    voices = voices.map { it.toNoteVoice() },
    checks = checks.map { it.toNoteCheck() },
)
