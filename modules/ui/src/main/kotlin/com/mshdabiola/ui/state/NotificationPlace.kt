package com.mshdabiola.ui.state

import androidx.compose.foundation.text.input.TextFieldState

sealed class NotificationPlace {
    data class Edit(val place: TextFieldState) : NotificationPlace()
    data object Home : NotificationPlace()
    data object Work : NotificationPlace()
    data object School : NotificationPlace()
}
