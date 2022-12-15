package com.mshdabiola.xnotepad.ui

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

data class NotePadAppState(
    val windowSizeClass: WindowSizeClass,
    val navHostController: NavHostController
)

@Composable
fun rememberNotePadAppState(
    windowSizeClass: WindowSizeClass,
    navHostController: NavHostController = rememberNavController()
): NotePadAppState {
    return remember(key1 = windowSizeClass) {
        NotePadAppState(windowSizeClass, navHostController)
    }
}