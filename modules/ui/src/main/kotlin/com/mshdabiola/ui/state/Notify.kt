package com.mshdabiola.ui.state

data class Notify(
    val message: String = "",
    val label: String? = null,
    val withDismissAction: Boolean = false,
    val isShort: Boolean = true,
    val callback: () -> Unit = {},
)
