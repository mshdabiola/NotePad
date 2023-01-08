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
    val labels: ImmutableList<String> = emptyList<String>().toImmutableList()
) {
    override fun toString(): String {
        val checkString = checks.joinToString(separator = "\n") {
            if (it.isCheck)
                "[*] ${it.content}"
            else
                "[ ] ${it.content}"
        }
        return if (checkString.isNotBlank())
            "${note.title} \n $checkString"
        else
            "${note.title} \n ${note.detail}"
    }
}


fun NotePad.toNotePadUiState(list: List<Label> = emptyList()) = NotePadUiState(
    note = note.toNoteUiState(),
    images = images.map { it.toNoteImageUiState() }.toImmutableList(),
    voices = voices.map { it.toNoteVoiceUiState() }.toImmutableList(),
    checks = checks.map { it.toNoteCheckUiState() }.toImmutableList(),
    labels = labels.map { s -> list.singleOrNull { it.id == s.labelId }?.label ?: "" }
        .toImmutableList()
)

fun NotePadUiState.toNotePad() = NotePad(
    note = note.toNote(),
    images = images.map { it.toNoteImage() },
    voices = voices.map { it.toNoteVoice() },
    checks = checks.map { it.toNoteCheck() }
)
