package com.mshdabiola.bottomsheet

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun rememberModalState(): ModalState {
    val bottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    return remember(bottomSheetState) { ModalStateImpl(bottomSheetState) }
}

@Stable
sealed interface ModalState {
    val isVisible: Boolean
    val isAtLeastPartiallyVisible: Boolean
    val isShownOrShowing: Boolean
    val isHiddenOrHiding: Boolean
    suspend fun hide()
    suspend fun show()
}

@Stable
@ExperimentalMaterialApi
internal class ModalStateImpl(
    val _state: ModalBottomSheetState
) : ModalState {
    override val isVisible: Boolean get() = _state.isVisible
    override val isAtLeastPartiallyVisible: Boolean get() = _state.isAtLeastPartiallyVisible
    override val isShownOrShowing: Boolean get() = _state.isShownOrShowing
    override val isHiddenOrHiding: Boolean get() = _state.isHiddenOrHiding

    override suspend fun hide() = _state.hide()
    override suspend fun show() {
        try {
            showRequestsPendingCount++
            _state.show()
        } finally {
            showRequestsPendingCount--
        }
    }

    override fun toString(): String = buildString {
        appendLine("isVisible: " + _state.isVisible.toString())
        appendLine("currentValue: " + _state.currentValue.toString())
        appendLine("targetValue: " + _state.targetValue.toString())
        appendLine("isAnimationRunning: " + _state.isAnimationRunning.toString())
    }

    private var showRequestsPendingCount by mutableStateOf(0)
    val hasPendingShowRequests by derivedStateOf { showRequestsPendingCount > 0 }
}