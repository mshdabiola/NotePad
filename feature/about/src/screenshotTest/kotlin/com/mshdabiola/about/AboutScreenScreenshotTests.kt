/*
 *abiola 2023
 */

package com.mshdabiola.about

import androidx.compose.runtime.Composable
import com.mshdabiola.ui.PreviewContainer
import com.mshdabiola.ui.PreviewMain

class AboutScreenScreenshotTests {

    @PreviewMain
    @Composable
    private fun Main() {
        PreviewContainer {
            AboutScreen(
                lastUpdate = "12-20-20025",
                version = "2.4.3",
            )
        }
    }
}
