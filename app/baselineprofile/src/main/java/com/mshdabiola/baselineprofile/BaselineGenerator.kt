package com.mshdabiola.baselineprofile

// import androidx.benchmark.macro.ExperimentalBaselineProfilesApi

//
// // @OptIn(ExperimentalBaselineProfilesApi::class)
// class BaselineGenerator {
//
//    @get:Rule
//    val baselineProfileRule = BaselineProfileRule()
//
//    @Test
//    fun startUp() = baselineProfileRule.collectBaselineProfile(
//        packageName = "com.mshdabiola.playnotepad",
//    ) {
//        pressHome()
//        startActivityAndWait()
//
//        device.waitForIdle()
//        val lazy = device.findObject(By.res("main:lazy"))
//
//        lazy.fling(Direction.DOWN)
//        lazy.fling(Direction.UP)
//        device.waitForIdle()
//        lazy.children[0].click()
//        device.waitForIdle()
//
// //        device.findObject(By.res("note image")).click()
// //        device.waitForIdle()
// //        device.findObject(By.descContains("back")).click()
//        device.waitForIdle()
//        device.findObject(By.descContains("back")).click()
// //
// //        device.waitForIdle()
// //        device.findObject(UiSelector().text("Cancel")).click()
// //
// //        device.waitForIdle()
// //        device.findObject(UiSelector().text("Play")).click()
// //
// //        device.waitForIdle()
// //        device.findObject(UiSelector().descriptionContains("menu")).click()
// //        device.findObject(UiSelector().text("Home")).click()
// //
// //        device.waitForIdle()
// //        device.findObject(UiSelector().descriptionContains("Setting")).click()
// //
// //        device.waitForIdle()
// //        device.findObject(UiSelector().text("Player name")).swipeDown(2)
// //        device.findObject(UiSelector().text("Player name")).swipeUp(2)
// //
// //        device.findObject(UiSelector().descriptionContains("close")).click()
// //        device.waitForIdle()
// //        device.findObject(UiSelector().descriptionContains("close")).click()
// //         while(!device.hasObject(By.text("6"))){
// //            device.findObject(UiSelector().descriptionContains("dice")).click()
// //            device.waitForIdle()
// //         }
// //        device.findObject(UiSelector().text("6")).click()
//    }
// }
