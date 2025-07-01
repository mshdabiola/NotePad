/*
 *abiola 2023
 */

package com.mshdabiola.testing.util

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mshdabiola.designsystem.theme.NotePadTheme

/**
 * Takes six screenshots combining light/dark and default/Android themes and whether dynamic color
 * is enabled.
 */
@Composable
fun CaptureMultiTheme(
    shouldCompareDarkMode: Boolean = true,
    shouldCompareDynamicColor: Boolean = true,
    shouldCompareAndroidTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    val darkModeValues = if (shouldCompareDarkMode) listOf(true, false) else listOf(false)
    val dynamicThemingValues = if (shouldCompareDynamicColor) listOf(true, false) else listOf(false)
    val androidThemeValues = if (shouldCompareAndroidTheme) listOf(true, false) else listOf(false)

    var darkMode by remember { mutableStateOf(true) }
    var dynamicTheming by remember { mutableStateOf(false) }
    var androidTheme by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        darkModeValues.forEach { isDarkMode ->
            darkMode = isDarkMode

            androidThemeValues.forEach { isAndroidTheme ->
                androidTheme = isAndroidTheme

                dynamicThemingValues.forEach dynamicTheme@{ isDynamicTheming ->
                    // Skip tests with both Android Theme and Dynamic color as they're incompatible.
                    if (isAndroidTheme && isDynamicTheming) return@dynamicTheme

                    dynamicTheming = isDynamicTheming

                    val description = generateDescription(
                        shouldCompareDarkMode = shouldCompareDarkMode,
                        darkMode = darkMode,
                        shouldCompareAndroidTheme = shouldCompareAndroidTheme,
                        androidTheme = androidTheme,
                        shouldCompareDynamicColor = shouldCompareDynamicColor,
                        dynamicTheming = dynamicTheming,
                    )

                    Capture(
                        darkMode = darkMode,
                        androidTheme = androidTheme,
                        dynamicTheming = dynamicTheming,
                        description = description,
                        content = content,
                    )
                }
            }
        }
    }
}

@Composable
fun Capture(
    darkMode: Boolean = false,
    androidTheme: Boolean = false,
    dynamicTheming: Boolean = false,
    description: String = "",
    content: @Composable () -> Unit,
) {
    NotePadTheme(
        androidTheme = androidTheme,
        darkTheme = darkMode,
        disableDynamicTheming = !dynamicTheming,
    ) {
        Surface(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(description)
                Spacer(modifier = Modifier.height(8.dp))

                content()
            }
        }
    }
}

@Composable
private fun generateDescription(
    shouldCompareDarkMode: Boolean,
    darkMode: Boolean,
    shouldCompareAndroidTheme: Boolean,
    androidTheme: Boolean,
    shouldCompareDynamicColor: Boolean,
    dynamicTheming: Boolean,
): String {
    val description = "" +
        if (shouldCompareDarkMode) {
            if (darkMode) "Dark" else "Light"
        } else {
            ""
        } +
        if (shouldCompareAndroidTheme) {
            if (androidTheme) " Android" else " Default"
        } else {
            ""
        } +
        if (shouldCompareDynamicColor) {
            if (dynamicTheming) " Dynamic" else ""
        } else {
            ""
        }

    return description.trim()
}

/**
 * Extracts some properties from the spec string. Note that this function is not exhaustive.
 */
private fun extractSpecs(deviceSpec: String): TestDeviceSpecs {
    val specs = deviceSpec.substringAfter("spec:")
        .split(",").map { it.split("=") }.associate { it[0] to it[1] }
    val width = specs["width"]?.toInt() ?: 640
    val height = specs["height"]?.toInt() ?: 480
    val dpi = specs["dpi"]?.toInt() ?: 480
    return TestDeviceSpecs(width, height, dpi)
}

data class TestDeviceSpecs(val width: Int, val height: Int, val dpi: Int)




// 1. Define Custom Annotations for Screen Sizes and Themes

// Small screen previews
@Preview(name = "Small Screen Day", device = "id:pixel_4a", widthDp = 360, heightDp = 640, uiMode = Configuration.UI_MODE_NIGHT_NO, group = "Screen Sizes")
@Preview(name = "Small Screen Night", device = "id:pixel_4a", widthDp = 360, heightDp = 640, uiMode = Configuration.UI_MODE_NIGHT_YES, group = "Screen Sizes")
annotation class PreviewSmallScreen

