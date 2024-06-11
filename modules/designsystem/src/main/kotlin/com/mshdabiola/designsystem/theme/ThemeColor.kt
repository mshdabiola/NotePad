/*
 *abiola 2024
 */

package com.mshdabiola.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mshdabiola.model.Contrast

sealed class ThemeColor(val isDark: Boolean, val contrast: Contrast) {

    abstract fun getColorScheme(): ColorScheme
    fun getGradientColors(): GradientColors {
        val colorScheme = getColorScheme()
        return GradientColors(
            top = colorScheme.inverseOnSurface,
            bottom = colorScheme.primaryContainer,
            container = colorScheme.surface,
        )
    }

    fun getTintTheme(): TintTheme {
        return TintTheme()
    }

    fun getBackgroundTheme(): BackgroundTheme {
        val colorScheme = getColorScheme()

        return BackgroundTheme(
            color = colorScheme.surface,
            tonalElevation = 2.dp,
        )
    }

    class DefaultThemeColor(isDark: Boolean, contrast: Contrast) : ThemeColor(isDark, contrast) {
        override fun getColorScheme(): ColorScheme {
            return when {
                contrast == Contrast.Normal && isDark -> {
                    val primaryDark = Color(0xFF9CCBFB)
                    val onPrimaryDark = Color(0xFF003354)
                    val primaryContainerDark = Color(0xFF124A73)
                    val onPrimaryContainerDark = Color(0xFFCFE5FF)
                    val secondaryDark = Color(0xFFB9C8DA)
                    val onSecondaryDark = Color(0xFF243240)
                    val secondaryContainerDark = Color(0xFF3A4857)
                    val onSecondaryContainerDark = Color(0xFFD5E4F7)
                    val tertiaryDark = Color(0xFFD4BEE6)
                    val onTertiaryDark = Color(0xFF392A49)
                    val tertiaryContainerDark = Color(0xFF504060)
                    val onTertiaryContainerDark = Color(0xFFF0DBFF)
                    val errorDark = Color(0xFFFFB4AB)
                    val onErrorDark = Color(0xFF690005)
                    val errorContainerDark = Color(0xFF93000A)
                    val onErrorContainerDark = Color(0xFFFFDAD6)
                    val backgroundDark = Color(0xFF101418)
                    val onBackgroundDark = Color(0xFFE0E2E8)
                    val surfaceDark = Color(0xFF101418)
                    val onSurfaceDark = Color(0xFFE0E2E8)
                    val surfaceVariantDark = Color(0xFF42474E)
                    val onSurfaceVariantDark = Color(0xFFC2C7CF)
                    val outlineDark = Color(0xFF8C9199)
                    val outlineVariantDark = Color(0xFF42474E)
                    val scrimDark = Color(0xFF000000)
                    val inverseSurfaceDark = Color(0xFFE0E2E8)
                    val inverseOnSurfaceDark = Color(0xFF2D3135)
                    val inversePrimaryDark = Color(0xFF31628D)
                    val surfaceDimDark = Color(0xFF101418)
                    val surfaceBrightDark = Color(0xFF36393E)
                    val surfaceContainerLowestDark = Color(0xFF0B0E12)
                    val surfaceContainerLowDark = Color(0xFF181C20)
                    val surfaceContainerDark = Color(0xFF1D2024)
                    val surfaceContainerHighDark = Color(0xFF272A2F)
                    val surfaceContainerHighestDark = Color(0xFF32353A)

                    darkColorScheme(
                        primary = primaryDark,
                        onPrimary = onPrimaryDark,
                        primaryContainer = primaryContainerDark,
                        onPrimaryContainer = onPrimaryContainerDark,
                        secondary = secondaryDark,
                        onSecondary = onSecondaryDark,
                        secondaryContainer = secondaryContainerDark,
                        onSecondaryContainer = onSecondaryContainerDark,
                        tertiary = tertiaryDark,
                        onTertiary = onTertiaryDark,
                        tertiaryContainer = tertiaryContainerDark,
                        onTertiaryContainer = onTertiaryContainerDark,
                        error = errorDark,
                        onError = onErrorDark,
                        errorContainer = errorContainerDark,
                        onErrorContainer = onErrorContainerDark,
                        background = backgroundDark,
                        onBackground = onBackgroundDark,
                        surface = surfaceDark,
                        onSurface = onSurfaceDark,
                        surfaceVariant = surfaceVariantDark,
                        onSurfaceVariant = onSurfaceVariantDark,
                        outline = outlineDark,
                        outlineVariant = outlineVariantDark,
                        scrim = scrimDark,
                        inverseSurface = inverseSurfaceDark,
                        inverseOnSurface = inverseOnSurfaceDark,
                        inversePrimary = inversePrimaryDark,
                    )
                }

                contrast == Contrast.Medium && isDark -> {
                    val primaryDarkMediumContrast = Color(0xFFA1CFFF)
                    val onPrimaryDarkMediumContrast = Color(0xFF00182B)
                    val primaryContainerDarkMediumContrast = Color(0xFF6695C2)
                    val onPrimaryContainerDarkMediumContrast = Color(0xFF000000)
                    val secondaryDarkMediumContrast = Color(0xFFBECCDF)
                    val onSecondaryDarkMediumContrast = Color(0xFF091725)
                    val secondaryContainerDarkMediumContrast = Color(0xFF8492A3)
                    val onSecondaryContainerDarkMediumContrast = Color(0xFF000000)
                    val tertiaryDarkMediumContrast = Color(0xFFD8C3EA)
                    val onTertiaryDarkMediumContrast = Color(0xFF1E0F2D)
                    val tertiaryContainerDarkMediumContrast = Color(0xFF9D89AE)
                    val onTertiaryContainerDarkMediumContrast = Color(0xFF000000)
                    val errorDarkMediumContrast = Color(0xFFFFBAB1)
                    val onErrorDarkMediumContrast = Color(0xFF370001)
                    val errorContainerDarkMediumContrast = Color(0xFFFF5449)
                    val onErrorContainerDarkMediumContrast = Color(0xFF000000)
                    val backgroundDarkMediumContrast = Color(0xFF101418)
                    val onBackgroundDarkMediumContrast = Color(0xFFE0E2E8)
                    val surfaceDarkMediumContrast = Color(0xFF101418)
                    val onSurfaceDarkMediumContrast = Color(0xFFFAFAFF)
                    val surfaceVariantDarkMediumContrast = Color(0xFF42474E)
                    val onSurfaceVariantDarkMediumContrast = Color(0xFFC6CBD3)
                    val outlineDarkMediumContrast = Color(0xFF9EA3AB)
                    val outlineVariantDarkMediumContrast = Color(0xFF7F838B)
                    val scrimDarkMediumContrast = Color(0xFF000000)
                    val inverseSurfaceDarkMediumContrast = Color(0xFFE0E2E8)
                    val inverseOnSurfaceDarkMediumContrast = Color(0xFF272A2F)
                    val inversePrimaryDarkMediumContrast = Color(0xFF144B75)
                    val surfaceDimDarkMediumContrast = Color(0xFF101418)
                    val surfaceBrightDarkMediumContrast = Color(0xFF36393E)
                    val surfaceContainerLowestDarkMediumContrast = Color(0xFF0B0E12)
                    val surfaceContainerLowDarkMediumContrast = Color(0xFF181C20)
                    val surfaceContainerDarkMediumContrast = Color(0xFF1D2024)
                    val surfaceContainerHighDarkMediumContrast = Color(0xFF272A2F)
                    val surfaceContainerHighestDarkMediumContrast = Color(0xFF32353A)

                    darkColorScheme(
                        primary = primaryDarkMediumContrast,
                        onPrimary = onPrimaryDarkMediumContrast,
                        primaryContainer = primaryContainerDarkMediumContrast,
                        onPrimaryContainer = onPrimaryContainerDarkMediumContrast,
                        secondary = secondaryDarkMediumContrast,
                        onSecondary = onSecondaryDarkMediumContrast,
                        secondaryContainer = secondaryContainerDarkMediumContrast,
                        onSecondaryContainer = onSecondaryContainerDarkMediumContrast,
                        tertiary = tertiaryDarkMediumContrast,
                        onTertiary = onTertiaryDarkMediumContrast,
                        tertiaryContainer = tertiaryContainerDarkMediumContrast,
                        onTertiaryContainer = onTertiaryContainerDarkMediumContrast,
                        error = errorDarkMediumContrast,
                        onError = onErrorDarkMediumContrast,
                        errorContainer = errorContainerDarkMediumContrast,
                        onErrorContainer = onErrorContainerDarkMediumContrast,
                        background = backgroundDarkMediumContrast,
                        onBackground = onBackgroundDarkMediumContrast,
                        surface = surfaceDarkMediumContrast,
                        onSurface = onSurfaceDarkMediumContrast,
                        surfaceVariant = surfaceVariantDarkMediumContrast,
                        onSurfaceVariant = onSurfaceVariantDarkMediumContrast,
                        outline = outlineDarkMediumContrast,
                        outlineVariant = outlineVariantDarkMediumContrast,
                        scrim = scrimDarkMediumContrast,
                        inverseSurface = inverseSurfaceDarkMediumContrast,
                        inverseOnSurface = inverseOnSurfaceDarkMediumContrast,
                        inversePrimary = inversePrimaryDarkMediumContrast,
                    )
                }

                contrast == Contrast.Medium && !isDark -> {
                    val primaryLightMediumContrast = Color(0xFF0A466F)
                    val onPrimaryLightMediumContrast = Color(0xFFFFFFFF)
                    val primaryContainerLightMediumContrast = Color(0xFF4978A4)
                    val onPrimaryContainerLightMediumContrast = Color(0xFFFFFFFF)
                    val secondaryLightMediumContrast = Color(0xFF364453)
                    val onSecondaryLightMediumContrast = Color(0xFFFFFFFF)
                    val secondaryContainerLightMediumContrast = Color(0xFF687686)
                    val onSecondaryContainerLightMediumContrast = Color(0xFFFFFFFF)
                    val tertiaryLightMediumContrast = Color(0xFF4C3C5C)
                    val onTertiaryLightMediumContrast = Color(0xFFFFFFFF)
                    val tertiaryContainerLightMediumContrast = Color(0xFF806D91)
                    val onTertiaryContainerLightMediumContrast = Color(0xFFFFFFFF)
                    val errorLightMediumContrast = Color(0xFF8C0009)
                    val onErrorLightMediumContrast = Color(0xFFFFFFFF)
                    val errorContainerLightMediumContrast = Color(0xFFDA342E)
                    val onErrorContainerLightMediumContrast = Color(0xFFFFFFFF)
                    val backgroundLightMediumContrast = Color(0xFFF7F9FF)
                    val onBackgroundLightMediumContrast = Color(0xFF181C20)
                    val surfaceLightMediumContrast = Color(0xFFF7F9FF)
                    val onSurfaceLightMediumContrast = Color(0xFF181C20)
                    val surfaceVariantLightMediumContrast = Color(0xFFDEE3EB)
                    val onSurfaceVariantLightMediumContrast = Color(0xFF3E434A)
                    val outlineLightMediumContrast = Color(0xFF5A5F66)
                    val outlineVariantLightMediumContrast = Color(0xFF767B82)
                    val scrimLightMediumContrast = Color(0xFF000000)
                    val inverseSurfaceLightMediumContrast = Color(0xFF2D3135)
                    val inverseOnSurfaceLightMediumContrast = Color(0xFFEFF1F6)
                    val inversePrimaryLightMediumContrast = Color(0xFF9CCBFB)
                    val surfaceDimLightMediumContrast = Color(0xFFD8DAE0)
                    val surfaceBrightLightMediumContrast = Color(0xFFF7F9FF)
                    val surfaceContainerLowestLightMediumContrast = Color(0xFFFFFFFF)
                    val surfaceContainerLowLightMediumContrast = Color(0xFFF2F3F9)
                    val surfaceContainerLightMediumContrast = Color(0xFFECEEF4)
                    val surfaceContainerHighLightMediumContrast = Color(0xFFE6E8EE)
                    val surfaceContainerHighestLightMediumContrast = Color(0xFFE0E2E8)

                    lightColorScheme(
                        primary = primaryLightMediumContrast,
                        onPrimary = onPrimaryLightMediumContrast,
                        primaryContainer = primaryContainerLightMediumContrast,
                        onPrimaryContainer = onPrimaryContainerLightMediumContrast,
                        secondary = secondaryLightMediumContrast,
                        onSecondary = onSecondaryLightMediumContrast,
                        secondaryContainer = secondaryContainerLightMediumContrast,
                        onSecondaryContainer = onSecondaryContainerLightMediumContrast,
                        tertiary = tertiaryLightMediumContrast,
                        onTertiary = onTertiaryLightMediumContrast,
                        tertiaryContainer = tertiaryContainerLightMediumContrast,
                        onTertiaryContainer = onTertiaryContainerLightMediumContrast,
                        error = errorLightMediumContrast,
                        onError = onErrorLightMediumContrast,
                        errorContainer = errorContainerLightMediumContrast,
                        onErrorContainer = onErrorContainerLightMediumContrast,
                        background = backgroundLightMediumContrast,
                        onBackground = onBackgroundLightMediumContrast,
                        surface = surfaceLightMediumContrast,
                        onSurface = onSurfaceLightMediumContrast,
                        surfaceVariant = surfaceVariantLightMediumContrast,
                        onSurfaceVariant = onSurfaceVariantLightMediumContrast,
                        outline = outlineLightMediumContrast,
                        outlineVariant = outlineVariantLightMediumContrast,
                        scrim = scrimLightMediumContrast,
                        inverseSurface = inverseSurfaceLightMediumContrast,
                        inverseOnSurface = inverseOnSurfaceLightMediumContrast,
                        inversePrimary = inversePrimaryLightMediumContrast,
                    )
                }

                contrast == Contrast.High && isDark -> {
                    val primaryDarkHighContrast = Color(0xFFFAFAFF)
                    val onPrimaryDarkHighContrast = Color(0xFF000000)
                    val primaryContainerDarkHighContrast = Color(0xFFA1CFFF)
                    val onPrimaryContainerDarkHighContrast = Color(0xFF000000)
                    val secondaryDarkHighContrast = Color(0xFFFAFAFF)
                    val onSecondaryDarkHighContrast = Color(0xFF000000)
                    val secondaryContainerDarkHighContrast = Color(0xFFBECCDF)
                    val onSecondaryContainerDarkHighContrast = Color(0xFF000000)
                    val tertiaryDarkHighContrast = Color(0xFFFFF9FC)
                    val onTertiaryDarkHighContrast = Color(0xFF000000)
                    val tertiaryContainerDarkHighContrast = Color(0xFFD8C3EA)
                    val onTertiaryContainerDarkHighContrast = Color(0xFF000000)
                    val errorDarkHighContrast = Color(0xFFFFF9F9)
                    val onErrorDarkHighContrast = Color(0xFF000000)
                    val errorContainerDarkHighContrast = Color(0xFFFFBAB1)
                    val onErrorContainerDarkHighContrast = Color(0xFF000000)
                    val backgroundDarkHighContrast = Color(0xFF101418)
                    val onBackgroundDarkHighContrast = Color(0xFFE0E2E8)
                    val surfaceDarkHighContrast = Color(0xFF101418)
                    val onSurfaceDarkHighContrast = Color(0xFFFFFFFF)
                    val surfaceVariantDarkHighContrast = Color(0xFF42474E)
                    val onSurfaceVariantDarkHighContrast = Color(0xFFFAFAFF)
                    val outlineDarkHighContrast = Color(0xFFC6CBD3)
                    val outlineVariantDarkHighContrast = Color(0xFFC6CBD3)
                    val scrimDarkHighContrast = Color(0xFF000000)
                    val inverseSurfaceDarkHighContrast = Color(0xFFE0E2E8)
                    val inverseOnSurfaceDarkHighContrast = Color(0xFF000000)
                    val inversePrimaryDarkHighContrast = Color(0xFF002C4A)
                    val surfaceDimDarkHighContrast = Color(0xFF101418)
                    val surfaceBrightDarkHighContrast = Color(0xFF36393E)
                    val surfaceContainerLowestDarkHighContrast = Color(0xFF0B0E12)
                    val surfaceContainerLowDarkHighContrast = Color(0xFF181C20)
                    val surfaceContainerDarkHighContrast = Color(0xFF1D2024)
                    val surfaceContainerHighDarkHighContrast = Color(0xFF272A2F)
                    val surfaceContainerHighestDarkHighContrast = Color(0xFF32353A)

                    darkColorScheme(
                        primary = primaryDarkHighContrast,
                        onPrimary = onPrimaryDarkHighContrast,
                        primaryContainer = primaryContainerDarkHighContrast,
                        onPrimaryContainer = onPrimaryContainerDarkHighContrast,
                        secondary = secondaryDarkHighContrast,
                        onSecondary = onSecondaryDarkHighContrast,
                        secondaryContainer = secondaryContainerDarkHighContrast,
                        onSecondaryContainer = onSecondaryContainerDarkHighContrast,
                        tertiary = tertiaryDarkHighContrast,
                        onTertiary = onTertiaryDarkHighContrast,
                        tertiaryContainer = tertiaryContainerDarkHighContrast,
                        onTertiaryContainer = onTertiaryContainerDarkHighContrast,
                        error = errorDarkHighContrast,
                        onError = onErrorDarkHighContrast,
                        errorContainer = errorContainerDarkHighContrast,
                        onErrorContainer = onErrorContainerDarkHighContrast,
                        background = backgroundDarkHighContrast,
                        onBackground = onBackgroundDarkHighContrast,
                        surface = surfaceDarkHighContrast,
                        onSurface = onSurfaceDarkHighContrast,
                        surfaceVariant = surfaceVariantDarkHighContrast,
                        onSurfaceVariant = onSurfaceVariantDarkHighContrast,
                        outline = outlineDarkHighContrast,
                        outlineVariant = outlineVariantDarkHighContrast,
                        scrim = scrimDarkHighContrast,
                        inverseSurface = inverseSurfaceDarkHighContrast,
                        inverseOnSurface = inverseOnSurfaceDarkHighContrast,
                        inversePrimary = inversePrimaryDarkHighContrast,
                    )
                }

                contrast == Contrast.High && !isDark -> {
                    val primaryLightHighContrast = Color(0xFF00243E)
                    val onPrimaryLightHighContrast = Color(0xFFFFFFFF)
                    val primaryContainerLightHighContrast = Color(0xFF0A466F)
                    val onPrimaryContainerLightHighContrast = Color(0xFFFFFFFF)
                    val secondaryLightHighContrast = Color(0xFF152331)
                    val onSecondaryLightHighContrast = Color(0xFFFFFFFF)
                    val secondaryContainerLightHighContrast = Color(0xFF364453)
                    val onSecondaryContainerLightHighContrast = Color(0xFFFFFFFF)
                    val tertiaryLightHighContrast = Color(0xFF2A1C3A)
                    val onTertiaryLightHighContrast = Color(0xFFFFFFFF)
                    val tertiaryContainerLightHighContrast = Color(0xFF4C3C5C)
                    val onTertiaryContainerLightHighContrast = Color(0xFFFFFFFF)
                    val errorLightHighContrast = Color(0xFF4E0002)
                    val onErrorLightHighContrast = Color(0xFFFFFFFF)
                    val errorContainerLightHighContrast = Color(0xFF8C0009)
                    val onErrorContainerLightHighContrast = Color(0xFFFFFFFF)
                    val backgroundLightHighContrast = Color(0xFFF7F9FF)
                    val onBackgroundLightHighContrast = Color(0xFF181C20)
                    val surfaceLightHighContrast = Color(0xFFF7F9FF)
                    val onSurfaceLightHighContrast = Color(0xFF000000)
                    val surfaceVariantLightHighContrast = Color(0xFFDEE3EB)
                    val onSurfaceVariantLightHighContrast = Color(0xFF1F242A)
                    val outlineLightHighContrast = Color(0xFF3E434A)
                    val outlineVariantLightHighContrast = Color(0xFF3E434A)
                    val scrimLightHighContrast = Color(0xFF000000)
                    val inverseSurfaceLightHighContrast = Color(0xFF2D3135)
                    val inverseOnSurfaceLightHighContrast = Color(0xFFFFFFFF)
                    val inversePrimaryLightHighContrast = Color(0xFFE0EDFF)
                    val surfaceDimLightHighContrast = Color(0xFFD8DAE0)
                    val surfaceBrightLightHighContrast = Color(0xFFF7F9FF)
                    val surfaceContainerLowestLightHighContrast = Color(0xFFFFFFFF)
                    val surfaceContainerLowLightHighContrast = Color(0xFFF2F3F9)
                    val surfaceContainerLightHighContrast = Color(0xFFECEEF4)
                    val surfaceContainerHighLightHighContrast = Color(0xFFE6E8EE)
                    val surfaceContainerHighestLightHighContrast = Color(0xFFE0E2E8)

                    lightColorScheme(
                        primary = primaryLightHighContrast,
                        onPrimary = onPrimaryLightHighContrast,
                        primaryContainer = primaryContainerLightHighContrast,
                        onPrimaryContainer = onPrimaryContainerLightHighContrast,
                        secondary = secondaryLightHighContrast,
                        onSecondary = onSecondaryLightHighContrast,
                        secondaryContainer = secondaryContainerLightHighContrast,
                        onSecondaryContainer = onSecondaryContainerLightHighContrast,
                        tertiary = tertiaryLightHighContrast,
                        onTertiary = onTertiaryLightHighContrast,
                        tertiaryContainer = tertiaryContainerLightHighContrast,
                        onTertiaryContainer = onTertiaryContainerLightHighContrast,
                        error = errorLightHighContrast,
                        onError = onErrorLightHighContrast,
                        errorContainer = errorContainerLightHighContrast,
                        onErrorContainer = onErrorContainerLightHighContrast,
                        background = backgroundLightHighContrast,
                        onBackground = onBackgroundLightHighContrast,
                        surface = surfaceLightHighContrast,
                        onSurface = onSurfaceLightHighContrast,
                        surfaceVariant = surfaceVariantLightHighContrast,
                        onSurfaceVariant = onSurfaceVariantLightHighContrast,
                        outline = outlineLightHighContrast,
                        outlineVariant = outlineVariantLightHighContrast,
                        scrim = scrimLightHighContrast,
                        inverseSurface = inverseSurfaceLightHighContrast,
                        inverseOnSurface = inverseOnSurfaceLightHighContrast,
                        inversePrimary = inversePrimaryLightHighContrast,
                    )
                }

                else -> {
                    val primaryLight = Color(0xFF31628D)
                    val onPrimaryLight = Color(0xFFFFFFFF)
                    val primaryContainerLight = Color(0xFFCFE5FF)
                    val onPrimaryContainerLight = Color(0xFF001D33)
                    val secondaryLight = Color(0xFF526070)
                    val onSecondaryLight = Color(0xFFFFFFFF)
                    val secondaryContainerLight = Color(0xFFD5E4F7)
                    val onSecondaryContainerLight = Color(0xFF0E1D2A)
                    val tertiaryLight = Color(0xFF695779)
                    val onTertiaryLight = Color(0xFFFFFFFF)
                    val tertiaryContainerLight = Color(0xFFF0DBFF)
                    val onTertiaryContainerLight = Color(0xFF231533)
                    val errorLight = Color(0xFFBA1A1A)
                    val onErrorLight = Color(0xFFFFFFFF)
                    val errorContainerLight = Color(0xFFFFDAD6)
                    val onErrorContainerLight = Color(0xFF410002)
                    val backgroundLight = Color(0xFFF7F9FF)
                    val onBackgroundLight = Color(0xFF181C20)
                    val surfaceLight = Color(0xFFF7F9FF)
                    val onSurfaceLight = Color(0xFF181C20)
                    val surfaceVariantLight = Color(0xFFDEE3EB)
                    val onSurfaceVariantLight = Color(0xFF42474E)
                    val outlineLight = Color(0xFF72777F)
                    val outlineVariantLight = Color(0xFFC2C7CF)
                    val scrimLight = Color(0xFF000000)
                    val inverseSurfaceLight = Color(0xFF2D3135)
                    val inverseOnSurfaceLight = Color(0xFFEFF1F6)
                    val inversePrimaryLight = Color(0xFF9CCBFB)
                    val surfaceDimLight = Color(0xFFD8DAE0)
                    val surfaceBrightLight = Color(0xFFF7F9FF)
                    val surfaceContainerLowestLight = Color(0xFFFFFFFF)
                    val surfaceContainerLowLight = Color(0xFFF2F3F9)
                    val surfaceContainerLight = Color(0xFFECEEF4)
                    val surfaceContainerHighLight = Color(0xFFE6E8EE)
                    val surfaceContainerHighestLight = Color(0xFFE0E2E8)

                    lightColorScheme(
                        primary = primaryLight,
                        onPrimary = onPrimaryLight,
                        primaryContainer = primaryContainerLight,
                        onPrimaryContainer = onPrimaryContainerLight,
                        secondary = secondaryLight,
                        onSecondary = onSecondaryLight,
                        secondaryContainer = secondaryContainerLight,
                        onSecondaryContainer = onSecondaryContainerLight,
                        tertiary = tertiaryLight,
                        onTertiary = onTertiaryLight,
                        tertiaryContainer = tertiaryContainerLight,
                        onTertiaryContainer = onTertiaryContainerLight,
                        error = errorLight,
                        onError = onErrorLight,
                        errorContainer = errorContainerLight,
                        onErrorContainer = onErrorContainerLight,
                        background = backgroundLight,
                        onBackground = onBackgroundLight,
                        surface = surfaceLight,
                        onSurface = onSurfaceLight,
                        surfaceVariant = surfaceVariantLight,
                        onSurfaceVariant = onSurfaceVariantLight,
                        outline = outlineLight,
                        outlineVariant = outlineVariantLight,
                        scrim = scrimLight,
                        inverseSurface = inverseSurfaceLight,
                        inverseOnSurface = inverseOnSurfaceLight,
                        inversePrimary = inversePrimaryLight,
                    )
                }
            }
        }
    }

