/*
 *abiola 2022
 */

package com.mshdabiola.designsystem.theme

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

/**
 * Now in Android theme.
 *
 * @param darkTheme Whether the theme should use a dark color scheme (follows system by default).
 * @param androidTheme Whether the theme should use the Android theme color scheme instead of the
 *        default theme.
 * @param disableDynamicTheming If `true`, disables the use of dynamic theming, even when it is
 *        supported. This parameter has no effect if [androidTheme] is `true`.
 */
@Composable
fun SkTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    androidTheme: Boolean = false,
    disableDynamicTheming: Boolean = true,
    content: @Composable () -> Unit,
) {
    // Color scheme
    val colorScheme = when {
        androidTheme -> if (darkTheme) highContrastDarkColorScheme else highContrastLightColorScheme
        !disableDynamicTheming && supportsDynamicTheming() -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        else -> if (darkTheme) darkScheme else lightScheme
    }
    // Gradient colors
    val emptyGradientColors = GradientColors(container = colorScheme.surfaceColorAtElevation(2.dp))
    val defaultGradientColors = GradientColors(
        top = colorScheme.inverseOnSurface,
        bottom = colorScheme.primaryContainer,
        container = colorScheme.surface,
    )
    val gradientColors = when {
        androidTheme -> if (darkTheme) DarkAndroidGradientColors else LightAndroidGradientColors
        !disableDynamicTheming && supportsDynamicTheming() -> emptyGradientColors
        else -> defaultGradientColors
    }
    // Background theme
    val defaultBackgroundTheme = BackgroundTheme(
        color = colorScheme.surface,
        tonalElevation = 2.dp,
    )
    val backgroundTheme = when {
        androidTheme -> if (darkTheme) DarkAndroidBackgroundTheme else LightAndroidBackgroundTheme
        else -> defaultBackgroundTheme
    }
    val tintTheme = when {
        androidTheme -> TintTheme()
        !disableDynamicTheming && supportsDynamicTheming() -> TintTheme(colorScheme.primary)
        else -> TintTheme()
    }
    // Composition locals
    CompositionLocalProvider(
        LocalGradientColors provides gradientColors,
        LocalBackgroundTheme provides backgroundTheme,
        LocalTintTheme provides tintTheme,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = SkTypography,
            content = content,
        )
    }
}

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
fun supportsDynamicTheming() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

val primaryLight = Color(0xFF8E4D31)
val onPrimaryLight = Color(0xFFFFFFFF)
val primaryContainerLight = Color(0xFFFFDBCE)
val onPrimaryContainerLight = Color(0xFF370E00)
val secondaryLight = Color(0xFF77574B)
val onSecondaryLight = Color(0xFFFFFFFF)
val secondaryContainerLight = Color(0xFFFFDBCE)
val onSecondaryContainerLight = Color(0xFF2C160C)
val tertiaryLight = Color(0xFF685F30)
val onTertiaryLight = Color(0xFFFFFFFF)
val tertiaryContainerLight = Color(0xFFF0E3A8)
val onTertiaryContainerLight = Color(0xFF211B00)
val errorLight = Color(0xFFBA1A1A)
val onErrorLight = Color(0xFFFFFFFF)
val errorContainerLight = Color(0xFFFFDAD6)
val onErrorContainerLight = Color(0xFF410002)
val backgroundLight = Color(0xFFFFF8F6)
val onBackgroundLight = Color(0xFF231A16)
val surfaceLight = Color(0xFFFFF8F6)
val onSurfaceLight = Color(0xFF231A16)
val surfaceVariantLight = Color(0xFFF5DED6)
val onSurfaceVariantLight = Color(0xFF53433E)
val outlineLight = Color(0xFF85736D)
val outlineVariantLight = Color(0xFFD8C2BA)
val scrimLight = Color(0xFF000000)
val inverseSurfaceLight = Color(0xFF382E2A)
val inverseOnSurfaceLight = Color(0xFFFFEDE7)
val inversePrimaryLight = Color(0xFFFFB598)
val surfaceDimLight = Color(0xFFE8D6D0)
val surfaceBrightLight = Color(0xFFFFF8F6)
val surfaceContainerLowestLight = Color(0xFFFFFFFF)
val surfaceContainerLowLight = Color(0xFFFFF1EC)
val surfaceContainerLight = Color(0xFFFCEAE4)
val surfaceContainerHighLight = Color(0xFFF6E4DE)
val surfaceContainerHighestLight = Color(0xFFF1DFD9)

