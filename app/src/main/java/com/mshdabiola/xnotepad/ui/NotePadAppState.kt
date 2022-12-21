package com.mshdabiola.xnotepad.ui

import android.net.Uri
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.mshdabiola.editscreen.navigateToEditScreen
import com.mshdabiola.mainscreen.navigateToMain

data class NotePadAppState(
    val windowSizeClass: WindowSizeClass,
    val navHostController: NavHostController
) {
    fun navigateToMain() {
        navHostController.navigateToMain()
    }

    fun navigateToEdit(
        id: Long,
        content: String,
        uri: Uri
    ) {
        navHostController.navigateToEditScreen(id, content, uri)
    }

    fun onBack() {
        navHostController.popBackStack()
    }
}

@Composable
fun rememberNotePadAppState(
    windowSizeClass: WindowSizeClass,
    navHostController: NavHostController = rememberNavController()
): NotePadAppState {
    return remember(key1 = windowSizeClass) {
        NotePadAppState(windowSizeClass, navHostController)
    }
}