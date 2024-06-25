/*
 *abiola 2024
 */

package com.mshdabiola.benchmarks.main

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import com.mshdabiola.benchmarks.flingElementDownUp

fun MacrobenchmarkScope.goToDetailScreen() {
    val savedSelector = By.res("main:add")

    device.wait(Until.hasObject(savedSelector), 5000)

    val addButton = device.findObject(savedSelector)
    addButton.click()
    device.waitForIdle()
    // Wait until saved title are shown on screen
}

fun MacrobenchmarkScope.mainScrollNoteDownUp() {
    val selector = By.res("main:list")
    device.wait(Until.hasObject(selector), 5000)

    val feedList = device.findObject(selector)
    device.flingElementDownUp(feedList)
}

fun MacrobenchmarkScope.mainWaitForContent() {
    // Wait until content is loaded by checking if topics are loaded
    //  device.wait(Until.gone(By.res("loadingWheel")), 5_000)
    // Sometimes, the loading wheel is gone, but the content is not loaded yet
    // So we'll wait here for topics to be sure
    //   val obj = device.waitAndFindObject(By.res("forYou:topicSelection"), 10_000)
    // Timeout here is quite big, because sometimes data loading takes a long time!
    //   obj.wait(untilHasChildren(), 60_000)
}
