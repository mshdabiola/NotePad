/*
 *abiola 2022
 */

package com.mshdabiola.benchmarks.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import com.mshdabiola.benchmarks.PACKAGE_NAME
import org.junit.Rule
import org.junit.Test

class GenerateBaselineProfile {
    @get:Rule val baselineProfileRule = BaselineProfileRule()

    @Test
    fun generate() =
        baselineProfileRule.collect(PACKAGE_NAME) {
            pressHome()
            startActivityAndWait()

            device.waitForIdle()
            val lazy = device.findObject(By.res("main:lazy"))

            lazy.fling(Direction.DOWN)
            lazy.fling(Direction.UP)
            device.waitForIdle()
            lazy.children[0].click()
            device.waitForIdle()

//        device.findObject(By.res("note image")).click()
//        device.waitForIdle()
//        device.findObject(By.descContains("back")).click()
            device.waitForIdle()
            device.findObject(By.descContains("back")).click()
//
//        device.waitForIdle()
//        device.findObject(UiSelector().text("Cancel")).click()
//
//        device.waitForIdle()
//        device.findObject(UiSelector().text("Play")).click()
//
//        device.waitForIdle()
//        device.findObject(UiSelector().descriptionContains("menu")).click()
//        device.findObject(UiSelector().text("Home")).click()
//
//        device.waitForIdle()
//        device.findObject(UiSelector().descriptionContains("Setting")).click()
//
//        device.waitForIdle()
//        device.findObject(UiSelector().text("Player name")).swipeDown(2)
//        device.findObject(UiSelector().text("Player name")).swipeUp(2)
//
//        device.findObject(UiSelector().descriptionContains("close")).click()
//        device.waitForIdle()
//        device.findObject(UiSelector().descriptionContains("close")).click()
//         while(!device.hasObject(By.text("6"))){
//            device.findObject(UiSelector().descriptionContains("dice")).click()
//            device.waitForIdle()
//         }
//        device.findObject(UiSelector().text("6")).click()
        }
}
