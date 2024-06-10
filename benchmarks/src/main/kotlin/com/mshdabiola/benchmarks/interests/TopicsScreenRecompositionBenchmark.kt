/*
 *abiola 2022
 */

package com.mshdabiola.benchmarks.interests

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import com.mshdabiola.benchmarks.PACKAGE_NAME
import com.mshdabiola.benchmarks.startActivityAndAllowNotifications
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TopicsScreenRecompositionBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun benchmarkStateChangeCompilationBaselineProfile() =
        benchmarkStateChange(CompilationMode.Partial())

    private fun benchmarkStateChange(compilationMode: CompilationMode) =
        benchmarkRule.measureRepeated(
            packageName = PACKAGE_NAME,
            metrics = listOf(FrameTimingMetric()),
            compilationMode = compilationMode,
            iterations = 10,
            startupMode = StartupMode.WARM,
            setupBlock = {
                // Start the app
                pressHome()
                startActivityAndAllowNotifications()
                // Navigate to interests screen
                device.findObject(By.text("Interests")).click()
                device.waitForIdle()
            },
        ) {
            interestsWaitForTopics()
            repeat(3) {
                interestsToggleBookmarked()
            }
        }
}