val primaryLightMediumContrast = Color(0xFF6C3218)
val onPrimaryLightMediumContrast = Color(0xFFFFFFFF)
val primaryContainerLightMediumContrast = Color(0xFFA96244)
val onPrimaryContainerLightMediumContrast = Color(0xFFFFFFFF)
val secondaryLightMediumContrast = Color(0xFF583C31)
val onSecondaryLightMediumContrast = Color(0xFFFFFFFF)
val secondaryContainerLightMediumContrast = Color(0xFF8F6D60)
val onSecondaryContainerLightMediumContrast = Color(0xFFFFFFFF)
val tertiaryLightMediumContrast = Color(0xFF4B4317)
val onTertiaryLightMediumContrast = Color(0xFFFFFFFF)
val tertiaryContainerLightMediumContrast = Color(0xFF7F7544)
val onTertiaryContainerLightMediumContrast = Color(0xFFFFFFFF)
val errorLightMediumContrast = Color(0xFF8C0009)
val onErrorLightMediumContrast = Color(0xFFFFFFFF)
val errorContainerLightMediumContrast = Color(0xFFDA342E)
val onErrorContainerLightMediumContrast = Color(0xFFFFFFFF)
val backgroundLightMediumContrast = Color(0xFFFFF8F6)
val onBackgroundLightMediumContrast = Color(0xFF231A16)
val surfaceLightMediumContrast = Color(0xFFFFF8F6)
val onSurfaceLightMediumContrast = Color(0xFF231A16)
val surfaceVariantLightMediumContrast = Color(0xFFF5DED6)
val onSurfaceVariantLightMediumContrast = Color(0xFF4E403A)
val outlineLightMediumContrast = Color(0xFF6C5B55)
val outlineVariantLightMediumContrast = Color(0xFF897770)
val scrimLightMediumContrast = Color(0xFF000000)
val inverseSurfaceLightMediumContrast = Color(0xFF382E2A)
val inverseOnSurfaceLightMediumContrast = Color(0xFFFFEDE7)
val inversePrimaryLightMediumContrast = Color(0xFFFFB598)
val surfaceDimLightMediumContrast = Color(0xFFE8D6D0)
val surfaceBrightLightMediumContrast = Color(0xFFFFF8F6)
val surfaceContainerLowestLightMediumContrast = Color(0xFFFFFFFF)
val surfaceContainerLowLightMediumContrast = Color(0xFFFFF1EC)
val surfaceContainerLightMediumContrast = Color(0xFFFCEAE4)
val surfaceContainerHighLightMediumContrast = Color(0xFFF6E4DE)
val surfaceContainerHighestLightMediumContrast = Color(0xFFF1DFD9)

val primaryLightHighContrast = Color(0xFF421300)
val onPrimaryLightHighContrast = Color(0xFFFFFFFF)
val primaryContainerLightHighContrast = Color(0xFF6C3218)
val onPrimaryContainerLightHighContrast = Color(0xFFFFFFFF)
val secondaryLightHighContrast = Color(0xFF341C13)
val onSecondaryLightHighContrast = Color(0xFFFFFFFF)
val secondaryContainerLightHighContrast = Color(0xFF583C31)
val onSecondaryContainerLightHighContrast = Color(0xFFFFFFFF)
val tertiaryLightHighContrast = Color(0xFF282200)
val onTertiaryLightHighContrast = Color(0xFFFFFFFF)
val tertiaryContainerLightHighContrast = Color(0xFF4B4317)
val onTertiaryContainerLightHighContrast = Color(0xFFFFFFFF)
val errorLightHighContrast = Color(0xFF4E0002)
val onErrorLightHighContrast = Color(0xFFFFFFFF)
val errorContainerLightHighContrast = Color(0xFF8C0009)
val onErrorContainerLightHighContrast = Color(0xFFFFFFFF)
val backgroundLightHighContrast = Color(0xFFFFF8F6)
val onBackgroundLightHighContrast = Color(0xFF231A16)
val surfaceLightHighContrast = Color(0xFFFFF8F6)
val onSurfaceLightHighContrast = Color(0xFF000000)
val surfaceVariantLightHighContrast = Color(0xFFF5DED6)
val onSurfaceVariantLightHighContrast = Color(0xFF2E211C)
val outlineLightHighContrast = Color(0xFF4E403A)
val outlineVariantLightHighContrast = Color(0xFF4E403A)
val scrimLightHighContrast = Color(0xFF000000)
val inverseSurfaceLightHighContrast = Color(0xFF382E2A)
val inverseOnSurfaceLightHighContrast = Color(0xFFFFFFFF)
val inversePrimaryLightHighContrast = Color(0xFFFFE7DF)
val surfaceDimLightHighContrast = Color(0xFFE8D6D0)
val surfaceBrightLightHighContrast = Color(0xFFFFF8F6)
val surfaceContainerLowestLightHighContrast = Color(0xFFFFFFFF)
val surfaceContainerLowLightHighContrast = Color(0xFFFFF1EC)
val surfaceContainerLightHighContrast = Color(0xFFFCEAE4)
val surfaceContainerHighLightHighContrast = Color(0xFFF6E4DE)
val surfaceContainerHighestLightHighContrast = Color(0xFFF1DFD9)

