package com.mshdabiola.designsystem.component

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.mshdabiola.designsystem.component.state.Notify
import kotlinx.collections.immutable.ImmutableList


@Composable
fun NotifySnacker(snackHostState: SnackbarHostState, notifys: ImmutableList<Notify>) {
    LaunchedEffect(key1 = notifys, block = {
        if (notifys.isNotEmpty()) {
            val first = notifys.first()
            val result = snackHostState.showSnackbar(
                message = first.message,
                withDismissAction = first.withDismissAction,
                actionLabel = first.label,
                duration = if (first.isShort) SnackbarDuration.Short else SnackbarDuration.Long,
            )
            when (result) {
                SnackbarResult.ActionPerformed -> {
                }

                SnackbarResult.Dismissed -> {
                    first.callback()
                }
            }
        }
    })
}