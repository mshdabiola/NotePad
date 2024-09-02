/*
 *abiola 2022
 */

package com.mshdabiola.designsystem.theme

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

var extendedColorScheme: ExtendedColorScheme = extendedLight

@Composable
fun SkTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    androidTheme: Boolean = false,
    disableDynamicTheming: Boolean = true,
    content: @Composable () -> Unit,
) {
    extendedColorScheme = if (darkTheme) extendedDark else extendedLight
    val colorScheme = when {
        androidTheme -> if (darkTheme) darkBrownScheme else lightBrownScheme
        !disableDynamicTheming && supportsDynamicTheming() -> {
            getDynamicColor(darkTheme)
        }

        else -> if (darkTheme) darkDefaultScheme else lightDefaultScheme
    }
//

    // Gradient colors
    val emptyGradientColors = GradientColors(container = colorScheme.surfaceColorAtElevation(2.dp))
    val defaultGradientColors = GradientColors(
        top = colorScheme.inverseOnSurface,
        bottom = colorScheme.primaryContainer,
        container = colorScheme.surface,
    )
    val gradientColors = when {
        !disableDynamicTheming && supportsDynamicTheming() -> emptyGradientColors
        else -> defaultGradientColors
    }
    // Background theme
    val defaultBackgroundTheme = BackgroundTheme(
        color = colorScheme.surface,
        tonalElevation = 2.dp,
    )

    val tintTheme = when {
        androidTheme -> TintTheme()
        !disableDynamicTheming && supportsDynamicTheming() -> TintTheme(colorScheme.primary)
        else -> TintTheme()
    }
    // Composition locals
    CompositionLocalProvider(
        LocalGradientColors provides gradientColors,
        LocalBackgroundTheme provides defaultBackgroundTheme,
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

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun getDynamicColor(darkTheme: Boolean): ColorScheme {
    val context = LocalContext.current
    return if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
}

val defaultColor = MyColors.Default
val lightDefaultScheme = lightColorScheme(
    primary = defaultColor.primaryLight,
    onPrimary = defaultColor.onPrimaryLight,
    primaryContainer = defaultColor.primaryContainerLight,
    onPrimaryContainer = defaultColor.onPrimaryContainerLight,
    secondary = defaultColor.secondaryLight,
    onSecondary = defaultColor.onSecondaryLight,
    secondaryContainer = defaultColor.secondaryContainerLight,
    onSecondaryContainer = defaultColor.onSecondaryContainerLight,
    tertiary = defaultColor.tertiaryLight,
    onTertiary = defaultColor.onTertiaryLight,
    tertiaryContainer = defaultColor.tertiaryContainerLight,
    onTertiaryContainer = defaultColor.onTertiaryContainerLight,
    error = defaultColor.errorLight,
    onError = defaultColor.onErrorLight,
    errorContainer = defaultColor.errorContainerLight,
    onErrorContainer = defaultColor.onErrorContainerLight,
    background = defaultColor.backgroundLight,
    onBackground = defaultColor.onBackgroundLight,
    surface = defaultColor.surfaceLight,
    onSurface = defaultColor.onSurfaceLight,
    surfaceVariant = defaultColor.surfaceVariantLight,
    onSurfaceVariant = defaultColor.onSurfaceVariantLight,
    outline = defaultColor.outlineLight,
    outlineVariant = defaultColor.outlineVariantLight,
    scrim = defaultColor.scrimLight,
    inverseSurface = defaultColor.inverseSurfaceLight,
    inverseOnSurface = defaultColor.inverseOnSurfaceLight,
    inversePrimary = defaultColor.inversePrimaryLight,
    surfaceDim = defaultColor.surfaceDimLight,
    surfaceBright = defaultColor.surfaceBrightLight,
    surfaceContainerLowest = defaultColor.surfaceContainerLowestLight,
    surfaceContainerLow = defaultColor.surfaceContainerLowLight,
    surfaceContainer = defaultColor.surfaceContainerLight,
    surfaceContainerHigh = defaultColor.surfaceContainerHighLight,
    surfaceContainerHighest = defaultColor.surfaceContainerHighestLight,
)

val darkDefaultScheme = darkColorScheme(
    primary = defaultColor.primaryDark,
    onPrimary = defaultColor.onPrimaryDark,
    primaryContainer = defaultColor.primaryContainerDark,
    onPrimaryContainer = defaultColor.onPrimaryContainerDark,
    secondary = defaultColor.secondaryDark,
    onSecondary = defaultColor.onSecondaryDark,
    secondaryContainer = defaultColor.secondaryContainerDark,
    onSecondaryContainer = defaultColor.onSecondaryContainerDark,
    tertiary = defaultColor.tertiaryDark,
    onTertiary = defaultColor.onTertiaryDark,
    tertiaryContainer = defaultColor.tertiaryContainerDark,
    onTertiaryContainer = defaultColor.onTertiaryContainerDark,
    error = defaultColor.errorDark,
    onError = defaultColor.onErrorDark,
    errorContainer = defaultColor.errorContainerDark,
    onErrorContainer = defaultColor.onErrorContainerDark,
    background = defaultColor.backgroundDark,
    onBackground = defaultColor.onBackgroundDark,
    surface = defaultColor.surfaceDark,
    onSurface = defaultColor.onSurfaceDark,
    surfaceVariant = defaultColor.surfaceVariantDark,
    onSurfaceVariant = defaultColor.onSurfaceVariantDark,
    outline = defaultColor.outlineDark,
    outlineVariant = defaultColor.outlineVariantDark,
    scrim = defaultColor.scrimDark,
    inverseSurface = defaultColor.inverseSurfaceDark,
    inverseOnSurface = defaultColor.inverseOnSurfaceDark,
    inversePrimary = defaultColor.inversePrimaryDark,
    surfaceDim = defaultColor.surfaceDimDark,
    surfaceBright = defaultColor.surfaceBrightDark,
    surfaceContainerLowest = defaultColor.surfaceContainerLowestDark,
    surfaceContainerLow = defaultColor.surfaceContainerLowDark,
    surfaceContainer = defaultColor.surfaceContainerDark,
    surfaceContainerHigh = defaultColor.surfaceContainerHighDark,
    surfaceContainerHighest = defaultColor.surfaceContainerHighestDark,
)

val brownColor = MyColors.Brown
val lightBrownScheme = lightColorScheme(
    primary = brownColor.primaryLight,
    onPrimary = brownColor.onPrimaryLight,
    primaryContainer = brownColor.primaryContainerLight,
    onPrimaryContainer = brownColor.onPrimaryContainerLight,
    secondary = brownColor.secondaryLight,
    onSecondary = brownColor.onSecondaryLight,
    secondaryContainer = brownColor.secondaryContainerLight,
    onSecondaryContainer = brownColor.onSecondaryContainerLight,
    tertiary = brownColor.tertiaryLight,
    onTertiary = brownColor.onTertiaryLight,
    tertiaryContainer = brownColor.tertiaryContainerLight,
    onTertiaryContainer = brownColor.onTertiaryContainerLight,
    error = brownColor.errorLight,
    onError = brownColor.onErrorLight,
    errorContainer = brownColor.errorContainerLight,
    onErrorContainer = brownColor.onErrorContainerLight,
    background = brownColor.backgroundLight,
    onBackground = brownColor.onBackgroundLight,
    surface = brownColor.surfaceLight,
    onSurface = brownColor.onSurfaceLight,
    surfaceVariant = brownColor.surfaceVariantLight,
    onSurfaceVariant = brownColor.onSurfaceVariantLight,
    outline = brownColor.outlineLight,
    outlineVariant = brownColor.outlineVariantLight,
    scrim = brownColor.scrimLight,
    inverseSurface = brownColor.inverseSurfaceLight,
    inverseOnSurface = brownColor.inverseOnSurfaceLight,
    inversePrimary = brownColor.inversePrimaryLight,
    surfaceDim = brownColor.surfaceDimLight,
    surfaceBright = brownColor.surfaceBrightLight,
    surfaceContainerLowest = brownColor.surfaceContainerLowestLight,
    surfaceContainerLow = brownColor.surfaceContainerLowLight,
    surfaceContainer = brownColor.surfaceContainerLight,
    surfaceContainerHigh = brownColor.surfaceContainerHighLight,
    surfaceContainerHighest = brownColor.surfaceContainerHighestLight,
)

val darkBrownScheme = darkColorScheme(
    primary = brownColor.primaryDark,
    onPrimary = brownColor.onPrimaryDark,
    primaryContainer = brownColor.primaryContainerDark,
    onPrimaryContainer = brownColor.onPrimaryContainerDark,
    secondary = brownColor.secondaryDark,
    onSecondary = brownColor.onSecondaryDark,
    secondaryContainer = brownColor.secondaryContainerDark,
    onSecondaryContainer = brownColor.onSecondaryContainerDark,
    tertiary = brownColor.tertiaryDark,
    onTertiary = brownColor.onTertiaryDark,
    tertiaryContainer = brownColor.tertiaryContainerDark,
    onTertiaryContainer = brownColor.onTertiaryContainerDark,
    error = brownColor.errorDark,
    onError = brownColor.onErrorDark,
    errorContainer = brownColor.errorContainerDark,
    onErrorContainer = brownColor.onErrorContainerDark,
    background = brownColor.backgroundDark,
    onBackground = brownColor.onBackgroundDark,
    surface = brownColor.surfaceDark,
    onSurface = brownColor.onSurfaceDark,
    surfaceVariant = brownColor.surfaceVariantDark,
    onSurfaceVariant = brownColor.onSurfaceVariantDark,
    outline = brownColor.outlineDark,
    outlineVariant = brownColor.outlineVariantDark,
    scrim = brownColor.scrimDark,
    inverseSurface = brownColor.inverseSurfaceDark,
    inverseOnSurface = brownColor.inverseOnSurfaceDark,
    inversePrimary = brownColor.inversePrimaryDark,
    surfaceDim = brownColor.surfaceDimDark,
    surfaceBright = brownColor.surfaceBrightDark,
    surfaceContainerLowest = brownColor.surfaceContainerLowestDark,
    surfaceContainerLow = brownColor.surfaceContainerLowDark,
    surfaceContainer = brownColor.surfaceContainerDark,
    surfaceContainerHigh = brownColor.surfaceContainerHighDark,
    surfaceContainerHighest = brownColor.surfaceContainerHighestDark,
)
