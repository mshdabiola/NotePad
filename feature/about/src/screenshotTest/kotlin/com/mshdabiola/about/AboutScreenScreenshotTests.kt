/*
 *abiola 2023
 */

package com.mshdabiola.about

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun AboutScreenShot() {
//    NotePadTheme() {
    AboutScreen(
        lastUpdate = "12-20-20025",
        version = "2.4.3",
    )
//    }
}
