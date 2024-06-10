/*
 *abiola 2022
 */

package com.mshdabiola.benchmarks.bookmarks

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By

fun MacrobenchmarkScope.goToBookmarksScreen() {
    val savedSelector = By.text("Saved")
    val savedButton = device.findObject(savedSelector)
    savedButton.click()
    device.waitForIdle()
    // Wait until saved title are shown on screen
}
