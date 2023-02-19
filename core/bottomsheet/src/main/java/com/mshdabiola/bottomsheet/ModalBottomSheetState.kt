package com.mshdabiola.bottomsheet

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue

@ExperimentalMaterialApi
val ModalBottomSheetState.isAtLeastPartiallyVisible: Boolean
    get() = progress.from != ModalBottomSheetValue.Hidden ||
            progress.to != ModalBottomSheetValue.Hidden

@ExperimentalMaterialApi
val ModalBottomSheetState.isShownOrShowing: Boolean
    get() = targetValue != ModalBottomSheetValue.Hidden

@ExperimentalMaterialApi
val ModalBottomSheetState.isHiddenOrHiding: Boolean
    get() = targetValue == ModalBottomSheetValue.Hidden
