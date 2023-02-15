package com.mshdabiola.designsystem.component.state

data class DateListUiState(
    val title: String,
    val value: String = "",
    val trail: String? = null,
    val isOpenDialog: Boolean,
    val enable: Boolean
)
