/*
 *abiola 2023
 */

package com.mshdabiola.benchmarks.interests

import android.os.Build.VERSION_CODES
import androidx.annotation.RequiresApi
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.PowerCategory
import androidx.benchmark.macro.PowerCategoryDisplayLevel
import androidx.benchmark.macro.PowerMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import com.mshdabiola.benchmarks.PACKAGE_NAME
import com.mshdabiola.benchmarks.allowNotifications
import com.mshdabiola.benchmarks.foryou.forYouScrollFeedDownUp
import com.mshdabiola.benchmarks.foryou.forYouSelectTopics
import com.mshdabiola.benchmarks.foryou.forYouWaitForContent
import com.mshdabiola.benchmarks.foryou.setAppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalMetricApi::class)
@RequiresApi(VERSION_CODES.Q)
@RunWith(AndroidJUnit4::class)
class ScrollTopicListPowerMetricsBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    private val categories = PowerCategory.entries
        .associateWith { PowerCategoryDisplayLevel.TOTAL }

    @Test
    fun benchmarkStateChangeCompilationLight() =
        benchmarkStateChangeWithTheme(CompilationMode.Partial(), false)

    @Test
    fun benchmarkStateChangeCompilationDark() =
        benchmarkStateChangeWithTheme(CompilationMode.Partial(), true)

    private fun benchmarkStateChangeWithTheme(compilationMode: CompilationMode, isDark: Boolean) =
        benchmarkRule.measureRepeated(
            packageName = PACKAGE_NAME,
            metrics = listOf(FrameTimingMetric(), PowerMetric(PowerMetric.Energy(categories))),
            compilationMode = compilationMode,
            iterations = 2,
            startupMode = StartupMode.WARM,
            setupBlock = {
                // Start the app
                pressHome()
                startActivityAndWait()
                allowNotifications()
                // Navigate to Settings
                device.findObject(By.desc("Settings")).click()
                device.waitForIdle()
                setAppTheme(isDark)
            },
        ) {
            forYouWaitForContent()
            forYouSelectTopics()
            repeat(3) {
                forYouScrollFeedDownUp()
            }
        }
}
