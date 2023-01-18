package com.mshdabiola.playnotepad.ui

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import com.mshdabiola.designsystem.theme.NotePadAppTheme
import com.mshdabiola.playnotepad.navigation.NotePadAppNavHost

@Composable
fun NotePadApp(
    windowSizeClass: WindowSizeClass,
    noteAppState: NotePadAppState = rememberNotePadAppState(windowSizeClass = windowSizeClass)
) {
    NotePadAppTheme {
        NotePadAppNavHost(
            navController = noteAppState.navHostController,
            navigateToEdit = noteAppState::navigateToEdit,
            navigateToLevel = noteAppState::navigateToLevel,
            navigateToSelectLevel = noteAppState::navigateToSelectLevel,
            navigateToMain = noteAppState::navigateToMain,
            navigateToSearch = noteAppState::navigateToSearch,
            onBack = noteAppState::onBack
        )
    }
}