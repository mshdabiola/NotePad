package com.mshdabiola.xnotepad

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.mshdabiola.designsystem.theme.NotePadAppTheme
import com.mshdabiola.xnotepad.ui.NotePadApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            NotePadAppTheme {
                // A surface container using the 'background' color from the theme
                NotePadApp(windowSizeClass = calculateWindowSizeClass(activity = this))
            }
        }
    }
}
