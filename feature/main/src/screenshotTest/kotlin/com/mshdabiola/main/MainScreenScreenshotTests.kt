/*
 *abiola 2023
 */

package com.mshdabiola.main

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.runtime.Composable
import com.mshdabiola.model.NoteDisplayCategory
import com.mshdabiola.model.getDefinedNotePads
import com.mshdabiola.testing.util.PreviewAllLocales
import com.mshdabiola.ui.PreviewContainer

@OptIn(ExperimentalSharedTransitionApi::class)
@PreviewAllLocales
@Composable
fun MainListScreenShot() {
    val list = getDefinedNotePads()
    val pin = list.take(3)
    val unPin = list.takeLast(7)

    PreviewContainer {
        MainScreen(
            mainState = MainState.Success(
                isGrid = true,
                labelName = "Label",
                pinNotePads = pin,
                unPinNotePads = unPin,
                noteDisplayCategory = NoteDisplayCategory(),
                selectState = null,
            ),
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@PreviewAllLocales
@Composable
fun MainEmptyScreenShot() {
    PreviewContainer {
        MainScreen(
            mainState = MainState.Success(
                isGrid = true,
                labelName = "Label",
                noteDisplayCategory = NoteDisplayCategory(),
                selectState = null,
            ),
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@PreviewAllLocales
@Composable
fun MainLoadingScreenShot() {
    PreviewContainer {
        MainScreen(
            mainState = MainState.Loading,
        )
    }
}
