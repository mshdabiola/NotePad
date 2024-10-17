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
        androidTheme -> if (darkTheme) darkPinkScheme else lightPinkScheme
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

val pinkColor = MyColors.Pink
val lightPinkScheme = lightColorScheme(
    primary = pinkColor.primaryLight,
    onPrimary = pinkColor.onPrimaryLight,
    primaryContainer = pinkColor.primaryContainerLight,
    onPrimaryContainer = pinkColor.onPrimaryContainerLight,
    secondary = pinkColor.secondaryLight,
    onSecondary = pinkColor.onSecondaryLight,
    secondaryContainer = pinkColor.secondaryContainerLight,
    onSecondaryContainer = pinkColor.onSecondaryContainerLight,
    tertiary = pinkColor.tertiaryLight,
    onTertiary = pinkColor.onTertiaryLight,
    tertiaryContainer = pinkColor.tertiaryContainerLight,
    onTertiaryContainer = pinkColor.onTertiaryContainerLight,
    error = pinkColor.errorLight,
    onError = pinkColor.onErrorLight,
    errorContainer = pinkColor.errorContainerLight,
    onErrorContainer = pinkColor.onErrorContainerLight,
    background = pinkColor.backgroundLight,
    onBackground = pinkColor.onBackgroundLight,
    surface = pinkColor.surfaceLight,
    onSurface = pinkColor.onSurfaceLight,
    surfaceVariant = pinkColor.surfaceVariantLight,
    onSurfaceVariant = pinkColor.onSurfaceVariantLight,
    outline = pinkColor.outlineLight,
    outlineVariant = pinkColor.outlineVariantLight,
    scrim = pinkColor.scrimLight,
    inverseSurface = pinkColor.inverseSurfaceLight,
    inverseOnSurface = pinkColor.inverseOnSurfaceLight,
    inversePrimary = pinkColor.inversePrimaryLight,
    surfaceDim = pinkColor.surfaceDimLight,
    surfaceBright = pinkColor.surfaceBrightLight,
    surfaceContainerLowest = pinkColor.surfaceContainerLowestLight,
    surfaceContainerLow = pinkColor.surfaceContainerLowLight,
    surfaceContainer = pinkColor.surfaceContainerLight,
    surfaceContainerHigh = pinkColor.surfaceContainerHighLight,
    surfaceContainerHighest = pinkColor.surfaceContainerHighestLight,
)

val darkPinkScheme = darkColorScheme(
    primary = pinkColor.primaryDark,
    onPrimary = pinkColor.onPrimaryDark,
    primaryContainer = pinkColor.primaryContainerDark,
    onPrimaryContainer = pinkColor.onPrimaryContainerDark,
    secondary = pinkColor.secondaryDark,
    onSecondary = pinkColor.onSecondaryDark,
    secondaryContainer = pinkColor.secondaryContainerDark,
    onSecondaryContainer = pinkColor.onSecondaryContainerDark,
    tertiary = pinkColor.tertiaryDark,
    onTertiary = pinkColor.onTertiaryDark,
    tertiaryContainer = pinkColor.tertiaryContainerDark,
    onTertiaryContainer = pinkColor.onTertiaryContainerDark,
    error = pinkColor.errorDark,
    onError = pinkColor.onErrorDark,
    errorContainer = pinkColor.errorContainerDark,
    onErrorContainer = pinkColor.onErrorContainerDark,
    background = pinkColor.backgroundDark,
    onBackground = pinkColor.onBackgroundDark,
    surface = pinkColor.surfaceDark,
    onSurface = pinkColor.onSurfaceDark,
    surfaceVariant = pinkColor.surfaceVariantDark,
    onSurfaceVariant = pinkColor.onSurfaceVariantDark,
    outline = pinkColor.outlineDark,
    outlineVariant = pinkColor.outlineVariantDark,
    scrim = pinkColor.scrimDark,
    inverseSurface = pinkColor.inverseSurfaceDark,
    inverseOnSurface = pinkColor.inverseOnSurfaceDark,
    inversePrimary = pinkColor.inversePrimaryDark,
    surfaceDim = pinkColor.surfaceDimDark,
    surfaceBright = pinkColor.surfaceBrightDark,
    surfaceContainerLowest = pinkColor.surfaceContainerLowestDark,
    surfaceContainerLow = pinkColor.surfaceContainerLowDark,
    surfaceContainer = pinkColor.surfaceContainerDark,
    surfaceContainerHigh = pinkColor.surfaceContainerHighDark,
    surfaceContainerHighest = pinkColor.surfaceContainerHighestDark,
)

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