val primaryDark = Color(0xFFFFB598)
val onPrimaryDark = Color(0xFF552008)
val primaryContainerDark = Color(0xFF71361C)
val onPrimaryContainerDark = Color(0xFFFFDBCE)
val secondaryDark = Color(0xFFE7BEAE)
val onSecondaryDark = Color(0xFF442A20)
val secondaryContainerDark = Color(0xFF5D4035)
val onSecondaryContainerDark = Color(0xFFFFDBCE)
val tertiaryDark = Color(0xFFD3C78E)
val onTertiaryDark = Color(0xFF383006)
val tertiaryContainerDark = Color(0xFF4F471B)
val onTertiaryContainerDark = Color(0xFFF0E3A8)
val errorDark = Color(0xFFFFB4AB)
val onErrorDark = Color(0xFF690005)
val errorContainerDark = Color(0xFF93000A)
val onErrorContainerDark = Color(0xFFFFDAD6)
val backgroundDark = Color(0xFF1A110E)
val onBackgroundDark = Color(0xFFF1DFD9)
val surfaceDark = Color(0xFF1A110E)
val onSurfaceDark = Color(0xFFF1DFD9)
val surfaceVariantDark = Color(0xFF53433E)
val onSurfaceVariantDark = Color(0xFFD8C2BA)
val outlineDark = Color(0xFFA08D86)
val outlineVariantDark = Color(0xFF53433E)
val scrimDark = Color(0xFF000000)
val inverseSurfaceDark = Color(0xFFF1DFD9)
val inverseOnSurfaceDark = Color(0xFF382E2A)
val inversePrimaryDark = Color(0xFF8E4D31)
val surfaceDimDark = Color(0xFF1A110E)
val surfaceBrightDark = Color(0xFF423733)
val surfaceContainerLowestDark = Color(0xFF140C09)
val surfaceContainerLowDark = Color(0xFF231A16)
val surfaceContainerDark = Color(0xFF271E1A)
val surfaceContainerHighDark = Color(0xFF322824)
val surfaceContainerHighestDark = Color(0xFF3D322F)

