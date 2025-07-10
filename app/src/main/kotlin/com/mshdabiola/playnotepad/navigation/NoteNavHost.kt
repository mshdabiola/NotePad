/*
 *abiola 2022
 */

package com.mshdabiola.playnotepad.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.mshdabiola.about.navigation.aboutScreen
import com.mshdabiola.detail.navigation.DetailArg
import com.mshdabiola.detail.navigation.detailScreen
import com.mshdabiola.detail.navigation.navigateToDetail
import com.mshdabiola.drawing.navigation.DrawingArgs
import com.mshdabiola.drawing.navigation.drawingScreen
import com.mshdabiola.drawing.navigation.navigateToDrawing
import com.mshdabiola.gallery.navigation.GalleryArg
import com.mshdabiola.gallery.navigation.gallery
import com.mshdabiola.gallery.navigation.navigateToGallery
import com.mshdabiola.label.navigation.label
import com.mshdabiola.main.navigation.main
import com.mshdabiola.playnotepad.ui.NoteAppState
import com.mshdabiola.playnotepad.ui.pop
import com.mshdabiola.search.navigation.navigateToSearch
import com.mshdabiola.search.navigation.search
import com.mshdabiola.selectlabel.navigation.navigateToSelectLabel
import com.mshdabiola.selectlabel.navigation.selectLabelScreen
import com.mshdabiola.setting.navigation.settingScreen
import com.mshdabiola.ui.LocalSharedStScope

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun NoteNavHost2(
    appState: NoteAppState,
    modifier: Modifier = Modifier,
) {
    val navController = appState.navController

    SharedTransitionLayout(modifier = modifier) {
        CompositionLocalProvider(
            LocalSharedStScope provides this,
        ) {
            NavDisplay(
                backStack = navController,
                entryProvider = entryProvider {
                    main(
                        modifier = Modifier,
                        navigateToDetail = { id, colorIndex, background -> navController.navigateToDetail(DetailArg(id, colorIndex, background)) },
                        navigateToSelectLevel = appState.navController::navigateToSelectLabel,
                        onOpenDrawer = {
                            appState.openDrawer()
                        },
                        navigateToSearch = navController::navigateToSearch,
                    )
                    detailScreen(
                        modifier = Modifier,
                        onBack = navController::pop,
                        navigateToGallery = { id, index, total, currentPath ->
                            navController.navigateToGallery(
                                GalleryArg(id, index, total, currentPath),
                            )
                        },
                        navigateToDrawing = { noteId, image ->

                            navController.navigateToDrawing(
                                DrawingArgs(
                                    noteId,
                                    image,
                                ),
                            )
                        },
                        navigateToSelectLevel = navController::navigateToSelectLabel,
                    )
                    gallery(
                        onBack = navController::pop,
                    )
                    aboutScreen(onBack = navController::pop)
                    label(onBack = navController::pop)
                    selectLabelScreen(onBack = navController::pop)
                    drawingScreen(onBack = navController::pop)
                    settingScreen(
                        modifier = Modifier,
                        onBack = navController::pop,
                    )
                    search(
                        modifier = Modifier,
                        onBack = navController::pop,
                        navigateToDetail = { id, colorIndex, background ->
                            navController.navigateToDetail(
                                DetailArg(id, colorIndex, background),
                            )
                        },

                    )
                },
                entryDecorators = listOf(
                    rememberSavedStateNavEntryDecorator(),

                    rememberViewModelStoreNavEntryDecorator(),
                ),
            )
        }
    }
}
