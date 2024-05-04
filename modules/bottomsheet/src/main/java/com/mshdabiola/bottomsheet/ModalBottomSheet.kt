package com.mshdabiola.bottomsheet

import android.app.Activity
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ModalBottomSheet(
    modalState: ModalState = rememberModalState().also {
        rememberCoroutineScope().launch { it.show() }
    },
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val contentView: ViewGroup = remember {
        val activity = context as Activity
        activity.findViewById(android.R.id.content) as ViewGroup
    }
    val coroutineScope = rememberCoroutineScope()
    val compositionContext = rememberCompositionContext()

    val internalState: ModalStateImpl
    val state = when (modalState) {
        is ModalStateImpl -> {
            internalState = modalState
            modalState._state
        }
    }

    val shouldBeVisible =
        modalState.isAtLeastPartiallyVisible || internalState.hasPendingShowRequests
    var lastComposeView: ComposeView? by remember { mutableStateOf(null) }
    val composeView = remember(shouldBeVisible, compositionContext) {
        lastComposeView?.let { contentView.removeView(it) }
        val newView: ComposeView? = when {
            shouldBeVisible -> ComposeView(contentView.context).also {
                it.setParentCompositionContext(compositionContext)
                contentView.addView(it)
            }

            else -> {
                null
            }
        }
        lastComposeView = newView
        newView
    }
    DisposableEffect(composeView, content as Any) {
        composeView?.setContent {
            ModalBottomSheetLayout(
                sheetBackgroundColor = Color.Transparent,
                sheetState = state,
                sheetContent = {
                    content()
                },
            ) {}
        }
        onDispose {}
    }
    DisposableEffect(Unit) {
        onDispose { composeView?.let { contentView.removeView(it) } }
    }
    BackHandler(enabled = state.isShownOrShowing) {
        coroutineScope.launch { modalState.hide() }
    }
}
