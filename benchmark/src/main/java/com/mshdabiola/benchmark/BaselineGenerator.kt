package com.mshdabiola.benchmark

// import androidx.benchmark.macro.ExperimentalBaselineProfilesApi
import androidx.benchmark.macro.junit4.BaselineProfileRule
import org.junit.Rule
import org.junit.Test

// @OptIn(ExperimentalBaselineProfilesApi::class)
class BaselineGenerator {

    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun startUp() = baselineProfileRule.collectBaselineProfile(
        packageName = "com.mshdabiola.playnotepad"
    ) {
        pressHome()
        startActivityAndWait()

//        device.waitForIdle()
//        device.findObject(UiSelector().descriptionContains("play")).click()
//
//        device.waitForIdle()
//        device.findObject(UiSelector().text("Host")).click()
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
