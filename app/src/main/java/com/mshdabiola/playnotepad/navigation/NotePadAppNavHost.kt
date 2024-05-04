package com.mshdabiola.playnotepad.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.mshdabiola.about.aboutScreen
import com.mshdabiola.about.navigateToAbout
import com.mshdabiola.drawing.drawingScreen
import com.mshdabiola.drawing.navigateToDrawing
import com.mshdabiola.editscreen.editScreen
import com.mshdabiola.editscreen.navigateToEditScreenWIthPop
import com.mshdabiola.gallery.galleryScreen
import com.mshdabiola.gallery.navigateToGallery
import com.mshdabiola.labelscreen.labelScreen
import com.mshdabiola.mainscreen.mainNavigationRoute
import com.mshdabiola.mainscreen.mainScreen
import com.mshdabiola.searchscreen.searchScreen
import com.mshdabiola.selectlabelscreen.selectLabelScreen

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NotePadAppNavHost(
    navController: NavHostController,
    navigateToEdit: (Long, String, Long) -> Unit,
    navigateToLevel: (Boolean) -> Unit,
    navigateToSearch: () -> Unit,
    onBack: () -> Unit,
    startDestination: String = mainNavigationRoute,
    navigateToSelectLevel: (IntArray) -> Unit,
    saveImage: (Long, Long) -> Unit
) {
    NavHost(
        modifier = Modifier.semantics { testTagsAsResourceId = true },
        navController = navController,
        startDestination = startDestination,
    ) {
        mainScreen(
            navigateToEditScreen = navigateToEdit,
            navigateToLevel = navigateToLevel,
            navigateToSearch = navigateToSearch,
            navigateToSelectLevel = navigateToSelectLevel,
            navigateToAbout = { navController.navigateToAbout() },
        )
        editScreen(
            onBack = onBack,
            navigateToSelectLevel = navigateToSelectLevel,
            navigateToGallery = { id, index -> navController.navigateToGallery(id, index) },
            navigateToDrawing = { id, image -> navController.navigateToDrawing(id, image) },
        )
        labelScreen(onBack = onBack)
        selectLabelScreen(onBack)
        searchScreen(onBack, navigateToEdit)
        galleryScreen(onBack = onBack) { l, s, l2 ->
            navController.navigateToEditScreenWIthPop(
                l,
                s,
                l2,
            )
        }
        drawingScreen(onBack, saveImage)
        aboutScreen(onBack)
    }
}
