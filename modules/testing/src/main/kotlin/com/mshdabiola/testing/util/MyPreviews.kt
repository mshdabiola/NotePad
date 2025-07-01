package com.mshdabiola.testing.util

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

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
@Preview(
    name = "Small Screen Night",
    device = "id:pixel_4a",
    widthDp = 360,
    heightDp = 640,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    group = "All Screens",
    showSystemUi = false,
    showBackground = false,
)
@Preview(name = "Medium Screen Day", device = "id:pixel_4_xl", widthDp = 411, heightDp = 891, uiMode = Configuration.UI_MODE_NIGHT_NO, group = "All Screens")
@Preview(name = "Medium Screen Night", device = "id:pixel_4_xl", widthDp = 411, heightDp = 891, uiMode = Configuration.UI_MODE_NIGHT_YES, group = "All Screens")
@Preview(name = "Large Screen Day", device = "id:pixel_c", widthDp = 800, heightDp = 1280, uiMode = Configuration.UI_MODE_NIGHT_NO, group = "All Screens")
@Preview(name = "Large Screen Night", device = "id:pixel_c", widthDp = 800, heightDp = 1280, uiMode = Configuration.UI_MODE_NIGHT_YES, group = "All Screens")
annotation class PreviewAllScreenSizes

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