// Medium screen previews (e.g., Pixel 4 XL)
@Preview(name = "Medium Screen Day", device = "id:pixel_4_xl", widthDp = 411, heightDp = 891, uiMode = Configuration.UI_MODE_NIGHT_NO, group = "Screen Sizes")
@Preview(name = "Medium Screen Night", device = "id:pixel_4_xl", widthDp = 411, heightDp = 891, uiMode = Configuration.UI_MODE_NIGHT_YES, group = "Screen Sizes")
annotation class PreviewMediumScreen

// Large screen previews (e.g., Foldable or Tablet in portrait)
@Preview(name = "Large Screen Day", device = "id:pixel_c", widthDp = 800, heightDp = 1280, uiMode = Configuration.UI_MODE_NIGHT_NO, group = "Screen Sizes")
@Preview(name = "Large Screen Night", device = "id:pixel_c", widthDp = 800, heightDp = 1280, uiMode = Configuration.UI_MODE_NIGHT_YES, group = "Screen Sizes")
annotation class PreviewLargeScreen

@Preview(name = "Small Screen Day", device = "id:pixel_4a", widthDp = 360, heightDp = 640, uiMode = Configuration.UI_MODE_NIGHT_NO, group = "All Screens")
@Preview(name = "Small Screen Night", device = "id:pixel_4a", widthDp = 360, heightDp = 640,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL, group = "All Screens",
    showSystemUi = false,
    showBackground = false
)
@Preview(name = "Medium Screen Day", device = "id:pixel_4_xl", widthDp = 411, heightDp = 891, uiMode = Configuration.UI_MODE_NIGHT_NO, group = "All Screens")
@Preview(name = "Medium Screen Night", device = "id:pixel_4_xl", widthDp = 411, heightDp = 891, uiMode = Configuration.UI_MODE_NIGHT_YES, group = "All Screens")
@Preview(name = "Large Screen Day", device = "id:pixel_c", widthDp = 800, heightDp = 1280, uiMode = Configuration.UI_MODE_NIGHT_NO, group = "All Screens")
@Preview(name = "Large Screen Night", device = "id:pixel_c", widthDp = 800, heightDp = 1280, uiMode = Configuration.UI_MODE_NIGHT_YES, group = "All Screens")
annotation class PreviewAllScreenSizes
// 2. Define Custom Annotations for Locales

// English Locale
@Preview(name = "English Day", locale = "en", uiMode = Configuration.UI_MODE_NIGHT_NO, group = "Locales")
@Preview(name = "English Night", locale = "en", uiMode = Configuration.UI_MODE_NIGHT_YES, group = "Locales")
annotation class PreviewEnglish

// French Locale
@Preview(name = "French Day", locale = "fr", uiMode = Configuration.UI_MODE_NIGHT_NO, group = "Locales")
@Preview(name = "French Night", locale = "fr", uiMode = Configuration.UI_MODE_NIGHT_YES, group = "Locales")
annotation class PreviewFrench

// Russian Locale
@Preview(name = "Russian Day", locale = "ru", uiMode = Configuration.UI_MODE_NIGHT_NO, group = "Locales")
@Preview(name = "Russian Night", locale = "ru", uiMode = Configuration.UI_MODE_NIGHT_YES, group = "Locales")
annotation class PreviewRussian

@Preview(name = "English Day", locale = "en", uiMode = Configuration.UI_MODE_NIGHT_NO, group = "All Locales")
@Preview(name = "English Night", locale = "en", uiMode = Configuration.UI_MODE_NIGHT_YES, group = "All Locales")
@Preview(name = "French Day", locale = "fr", uiMode = Configuration.UI_MODE_NIGHT_NO, group = "All Locales")
@Preview(name = "French Night", locale = "fr", uiMode = Configuration.UI_MODE_NIGHT_YES, group = "All Locales")
@Preview(name = "Russian Day", locale = "ru", uiMode = Configuration.UI_MODE_NIGHT_NO, group = "All Locales")
@Preview(name = "Russian Night", locale = "ru", uiMode = Configuration.UI_MODE_NIGHT_YES, group = "All Locales")
annotation class PreviewAllLocales

