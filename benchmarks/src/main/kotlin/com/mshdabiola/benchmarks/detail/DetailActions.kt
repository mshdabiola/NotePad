/*
 *abiola 2024
 */

package com.mshdabiola.benchmarks.detail

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until

fun MacrobenchmarkScope.goBack() {
    val selector = By.desc("back")

    device.wait(Until.hasObject(selector), 5000)

    val backButton = device.findObject(selector)
    backButton.click()
    device.waitForIdle()
    // Wait until saved title are shown on screen
}

fun MacrobenchmarkScope.addNote() {
    val titleSelector = By.res("detail:title")
    val contentSelector = By.res("detail:content")

    device.wait(Until.hasObject(titleSelector), 5000)

    val titleTextField = device.findObject(titleSelector)
    val contentTextField = device.findObject(contentSelector)

    titleTextField.text = "title"
    contentTextField.text = "content"

    // Wait until saved title are shown on screen
}