val primaryDarkMediumContrast = Color(0xFFFFBBA0)
val onPrimaryDarkMediumContrast = Color(0xFF2E0B00)
val primaryContainerDarkMediumContrast = Color(0xFFCA7D5E)
val onPrimaryContainerDarkMediumContrast = Color(0xFF000000)
val secondaryDarkMediumContrast = Color(0xFFEBC2B2)
val onSecondaryDarkMediumContrast = Color(0xFF261108)
val secondaryContainerDarkMediumContrast = Color(0xFFAD897B)
val onSecondaryContainerDarkMediumContrast = Color(0xFF000000)
val tertiaryDarkMediumContrast = Color(0xFFD8CB92)
val onTertiaryDarkMediumContrast = Color(0xFF1B1600)
val tertiaryContainerDarkMediumContrast = Color(0xFF9C915D)
val onTertiaryContainerDarkMediumContrast = Color(0xFF000000)
val errorDarkMediumContrast = Color(0xFFFFBAB1)
val onErrorDarkMediumContrast = Color(0xFF370001)
val errorContainerDarkMediumContrast = Color(0xFFFF5449)
val onErrorContainerDarkMediumContrast = Color(0xFF000000)
val backgroundDarkMediumContrast = Color(0xFF1A110E)
val onBackgroundDarkMediumContrast = Color(0xFFF1DFD9)
val surfaceDarkMediumContrast = Color(0xFF1A110E)
val onSurfaceDarkMediumContrast = Color(0xFFFFF9F8)
val surfaceVariantDarkMediumContrast = Color(0xFF53433E)
val onSurfaceVariantDarkMediumContrast = Color(0xFFDCC6BE)
val outlineDarkMediumContrast = Color(0xFFB39F97)
val outlineVariantDarkMediumContrast = Color(0xFF927F78)
val scrimDarkMediumContrast = Color(0xFF000000)
val inverseSurfaceDarkMediumContrast = Color(0xFFF1DFD9)
val inverseOnSurfaceDarkMediumContrast = Color(0xFF322824)
val inversePrimaryDarkMediumContrast = Color(0xFF73371D)
val surfaceDimDarkMediumContrast = Color(0xFF1A110E)
val surfaceBrightDarkMediumContrast = Color(0xFF423733)
val surfaceContainerLowestDarkMediumContrast = Color(0xFF140C09)
val surfaceContainerLowDarkMediumContrast = Color(0xFF231A16)
val surfaceContainerDarkMediumContrast = Color(0xFF271E1A)
val surfaceContainerHighDarkMediumContrast = Color(0xFF322824)
val surfaceContainerHighestDarkMediumContrast = Color(0xFF3D322F)

val primaryDarkHighContrast = Color(0xFFFFF9F8)
val onPrimaryDarkHighContrast = Color(0xFF000000)
val primaryContainerDarkHighContrast = Color(0xFFFFBBA0)
val onPrimaryContainerDarkHighContrast = Color(0xFF000000)
val secondaryDarkHighContrast = Color(0xFFFFF9F8)
val onSecondaryDarkHighContrast = Color(0xFF000000)
val secondaryContainerDarkHighContrast = Color(0xFFEBC2B2)
val onSecondaryContainerDarkHighContrast = Color(0xFF000000)
val tertiaryDarkHighContrast = Color(0xFFFFFAF4)
val onTertiaryDarkHighContrast = Color(0xFF000000)
val tertiaryContainerDarkHighContrast = Color(0xFFD8CB92)
val onTertiaryContainerDarkHighContrast = Color(0xFF000000)
val errorDarkHighContrast = Color(0xFFFFF9F9)
val onErrorDarkHighContrast = Color(0xFF000000)
val errorContainerDarkHighContrast = Color(0xFFFFBAB1)
val onErrorContainerDarkHighContrast = Color(0xFF000000)
val backgroundDarkHighContrast = Color(0xFF1A110E)
val onBackgroundDarkHighContrast = Color(0xFFF1DFD9)
val surfaceDarkHighContrast = Color(0xFF1A110E)
val onSurfaceDarkHighContrast = Color(0xFFFFFFFF)
val surfaceVariantDarkHighContrast = Color(0xFF53433E)
val onSurfaceVariantDarkHighContrast = Color(0xFFFFF9F8)
val outlineDarkHighContrast = Color(0xFFDCC6BE)
val outlineVariantDarkHighContrast = Color(0xFFDCC6BE)
val scrimDarkHighContrast = Color(0xFF000000)
val inverseSurfaceDarkHighContrast = Color(0xFFF1DFD9)
val inverseOnSurfaceDarkHighContrast = Color(0xFF000000)
val inversePrimaryDarkHighContrast = Color(0xFF4C1A03)
val surfaceDimDarkHighContrast = Color(0xFF1A110E)
val surfaceBrightDarkHighContrast = Color(0xFF423733)
val surfaceContainerLowestDarkHighContrast = Color(0xFF140C09)
val surfaceContainerLowDarkHighContrast = Color(0xFF231A16)
val surfaceContainerDarkHighContrast = Color(0xFF271E1A)
val surfaceContainerHighDarkHighContrast = Color(0xFF322824)
val surfaceContainerHighestDarkHighContrast = Color(0xFF3D322F)

