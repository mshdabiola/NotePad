package com.mshdabiola.xnotepad.ui

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import com.mshdabiola.designsystem.theme.NotePadAppTheme
import com.mshdabiola.xnotepad.navigation.NotePadAppNavHost

@Composable
fun NotePadApp(
    windowSizeClass: WindowSizeClass,
    noteAppState: NotePadAppState = rememberNotePadAppState(windowSizeClass = windowSizeClass)
) {
    NotePadAppTheme {
        NotePadAppNavHost(
            navController = noteAppState.navHostController,
            navigateToEdit = noteAppState::navigateToEdit,
            navigateToMain = noteAppState::navigateToMain,
            onBack = noteAppState::onBack
        )
    }
}