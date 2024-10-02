/*
 *abiola 2022
 */

package com.mshdabiola.playnotepad.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.mshdabiola.about.aboutScreen
import com.mshdabiola.detail.navigation.DetailArg
import com.mshdabiola.detail.navigation.detailScreen
import com.mshdabiola.detail.navigation.navigateToDetail
import com.mshdabiola.drawing.drawingScreen
import com.mshdabiola.drawing.navigateToDrawing
import com.mshdabiola.gallery.GalleryArg
import com.mshdabiola.gallery.galleryScreen
import com.mshdabiola.gallery.navigateToGallery
import com.mshdabiola.labelscreen.labelScreen
import com.mshdabiola.main.navigation.FullMainRoute
import com.mshdabiola.main.navigation.mainScreen
import com.mshdabiola.playnotepad.ui.NoteAppState
import com.mshdabiola.selectlabelscreen.navigateToSelectLabel
import com.mshdabiola.selectlabelscreen.selectLabelScreen

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun NoteNavHost(
    appState: NoteAppState,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier,
) {
    val navController = appState.navController
    SharedTransitionLayout(modifier = modifier) {
        NavHost(
            navController = navController,
            startDestination = FullMainRoute,
            modifier = Modifier,
        ) {
            mainScreen(
                modifier = Modifier,
                sharedTransitionScope = this@SharedTransitionLayout,
                onShowSnack = onShowSnackbar,
                navigateToDetail = { navController.navigateToDetail(DetailArg(it)) },
                navigateToSelectLevel = appState.navController::navigateToSelectLabel,
                onOpenDrawer = {
                    appState.openDrawer()
                },
            )
            detailScreen(
                //  modifier = Modifier,
                //  sharedTransitionScope = this@SharedTransitionLayout,
                onShowSnackbar = onShowSnackbar,
                onBack = navController::popBackStack,
                navigateToGallery = { navController.navigateToGallery(GalleryArg(it)) },
                navigateToDrawing = navController::navigateToDrawing,
                navigateToSelectLevel = appState.navController::navigateToSelectLabel,
            )
            galleryScreen(
                onBack = navController::popBackStack,
            )
            aboutScreen(onBack = navController::popBackStack)
            labelScreen(onBack = navController::popBackStack)
            selectLabelScreen(onBack = navController::popBackStack)
            drawingScreen(onBack = navController::popBackStack)
        }
    }
}

//
// @OptIn(ExperimentalComposeUiApi::class)
// @Composable
// fun NotePadAppNavHost(
//    navController: NavHostController,
//    navigateToEdit: (Long, String, Long) -> Unit,
//    navigateToLevel: (Boolean) -> Unit,
//    navigateToSearch: () -> Unit,
//    onBack: () -> Unit,
//    startDestination: String = mainNavigationRoute,
//    navigateToSelectLevel: (IntArray) -> Unit,
//    saveImage: (Long, Long) -> Unit,
// ) {
//    NavHost(
//        modifier = Modifier.semantics { testTagsAsResourceId = true },
//        navController = navController,
//        startDestination = startDestination,
//    ) {
//        mainScreen(
//            navigateToEditScreen = navigateToEdit,
//            navigateToLevel = navigateToLevel,
//            navigateToSearch = navigateToSearch,
//            navigateToSelectLevel = navigateToSelectLevel,
//            navigateToAbout = { navController.navigateToAbout() },
//        )
//        editScreen(
//            onBack = onBack,
//            navigateToSelectLevel = navigateToSelectLevel,
//            navigateToGallery = { id, index -> navController.navigateToGallery(id, index) },
//            navigateToDrawing = { id, image -> navController.navigateToDrawing(id, image) },
//        )
//        labelScreen(onBack = onBack)
//        selectLabelScreen(onBack)
//        searchScreen(onBack, navigateToEdit)
//        galleryScreen(onBack = onBack) { l, s, l2 ->
//            navController.navigateToEditScreenWIthPop(
//                l,
//                s,
//                l2,
//            )
//        }
//        drawingScreen(onBack, saveImage)
//        aboutScreen(onBack)
//    }
// }
