/*
 *abiola 2023
 */

package com.mshdabiola.about

import androidx.compose.runtime.Composable
import com.mshdabiola.designsystem.theme.NotePadTheme
import com.mshdabiola.testing.util.PreviewAllLocales

@PreviewAllLocales
@Composable
private fun AboutScreenShot() {
    NotePadTheme() {
        AboutScreen(
            lastUpdate = "12-20-20025",
            version = "2.4.3",
        )
    }
}