    class GreenThemeColor(isDark: Boolean, contrast: Contrast) : ThemeColor(isDark, contrast) {
        override fun getColorScheme(): ColorScheme {
            return when {
                contrast == Contrast.Normal && isDark -> {
                    val primaryDark = Color(0xFFB6D085)
                    val onPrimaryDark = Color(0xFF243600)
                    val primaryContainerDark = Color(0xFF394D11)
                    val onPrimaryContainerDark = Color(0xFFD2EC9F)
                    val secondaryDark = Color(0xFFC1CAAB)
                    val onSecondaryDark = Color(0xFF2C331D)
                    val secondaryContainerDark = Color(0xFF424A32)
                    val onSecondaryContainerDark = Color(0xFFDDE6C6)
                    val tertiaryDark = Color(0xFFA0D0C9)
                    val onTertiaryDark = Color(0xFF013733)
                    val tertiaryContainerDark = Color(0xFF1F4E49)
                    val onTertiaryContainerDark = Color(0xFFBCECE5)
                    val errorDark = Color(0xFFFFB4AB)
                    val onErrorDark = Color(0xFF690005)
                    val errorContainerDark = Color(0xFF93000A)
                    val onErrorContainerDark = Color(0xFFFFDAD6)
                    val backgroundDark = Color(0xFF12140D)
                    val onBackgroundDark = Color(0xFFE3E3D8)
                    val surfaceDark = Color(0xFF12140D)
                    val onSurfaceDark = Color(0xFFE3E3D8)
                    val surfaceVariantDark = Color(0xFF45483D)
                    val onSurfaceVariantDark = Color(0xFFC6C8B9)
                    val outlineDark = Color(0xFF8F9284)
                    val outlineVariantDark = Color(0xFF45483D)
                    val scrimDark = Color(0xFF000000)
                    val inverseSurfaceDark = Color(0xFFE3E3D8)
                    val inverseOnSurfaceDark = Color(0xFF2F3129)
                    val inversePrimaryDark = Color(0xFF506528)
                    val surfaceDimDark = Color(0xFF12140D)
                    val surfaceBrightDark = Color(0xFF383A32)
                    val surfaceContainerLowestDark = Color(0xFF0D0F09)
                    val surfaceContainerLowDark = Color(0xFF1A1C15)
                    val surfaceContainerDark = Color(0xFF1E2019)
                    val surfaceContainerHighDark = Color(0xFF292B23)
                    val surfaceContainerHighestDark = Color(0xFF34362E)

                    darkColorScheme(
                        primary = primaryDark,
                        onPrimary = onPrimaryDark,
                        primaryContainer = primaryContainerDark,
                        onPrimaryContainer = onPrimaryContainerDark,
                        secondary = secondaryDark,
                        onSecondary = onSecondaryDark,
                        secondaryContainer = secondaryContainerDark,
                        onSecondaryContainer = onSecondaryContainerDark,
                        tertiary = tertiaryDark,
                        onTertiary = onTertiaryDark,
                        tertiaryContainer = tertiaryContainerDark,
                        onTertiaryContainer = onTertiaryContainerDark,
                        error = errorDark,
                        onError = onErrorDark,
                        errorContainer = errorContainerDark,
                        onErrorContainer = onErrorContainerDark,
                        background = backgroundDark,
                        onBackground = onBackgroundDark,
                        surface = surfaceDark,
                        onSurface = onSurfaceDark,
                        surfaceVariant = surfaceVariantDark,
                        onSurfaceVariant = onSurfaceVariantDark,
                        outline = outlineDark,
                        outlineVariant = outlineVariantDark,
                        scrim = scrimDark,
                        inverseSurface = inverseSurfaceDark,
                        inverseOnSurface = inverseOnSurfaceDark,
                        inversePrimary = inversePrimaryDark,
                    )
                }

                contrast == Contrast.Medium && isDark -> {
                    val primaryDarkMediumContrast = Color(0xFFBAD489)
                    val onPrimaryDarkMediumContrast = Color(0xFF0F1900)
                    val primaryContainerDarkMediumContrast = Color(0xFF819955)
                    val onPrimaryContainerDarkMediumContrast = Color(0xFF000000)
                    val secondaryDarkMediumContrast = Color(0xFFC6CEAF)
                    val onSecondaryDarkMediumContrast = Color(0xFF121906)
                    val secondaryContainerDarkMediumContrast = Color(0xFF8C9478)
                    val onSecondaryContainerDarkMediumContrast = Color(0xFF000000)
                    val tertiaryDarkMediumContrast = Color(0xFFA4D4CD)
                    val onTertiaryDarkMediumContrast = Color(0xFF001A18)
                    val tertiaryContainerDarkMediumContrast = Color(0xFF6B9993)
                    val onTertiaryContainerDarkMediumContrast = Color(0xFF000000)
                    val errorDarkMediumContrast = Color(0xFFFFBAB1)
                    val onErrorDarkMediumContrast = Color(0xFF370001)
                    val errorContainerDarkMediumContrast = Color(0xFFFF5449)
                    val onErrorContainerDarkMediumContrast = Color(0xFF000000)
                    val backgroundDarkMediumContrast = Color(0xFF12140D)
                    val onBackgroundDarkMediumContrast = Color(0xFFE3E3D8)
                    val surfaceDarkMediumContrast = Color(0xFF12140D)
                    val onSurfaceDarkMediumContrast = Color(0xFFFBFBEF)
                    val surfaceVariantDarkMediumContrast = Color(0xFF45483D)
                    val onSurfaceVariantDarkMediumContrast = Color(0xFFCACCBD)
                    val outlineDarkMediumContrast = Color(0xFFA2A496)
                    val outlineVariantDarkMediumContrast = Color(0xFF828477)
                    val scrimDarkMediumContrast = Color(0xFF000000)
                    val inverseSurfaceDarkMediumContrast = Color(0xFFE3E3D8)
                    val inverseOnSurfaceDarkMediumContrast = Color(0xFF292B23)
                    val inversePrimaryDarkMediumContrast = Color(0xFF3A4E13)
                    val surfaceDimDarkMediumContrast = Color(0xFF12140D)
                    val surfaceBrightDarkMediumContrast = Color(0xFF383A32)
                    val surfaceContainerLowestDarkMediumContrast = Color(0xFF0D0F09)
                    val surfaceContainerLowDarkMediumContrast = Color(0xFF1A1C15)
                    val surfaceContainerDarkMediumContrast = Color(0xFF1E2019)
                    val surfaceContainerHighDarkMediumContrast = Color(0xFF292B23)
                    val surfaceContainerHighestDarkMediumContrast = Color(0xFF34362E)

                    darkColorScheme(
                        primary = primaryDarkMediumContrast,
                        onPrimary = onPrimaryDarkMediumContrast,
                        primaryContainer = primaryContainerDarkMediumContrast,
                        onPrimaryContainer = onPrimaryContainerDarkMediumContrast,
                        secondary = secondaryDarkMediumContrast,
                        onSecondary = onSecondaryDarkMediumContrast,
                        secondaryContainer = secondaryContainerDarkMediumContrast,
                        onSecondaryContainer = onSecondaryContainerDarkMediumContrast,
                        tertiary = tertiaryDarkMediumContrast,
                        onTertiary = onTertiaryDarkMediumContrast,
                        tertiaryContainer = tertiaryContainerDarkMediumContrast,
                        onTertiaryContainer = onTertiaryContainerDarkMediumContrast,
                        error = errorDarkMediumContrast,
                        onError = onErrorDarkMediumContrast,
                        errorContainer = errorContainerDarkMediumContrast,
                        onErrorContainer = onErrorContainerDarkMediumContrast,
                        background = backgroundDarkMediumContrast,
                        onBackground = onBackgroundDarkMediumContrast,
                        surface = surfaceDarkMediumContrast,
                        onSurface = onSurfaceDarkMediumContrast,
                        surfaceVariant = surfaceVariantDarkMediumContrast,
                        onSurfaceVariant = onSurfaceVariantDarkMediumContrast,
                        outline = outlineDarkMediumContrast,
                        outlineVariant = outlineVariantDarkMediumContrast,
                        scrim = scrimDarkMediumContrast,
                        inverseSurface = inverseSurfaceDarkMediumContrast,
                        inverseOnSurface = inverseOnSurfaceDarkMediumContrast,
                        inversePrimary = inversePrimaryDarkMediumContrast,
                    )
                }

                contrast == Contrast.Medium && !isDark -> {
                    val primaryLightMediumContrast = Color(0xFF35490D)
                    val onPrimaryLightMediumContrast = Color(0xFFFFFFFF)
                    val primaryContainerLightMediumContrast = Color(0xFF667C3C)
                    val onPrimaryContainerLightMediumContrast = Color(0xFFFFFFFF)
                    val secondaryLightMediumContrast = Color(0xFF3E462E)
                    val onSecondaryLightMediumContrast = Color(0xFFFFFFFF)
                    val secondaryContainerLightMediumContrast = Color(0xFF6F785D)
                    val onSecondaryContainerLightMediumContrast = Color(0xFFFFFFFF)
                    val tertiaryLightMediumContrast = Color(0xFF1B4A45)
                    val onTertiaryLightMediumContrast = Color(0xFFFFFFFF)
                    val tertiaryContainerLightMediumContrast = Color(0xFF4F7D77)
                    val onTertiaryContainerLightMediumContrast = Color(0xFFFFFFFF)
                    val errorLightMediumContrast = Color(0xFF8C0009)
                    val onErrorLightMediumContrast = Color(0xFFFFFFFF)
                    val errorContainerLightMediumContrast = Color(0xFFDA342E)
                    val onErrorContainerLightMediumContrast = Color(0xFFFFFFFF)
                    val backgroundLightMediumContrast = Color(0xFFFAFAEE)
                    val onBackgroundLightMediumContrast = Color(0xFF1A1C15)
                    val surfaceLightMediumContrast = Color(0xFFFAFAEE)
                    val onSurfaceLightMediumContrast = Color(0xFF1A1C15)
                    val surfaceVariantLightMediumContrast = Color(0xFFE2E4D4)
                    val onSurfaceVariantLightMediumContrast = Color(0xFF414439)
                    val outlineLightMediumContrast = Color(0xFF5D6054)
                    val outlineVariantLightMediumContrast = Color(0xFF797C6F)
                    val scrimLightMediumContrast = Color(0xFF000000)
                    val inverseSurfaceLightMediumContrast = Color(0xFF2F3129)
                    val inverseOnSurfaceLightMediumContrast = Color(0xFFF1F1E6)
                    val inversePrimaryLightMediumContrast = Color(0xFFB6D085)
                    val surfaceDimLightMediumContrast = Color(0xFFDADBCF)
                    val surfaceBrightLightMediumContrast = Color(0xFFFAFAEE)
                    val surfaceContainerLowestLightMediumContrast = Color(0xFFFFFFFF)
                    val surfaceContainerLowLightMediumContrast = Color(0xFFF4F4E8)
                    val surfaceContainerLightMediumContrast = Color(0xFFEEEFE3)
                    val surfaceContainerHighLightMediumContrast = Color(0xFFE9E9DD)
                    val surfaceContainerHighestLightMediumContrast = Color(0xFFE3E3D8)

                    lightColorScheme(
                        primary = primaryLightMediumContrast,
                        onPrimary = onPrimaryLightMediumContrast,
                        primaryContainer = primaryContainerLightMediumContrast,
                        onPrimaryContainer = onPrimaryContainerLightMediumContrast,
                        secondary = secondaryLightMediumContrast,
                        onSecondary = onSecondaryLightMediumContrast,
                        secondaryContainer = secondaryContainerLightMediumContrast,
                        onSecondaryContainer = onSecondaryContainerLightMediumContrast,
                        tertiary = tertiaryLightMediumContrast,
                        onTertiary = onTertiaryLightMediumContrast,
                        tertiaryContainer = tertiaryContainerLightMediumContrast,
                        onTertiaryContainer = onTertiaryContainerLightMediumContrast,
                        error = errorLightMediumContrast,
                        onError = onErrorLightMediumContrast,
                        errorContainer = errorContainerLightMediumContrast,
                        onErrorContainer = onErrorContainerLightMediumContrast,
                        background = backgroundLightMediumContrast,
                        onBackground = onBackgroundLightMediumContrast,
                        surface = surfaceLightMediumContrast,
                        onSurface = onSurfaceLightMediumContrast,
                        surfaceVariant = surfaceVariantLightMediumContrast,
                        onSurfaceVariant = onSurfaceVariantLightMediumContrast,
                        outline = outlineLightMediumContrast,
                        outlineVariant = outlineVariantLightMediumContrast,
                        scrim = scrimLightMediumContrast,
                        inverseSurface = inverseSurfaceLightMediumContrast,
                        inverseOnSurface = inverseOnSurfaceLightMediumContrast,
                        inversePrimary = inversePrimaryLightMediumContrast,
                    )
                }

                contrast == Contrast.High && isDark -> {
                    val primaryDarkHighContrast = Color(0xFFF5FFDB)
                    val onPrimaryDarkHighContrast = Color(0xFF000000)
                    val primaryContainerDarkHighContrast = Color(0xFFBAD489)
                    val onPrimaryContainerDarkHighContrast = Color(0xFF000000)
                    val secondaryDarkHighContrast = Color(0xFFF6FFDD)
                    val onSecondaryDarkHighContrast = Color(0xFF000000)
                    val secondaryContainerDarkHighContrast = Color(0xFFC6CEAF)
                    val onSecondaryContainerDarkHighContrast = Color(0xFF000000)
                    val tertiaryDarkHighContrast = Color(0xFFEBFFFB)
                    val onTertiaryDarkHighContrast = Color(0xFF000000)
                    val tertiaryContainerDarkHighContrast = Color(0xFFA4D4CD)
                    val onTertiaryContainerDarkHighContrast = Color(0xFF000000)
                    val errorDarkHighContrast = Color(0xFFFFF9F9)
                    val onErrorDarkHighContrast = Color(0xFF000000)
                    val errorContainerDarkHighContrast = Color(0xFFFFBAB1)
                    val onErrorContainerDarkHighContrast = Color(0xFF000000)
                    val backgroundDarkHighContrast = Color(0xFF12140D)
                    val onBackgroundDarkHighContrast = Color(0xFFE3E3D8)
                    val surfaceDarkHighContrast = Color(0xFF12140D)
                    val onSurfaceDarkHighContrast = Color(0xFFFFFFFF)
                    val surfaceVariantDarkHighContrast = Color(0xFF45483D)
                    val onSurfaceVariantDarkHighContrast = Color(0xFFFAFCEC)
                    val outlineDarkHighContrast = Color(0xFFCACCBD)
                    val outlineVariantDarkHighContrast = Color(0xFFCACCBD)
                    val scrimDarkHighContrast = Color(0xFF000000)
                    val inverseSurfaceDarkHighContrast = Color(0xFFE3E3D8)
                    val inverseOnSurfaceDarkHighContrast = Color(0xFF000000)
                    val inversePrimaryDarkHighContrast = Color(0xFF1F2F00)
                    val surfaceDimDarkHighContrast = Color(0xFF12140D)
                    val surfaceBrightDarkHighContrast = Color(0xFF383A32)
                    val surfaceContainerLowestDarkHighContrast = Color(0xFF0D0F09)
                    val surfaceContainerLowDarkHighContrast = Color(0xFF1A1C15)
                    val surfaceContainerDarkHighContrast = Color(0xFF1E2019)
                    val surfaceContainerHighDarkHighContrast = Color(0xFF292B23)
                    val surfaceContainerHighestDarkHighContrast = Color(0xFF34362E)

                    darkColorScheme(
                        primary = primaryDarkHighContrast,
                        onPrimary = onPrimaryDarkHighContrast,
                        primaryContainer = primaryContainerDarkHighContrast,
                        onPrimaryContainer = onPrimaryContainerDarkHighContrast,
                        secondary = secondaryDarkHighContrast,
                        onSecondary = onSecondaryDarkHighContrast,
                        secondaryContainer = secondaryContainerDarkHighContrast,
                        onSecondaryContainer = onSecondaryContainerDarkHighContrast,
                        tertiary = tertiaryDarkHighContrast,
                        onTertiary = onTertiaryDarkHighContrast,
                        tertiaryContainer = tertiaryContainerDarkHighContrast,
                        onTertiaryContainer = onTertiaryContainerDarkHighContrast,
                        error = errorDarkHighContrast,
                        onError = onErrorDarkHighContrast,
                        errorContainer = errorContainerDarkHighContrast,
                        onErrorContainer = onErrorContainerDarkHighContrast,
                        background = backgroundDarkHighContrast,
                        onBackground = onBackgroundDarkHighContrast,
                        surface = surfaceDarkHighContrast,
                        onSurface = onSurfaceDarkHighContrast,
                        surfaceVariant = surfaceVariantDarkHighContrast,
                        onSurfaceVariant = onSurfaceVariantDarkHighContrast,
                        outline = outlineDarkHighContrast,
                        outlineVariant = outlineVariantDarkHighContrast,
                        scrim = scrimDarkHighContrast,
                        inverseSurface = inverseSurfaceDarkHighContrast,
                        inverseOnSurface = inverseOnSurfaceDarkHighContrast,
                        inversePrimary = inversePrimaryDarkHighContrast,
                    )
                }

                contrast == Contrast.High && !isDark -> {
                    val primaryLightHighContrast = Color(0xFF192600)
                    val onPrimaryLightHighContrast = Color(0xFFFFFFFF)
                    val primaryContainerLightHighContrast = Color(0xFF35490D)
                    val onPrimaryContainerLightHighContrast = Color(0xFFFFFFFF)
                    val secondaryLightHighContrast = Color(0xFF1D2510)
                    val onSecondaryLightHighContrast = Color(0xFFFFFFFF)
                    val secondaryContainerLightHighContrast = Color(0xFF3E462E)
                    val onSecondaryContainerLightHighContrast = Color(0xFFFFFFFF)
                    val tertiaryLightHighContrast = Color(0xFF002724)
                    val onTertiaryLightHighContrast = Color(0xFFFFFFFF)
                    val tertiaryContainerLightHighContrast = Color(0xFF1B4A45)
                    val onTertiaryContainerLightHighContrast = Color(0xFFFFFFFF)
                    val errorLightHighContrast = Color(0xFF4E0002)
                    val onErrorLightHighContrast = Color(0xFFFFFFFF)
                    val errorContainerLightHighContrast = Color(0xFF8C0009)
                    val onErrorContainerLightHighContrast = Color(0xFFFFFFFF)
                    val backgroundLightHighContrast = Color(0xFFFAFAEE)
                    val onBackgroundLightHighContrast = Color(0xFF1A1C15)
                    val surfaceLightHighContrast = Color(0xFFFAFAEE)
                    val onSurfaceLightHighContrast = Color(0xFF000000)
                    val surfaceVariantLightHighContrast = Color(0xFFE2E4D4)
                    val onSurfaceVariantLightHighContrast = Color(0xFF22251B)
                    val outlineLightHighContrast = Color(0xFF414439)
                    val outlineVariantLightHighContrast = Color(0xFF414439)
                    val scrimLightHighContrast = Color(0xFF000000)
                    val inverseSurfaceLightHighContrast = Color(0xFF2F3129)
                    val inverseOnSurfaceLightHighContrast = Color(0xFFFFFFFF)
                    val inversePrimaryLightHighContrast = Color(0xFFDBF6A8)
                    val surfaceDimLightHighContrast = Color(0xFFDADBCF)
                    val surfaceBrightLightHighContrast = Color(0xFFFAFAEE)
                    val surfaceContainerLowestLightHighContrast = Color(0xFFFFFFFF)
                    val surfaceContainerLowLightHighContrast = Color(0xFFF4F4E8)
                    val surfaceContainerLightHighContrast = Color(0xFFEEEFE3)
                    val surfaceContainerHighLightHighContrast = Color(0xFFE9E9DD)
                    val surfaceContainerHighestLightHighContrast = Color(0xFFE3E3D8)

                    lightColorScheme(
                        primary = primaryLightHighContrast,
                        onPrimary = onPrimaryLightHighContrast,
                        primaryContainer = primaryContainerLightHighContrast,
                        onPrimaryContainer = onPrimaryContainerLightHighContrast,
                        secondary = secondaryLightHighContrast,
                        onSecondary = onSecondaryLightHighContrast,
                        secondaryContainer = secondaryContainerLightHighContrast,
                        onSecondaryContainer = onSecondaryContainerLightHighContrast,
                        tertiary = tertiaryLightHighContrast,
                        onTertiary = onTertiaryLightHighContrast,
                        tertiaryContainer = tertiaryContainerLightHighContrast,
                        onTertiaryContainer = onTertiaryContainerLightHighContrast,
                        error = errorLightHighContrast,
                        onError = onErrorLightHighContrast,
                        errorContainer = errorContainerLightHighContrast,
                        onErrorContainer = onErrorContainerLightHighContrast,
                        background = backgroundLightHighContrast,
                        onBackground = onBackgroundLightHighContrast,
                        surface = surfaceLightHighContrast,
                        onSurface = onSurfaceLightHighContrast,
                        surfaceVariant = surfaceVariantLightHighContrast,
                        onSurfaceVariant = onSurfaceVariantLightHighContrast,
                        outline = outlineLightHighContrast,
                        outlineVariant = outlineVariantLightHighContrast,
                        scrim = scrimLightHighContrast,
                        inverseSurface = inverseSurfaceLightHighContrast,
                        inverseOnSurface = inverseOnSurfaceLightHighContrast,
                        inversePrimary = inversePrimaryLightHighContrast,
                    )
                }

                else -> {
                    val primaryLight = Color(0xFF506528)
                    val onPrimaryLight = Color(0xFFFFFFFF)
                    val primaryContainerLight = Color(0xFFD2EC9F)
                    val onPrimaryContainerLight = Color(0xFF131F00)
                    val secondaryLight = Color(0xFF596248)
                    val onSecondaryLight = Color(0xFFFFFFFF)
                    val secondaryContainerLight = Color(0xFFDDE6C6)
                    val onSecondaryContainerLight = Color(0xFF171E0A)
                    val tertiaryLight = Color(0xFF396661)
                    val onTertiaryLight = Color(0xFFFFFFFF)
                    val tertiaryContainerLight = Color(0xFFBCECE5)
                    val onTertiaryContainerLight = Color(0xFF00201D)
                    val errorLight = Color(0xFFBA1A1A)
                    val onErrorLight = Color(0xFFFFFFFF)
                    val errorContainerLight = Color(0xFFFFDAD6)
                    val onErrorContainerLight = Color(0xFF410002)
                    val backgroundLight = Color(0xFFFAFAEE)
                    val onBackgroundLight = Color(0xFF1A1C15)
                    val surfaceLight = Color(0xFFFAFAEE)
                    val onSurfaceLight = Color(0xFF1A1C15)
                    val surfaceVariantLight = Color(0xFFE2E4D4)
                    val onSurfaceVariantLight = Color(0xFF45483D)
                    val outlineLight = Color(0xFF76786B)
                    val outlineVariantLight = Color(0xFFC6C8B9)
                    val scrimLight = Color(0xFF000000)
                    val inverseSurfaceLight = Color(0xFF2F3129)
                    val inverseOnSurfaceLight = Color(0xFFF1F1E6)
                    val inversePrimaryLight = Color(0xFFB6D085)
                    val surfaceDimLight = Color(0xFFDADBCF)
                    val surfaceBrightLight = Color(0xFFFAFAEE)
                    val surfaceContainerLowestLight = Color(0xFFFFFFFF)
                    val surfaceContainerLowLight = Color(0xFFF4F4E8)
                    val surfaceContainerLight = Color(0xFFEEEFE3)
                    val surfaceContainerHighLight = Color(0xFFE9E9DD)
                    val surfaceContainerHighestLight = Color(0xFFE3E3D8)

                    lightColorScheme(
                        primary = primaryLight,
                        onPrimary = onPrimaryLight,
                        primaryContainer = primaryContainerLight,
                        onPrimaryContainer = onPrimaryContainerLight,
                        secondary = secondaryLight,
                        onSecondary = onSecondaryLight,
                        secondaryContainer = secondaryContainerLight,
                        onSecondaryContainer = onSecondaryContainerLight,
                        tertiary = tertiaryLight,
                        onTertiary = onTertiaryLight,
                        tertiaryContainer = tertiaryContainerLight,
                        onTertiaryContainer = onTertiaryContainerLight,
                        error = errorLight,
                        onError = onErrorLight,
                        errorContainer = errorContainerLight,
                        onErrorContainer = onErrorContainerLight,
                        background = backgroundLight,
                        onBackground = onBackgroundLight,
                        surface = surfaceLight,
                        onSurface = onSurfaceLight,
                        surfaceVariant = surfaceVariantLight,
                        onSurfaceVariant = onSurfaceVariantLight,
                        outline = outlineLight,
                        outlineVariant = outlineVariantLight,
                        scrim = scrimLight,
                        inverseSurface = inverseSurfaceLight,
                        inverseOnSurface = inverseOnSurfaceLight,
                        inversePrimary = inversePrimaryLight,
                    )
                }
            }
        }
    }
}
