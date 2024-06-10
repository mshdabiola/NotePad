/*
 *abiola 2022
 */

package com.mshdabiola.benchmarks.interests

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import com.mshdabiola.benchmarks.flingElementDownUp

fun MacrobenchmarkScope.goToInterestsScreen() {
    device.findObject(By.text("Interests")).click()
    device.waitForIdle()
    // Wait until interests are shown on screen

    // Wait until content is loaded by checking if interests are loaded
    device.wait(Until.gone(By.res("loadingWheel")), 5_000)
}

fun MacrobenchmarkScope.interestsScrollTopicsDownUp() {
    device.wait(Until.hasObject(By.res("interests:topics")), 5_000)
    val topicsList = device.findObject(By.res("interests:topics"))
    device.flingElementDownUp(topicsList)
}

fun MacrobenchmarkScope.interestsWaitForTopics() {
    device.wait(Until.hasObject(By.text("Accessibility")), 30_000)
}

fun MacrobenchmarkScope.interestsToggleBookmarked() {
    val topicsList = device.findObject(By.res("interests:topics"))
    val checkable = topicsList.findObject(By.checkable(true))
    checkable.click()
    device.waitForIdle()
}
