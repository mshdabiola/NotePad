/*
 *abiola 2023
 */

package com.mshdabiola.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.mshdabiola.common.result.Result
import com.mshdabiola.testing.util.Capture

@Preview
@Composable
private fun MainScreenShot() {
    Capture {
        MainScreen(
            mainState = Result.Loading,

        )
    }
}