val greenLight = Color(0xFF49672E)
val onGreenLight = Color(0xFFFFFFFF)
val greenContainerLight = Color(0xFFCAEEA6)
val onGreenContainerLight = Color(0xFF0D2000)
val pinkLight = Color(0xFF8E4956)
val onPinkLight = Color(0xFFFFFFFF)
val pinkContainerLight = Color(0xFFFFD9DD)
val onPinkContainerLight = Color(0xFF3B0715)

val greenLightMediumContrast = Color(0xFF2E4A14)
val onGreenLightMediumContrast = Color(0xFFFFFFFF)
val greenContainerLightMediumContrast = Color(0xFF5F7D42)
val onGreenContainerLightMediumContrast = Color(0xFFFFFFFF)
val pinkLightMediumContrast = Color(0xFF6D2F3B)
val onPinkLightMediumContrast = Color(0xFFFFFFFF)
val pinkContainerLightMediumContrast = Color(0xFFA85F6B)
val onPinkContainerLightMediumContrast = Color(0xFFFFFFFF)

val greenLightHighContrast = Color(0xFF122700)
val onGreenLightHighContrast = Color(0xFFFFFFFF)
val greenContainerLightHighContrast = Color(0xFF2E4A14)
val onGreenContainerLightHighContrast = Color(0xFFFFFFFF)
val pinkLightHighContrast = Color(0xFF430E1B)
val onPinkLightHighContrast = Color(0xFFFFFFFF)
val pinkContainerLightHighContrast = Color(0xFF6D2F3B)
val onPinkContainerLightHighContrast = Color(0xFFFFFFFF)

val greenDark = Color(0xFFAFD18C)
val onGreenDark = Color(0xFF1D3703)
val greenContainerDark = Color(0xFF324E18)
val onGreenContainerDark = Color(0xFFCAEEA6)
val pinkDark = Color(0xFFFFB2BD)
val onPinkDark = Color(0xFF561D29)
val pinkContainerDark = Color(0xFF72333F)
val onPinkContainerDark = Color(0xFFFFD9DD)

val greenDarkMediumContrast = Color(0xFFB3D690)
val onGreenDarkMediumContrast = Color(0xFF0A1A00)
val greenContainerDarkMediumContrast = Color(0xFF7A9A5B)
val onGreenContainerDarkMediumContrast = Color(0xFF000000)
val pinkDarkMediumContrast = Color(0xFFFFB8C2)
val onPinkDarkMediumContrast = Color(0xFF330310)
val pinkContainerDarkMediumContrast = Color(0xFFC97A87)
val onPinkContainerDarkMediumContrast = Color(0xFF000000)

val greenDarkHighContrast = Color(0xFFF3FFE1)
val onGreenDarkHighContrast = Color(0xFF000000)
val greenContainerDarkHighContrast = Color(0xFFB3D690)
val onGreenContainerDarkHighContrast = Color(0xFF000000)
val pinkDarkHighContrast = Color(0xFFFFF9F9)
val onPinkDarkHighContrast = Color(0xFF000000)
val pinkContainerDarkHighContrast = Color(0xFFFFB8C2)
val onPinkContainerDarkHighContrast = Color(0xFF000000)

@Immutable
data class ExtendedColorScheme(
    val green: ColorFamily,
    val pink: ColorFamily,
)

