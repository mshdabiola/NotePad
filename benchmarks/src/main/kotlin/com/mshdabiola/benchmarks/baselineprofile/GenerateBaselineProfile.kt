/*
 *abiola 2022
 */

package com.mshdabiola.benchmarks.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import com.mshdabiola.benchmarks.PACKAGE_NAME
import com.mshdabiola.benchmarks.detail.addNote
import com.mshdabiola.benchmarks.detail.goBack
import com.mshdabiola.benchmarks.main.goToDetailScreen
import com.mshdabiola.benchmarks.main.mainScrollNoteDownUp
import org.junit.Rule
import org.junit.Test

class GenerateBaselineProfile {
    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun generate() =
        baselineProfileRule.collect(PACKAGE_NAME) {
            pressHome()
            startActivityAndWait()

            device.waitForIdle()

            goToDetailScreen()
            addNote()
            goBack()

            mainScrollNoteDownUp()
        }
}
