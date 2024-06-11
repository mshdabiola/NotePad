/*
 *abiola 2022
 */

package com.mshdabiola.benchmarks.foryou

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import com.mshdabiola.benchmarks.flingElementDownUp
import org.junit.Assert.fail

fun MacrobenchmarkScope.forYouWaitForContent() {
    // Wait until content is loaded by checking if topics are loaded
    //  device.wait(Until.gone(By.res("loadingWheel")), 5_000)
    // Sometimes, the loading wheel is gone, but the content is not loaded yet
    // So we'll wait here for topics to be sure
    //   val obj = device.waitAndFindObject(By.res("forYou:topicSelection"), 10_000)
    // Timeout here is quite big, because sometimes data loading takes a long time!
    //   obj.wait(untilHasChildren(), 60_000)
}

/**
 * Selects some topics, which will show the feed content for them.
 * [recheckTopicsIfChecked] Topics may be already checked from the previous iteration.
 */
fun MacrobenchmarkScope.forYouSelectTopics(recheckTopicsIfChecked: Boolean = false) {
    val topics = device.findObject(By.res("forYou:topicSelection"))

    // Set gesture margin from sides not to trigger system gesture navigation
    val horizontalMargin = 10 * topics.visibleBounds.width() / 100
    topics.setGestureMargins(horizontalMargin, 0, horizontalMargin, 0)

    // Select some topics to show some feed content
    var index = 0
    var visited = 0

    while (visited < 3) {
        if (topics.childCount == 0) {
            fail("No topics found, can't generate profile for ForYou page.")
        }
        // Selecting some topics, which will populate items in the feed.
        val topic = topics.children[index % topics.childCount]
        // Find the checkable element to figure out whether it's checked or not
        val topicCheckIcon = topic.findObject(By.checkable(true))
        // Topic icon may not be visible if it's out of the screen boundaries
        // If that's the case, let's try another index
        if (topicCheckIcon == null) {
            index++
            continue
        }

        when {
            // Topic wasn't checked, so just do that
            !topicCheckIcon.isChecked -> {
                topic.click()
                device.waitForIdle()
            }

            // Topic was checked already and we want to recheck it, so just do it twice
            recheckTopicsIfChecked -> {
                repeat(2) {
                    topic.click()
                    device.waitForIdle()
                }
            }

            else -> {
                // Topic is checked, but we don't recheck it
            }
        }

        index++
        visited++
    }
}

fun MacrobenchmarkScope.forYouScrollFeedDownUp() {
    val feedList = device.findObject(By.res("forYou:feed"))
    device.flingElementDownUp(feedList)
}

fun MacrobenchmarkScope.setAppTheme(isDark: Boolean) {
    when (isDark) {
        true -> device.findObject(By.text("Dark")).click()
        false -> device.findObject(By.text("Light")).click()
    }
    device.waitForIdle()
    device.findObject(By.text("OK")).click()
}
