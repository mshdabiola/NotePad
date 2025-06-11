/*
 *abiola 2022
 */

package com.mshdabiola.designsystem.component

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NoteLoadingWheel(
    contentDesc: String,
    modifier: Modifier = Modifier,
) {
    LoadingIndicator(modifier = modifier)
}

private const val ROTATION_TIME = 12000
private const val NUM_OF_LINES = 12
