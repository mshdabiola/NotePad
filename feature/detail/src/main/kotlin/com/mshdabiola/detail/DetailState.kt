/*
 *abiola 2024
 */

package com.mshdabiola.detail

import androidx.compose.foundation.text.input.TextFieldState
import com.mshdabiola.model.NotePad

data class DetailState(
    val notePad: NotePad = NotePad(),
    val title: TextFieldState = TextFieldState(),
    val detail: TextFieldState = TextFieldState(),

)