val lightScheme = lightColorScheme(
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
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

val darkScheme = darkColorScheme(
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
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

private val mediumContrastLightColorScheme = lightColorScheme(
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
    surfaceDim = surfaceDimLightMediumContrast,
    surfaceBright = surfaceBrightLightMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestLightMediumContrast,
    surfaceContainerLow = surfaceContainerLowLightMediumContrast,
    surfaceContainer = surfaceContainerLightMediumContrast,
    surfaceContainerHigh = surfaceContainerHighLightMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestLightMediumContrast,
)

val highContrastLightColorScheme = lightColorScheme(
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
    surfaceDim = surfaceDimLightHighContrast,
    surfaceBright = surfaceBrightLightHighContrast,
    surfaceContainerLowest = surfaceContainerLowestLightHighContrast,
    surfaceContainerLow = surfaceContainerLowLightHighContrast,
    surfaceContainer = surfaceContainerLightHighContrast,
    surfaceContainerHigh = surfaceContainerHighLightHighContrast,
    surfaceContainerHighest = surfaceContainerHighestLightHighContrast,
)

private val mediumContrastDarkColorScheme = darkColorScheme(
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
    surfaceDim = surfaceDimDarkMediumContrast,
    surfaceBright = surfaceBrightDarkMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkMediumContrast,
    surfaceContainerLow = surfaceContainerLowDarkMediumContrast,
    surfaceContainer = surfaceContainerDarkMediumContrast,
    surfaceContainerHigh = surfaceContainerHighDarkMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkMediumContrast,
)

val highContrastDarkColorScheme = darkColorScheme(
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
    surfaceDim = surfaceDimDarkHighContrast,
    surfaceBright = surfaceBrightDarkHighContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkHighContrast,
    surfaceContainerLow = surfaceContainerLowDarkHighContrast,
    surfaceContainer = surfaceContainerDarkHighContrast,
    surfaceContainerHigh = surfaceContainerHighDarkHighContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkHighContrast,
)

val extendedLight = ExtendedColorScheme(
    green = ColorFamily(
        greenLight,
        onGreenLight,
        greenContainerLight,
        onGreenContainerLight,
    ),
    pink = ColorFamily(
        pinkLight,
        onPinkLight,
        pinkContainerLight,
        onPinkContainerLight,
    ),
)

val extendedDark = ExtendedColorScheme(
    green = ColorFamily(
        greenDark,
        onGreenDark,
        greenContainerDark,
        onGreenContainerDark,
    ),
    pink = ColorFamily(
        pinkDark,
        onPinkDark,
        pinkContainerDark,
        onPinkContainerDark,
    ),
)

val extendedLightMediumContrast = ExtendedColorScheme(
    green = ColorFamily(
        greenLightMediumContrast,
        onGreenLightMediumContrast,
        greenContainerLightMediumContrast,
        onGreenContainerLightMediumContrast,
    ),
    pink = ColorFamily(
        pinkLightMediumContrast,
        onPinkLightMediumContrast,
        pinkContainerLightMediumContrast,
        onPinkContainerLightMediumContrast,
    ),
)

val extendedLightHighContrast = ExtendedColorScheme(
    green = ColorFamily(
        greenLightHighContrast,
        onGreenLightHighContrast,
        greenContainerLightHighContrast,
        onGreenContainerLightHighContrast,
    ),
    pink = ColorFamily(
        pinkLightHighContrast,
        onPinkLightHighContrast,
        pinkContainerLightHighContrast,
        onPinkContainerLightHighContrast,
    ),
)

val extendedDarkMediumContrast = ExtendedColorScheme(
    green = ColorFamily(
        greenDarkMediumContrast,
        onGreenDarkMediumContrast,
        greenContainerDarkMediumContrast,
        onGreenContainerDarkMediumContrast,
    ),
    pink = ColorFamily(
        pinkDarkMediumContrast,
        onPinkDarkMediumContrast,
        pinkContainerDarkMediumContrast,
        onPinkContainerDarkMediumContrast,
    ),
)

val extendedDarkHighContrast = ExtendedColorScheme(
    green = ColorFamily(
        greenDarkHighContrast,
        onGreenDarkHighContrast,
        greenContainerDarkHighContrast,
        onGreenContainerDarkHighContrast,
    ),
    pink = ColorFamily(
        pinkDarkHighContrast,
        onPinkDarkHighContrast,
        pinkContainerDarkHighContrast,
        onPinkContainerDarkHighContrast,
    ),
)

@Immutable
data class ColorFamily(
    val color: Color,
    val onColor: Color,
    val colorContainer: Color,
    val onColorContainer: Color,
)

val unspecified_scheme = ColorFamily(
    Color.Unspecified,
    Color.Unspecified,
    Color.Unspecified,
    Color.Unspecified,
)

val LightAndroidGradientColors = GradientColors(container = primaryContainerLight)

/**
 * Dark Android gradient colors
 */
val DarkAndroidGradientColors = GradientColors(container = Color.Black)

/**
 * Light Android background theme
 */
val LightAndroidBackgroundTheme = BackgroundTheme(color = primaryContainerDark)

/**
 * Dark Android background theme
 */
val DarkAndroidBackgroundTheme = BackgroundTheme(color = Color.Black)
