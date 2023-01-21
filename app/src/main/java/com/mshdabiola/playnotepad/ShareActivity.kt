package com.mshdabiola.playnotepad

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.mshdabiola.designsystem.theme.NotePadAppTheme
import com.mshdabiola.editscreen.ActionEditScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShareActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_Translucent_NoTitleBar)
        setContent {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            NotePadAppTheme {
                ActionEditScreen(
                )
            }
        }

    }
}
