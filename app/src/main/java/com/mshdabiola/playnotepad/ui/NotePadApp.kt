package com.mshdabiola.playnotepad.ui

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import com.mshdabiola.designsystem.theme.SkTheme
import com.mshdabiola.playnotepad.navigation.NotePadAppNavHost

@Composable
fun NotePadApp(
    windowSizeClass: WindowSizeClass,
    noteAppState: NotePadAppState = rememberNotePadAppState(windowSizeClass = windowSizeClass),
    saveImage: (Long, Long) -> Unit
) {
    SkTheme {
        NotePadAppNavHost(
            navController = noteAppState.navHostController,
            navigateToEdit = noteAppState::navigateToEdit,
            navigateToLevel = noteAppState::navigateToLevel,
            navigateToSearch = noteAppState::navigateToSearch,
            onBack = noteAppState::onBack,
            navigateToSelectLevel = noteAppState::navigateToSelectLevel,
            saveImage = saveImage
        )
    }
}
