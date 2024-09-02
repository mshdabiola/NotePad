package com.mshdabiola.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

sealed class MyColors {

    abstract val primaryLight: Color
    abstract val onPrimaryLight: Color
    abstract val primaryContainerLight: Color
    abstract val onPrimaryContainerLight: Color
    abstract val secondaryLight: Color
    abstract val onSecondaryLight: Color
    abstract val secondaryContainerLight: Color
    abstract val onSecondaryContainerLight: Color
    abstract val tertiaryLight: Color
    abstract val onTertiaryLight: Color
    abstract val tertiaryContainerLight: Color
    abstract val onTertiaryContainerLight: Color
    abstract val errorLight: Color
    abstract val onErrorLight: Color
    abstract val errorContainerLight: Color
    abstract val onErrorContainerLight: Color
    abstract val backgroundLight: Color
    abstract val onBackgroundLight: Color
    abstract val surfaceLight: Color
    abstract val onSurfaceLight: Color
    abstract val surfaceVariantLight: Color
    abstract val onSurfaceVariantLight: Color
    abstract val outlineLight: Color
    abstract val outlineVariantLight: Color
    abstract val scrimLight: Color
    abstract val inverseSurfaceLight: Color
    abstract val inverseOnSurfaceLight: Color
    abstract val inversePrimaryLight: Color
    abstract val surfaceDimLight: Color
    abstract val surfaceBrightLight: Color
    abstract val surfaceContainerLowestLight: Color
    abstract val surfaceContainerLowLight: Color
    abstract val surfaceContainerLight: Color
    abstract val surfaceContainerHighLight: Color
    abstract val surfaceContainerHighestLight: Color

    abstract val primaryDark: Color
    abstract val onPrimaryDark: Color
    abstract val primaryContainerDark: Color
    abstract val onPrimaryContainerDark: Color
    abstract val secondaryDark: Color
    abstract val onSecondaryDark: Color
    abstract val secondaryContainerDark: Color
    abstract val onSecondaryContainerDark: Color
    abstract val tertiaryDark: Color
    abstract val onTertiaryDark: Color
    abstract val tertiaryContainerDark: Color
    abstract val onTertiaryContainerDark: Color
    abstract val errorDark: Color
    abstract val onErrorDark: Color
    abstract val errorContainerDark: Color
    abstract val onErrorContainerDark: Color
    abstract val backgroundDark: Color
    abstract val onBackgroundDark: Color
    abstract val surfaceDark: Color
    abstract val onSurfaceDark: Color
    abstract val surfaceVariantDark: Color
    abstract val onSurfaceVariantDark: Color
    abstract val outlineDark: Color
    abstract val outlineVariantDark: Color
    abstract val scrimDark: Color
    abstract val inverseSurfaceDark: Color
    abstract val inverseOnSurfaceDark: Color
    abstract val inversePrimaryDark: Color
    abstract val surfaceDimDark: Color
    abstract val surfaceBrightDark: Color
    abstract val surfaceContainerLowestDark: Color
    abstract val surfaceContainerLowDark: Color
    abstract val surfaceContainerDark: Color
    abstract val surfaceContainerHighDark: Color
    abstract val surfaceContainerHighestDark: Color

    data object Brown : MyColors() {

        override val primaryLight = Color(0xFF8E4D31)
        override val onPrimaryLight = Color(0xFFFFFFFF)
        override val primaryContainerLight = Color(0xFFFFDBCE)
        override val onPrimaryContainerLight = Color(0xFF370E00)
        override val secondaryLight = Color(0xFF77574B)
        override val onSecondaryLight = Color(0xFFFFFFFF)
        override val secondaryContainerLight = Color(0xFFFFDBCE)
        override val onSecondaryContainerLight = Color(0xFF2C160C)
        override val tertiaryLight = Color(0xFF685F30)
        override val onTertiaryLight = Color(0xFFFFFFFF)
        override val tertiaryContainerLight = Color(0xFFF0E3A8)
        override val onTertiaryContainerLight = Color(0xFF211B00)
        override val errorLight = Color(0xFFBA1A1A)
        override val onErrorLight = Color(0xFFFFFFFF)
        override val errorContainerLight = Color(0xFFFFDAD6)
        override val onErrorContainerLight = Color(0xFF410002)
        override val backgroundLight = Color(0xFFFFF8F6)
        override val onBackgroundLight = Color(0xFF231A16)
        override val surfaceLight = Color(0xFFFFF8F6)
        override val onSurfaceLight = Color(0xFF231A16)
        override val surfaceVariantLight = Color(0xFFF5DED6)
        override val onSurfaceVariantLight = Color(0xFF53433E)
        override val outlineLight = Color(0xFF85736D)
        override val outlineVariantLight = Color(0xFFD8C2BA)
        override val scrimLight = Color(0xFF000000)
        override val inverseSurfaceLight = Color(0xFF382E2A)
        override val inverseOnSurfaceLight = Color(0xFFFFEDE7)
        override val inversePrimaryLight = Color(0xFFFFB598)
        override val surfaceDimLight = Color(0xFFE8D6D0)
        override val surfaceBrightLight = Color(0xFFFFF8F6)
        override val surfaceContainerLowestLight = Color(0xFFFFFFFF)
        override val surfaceContainerLowLight = Color(0xFFFFF1EC)
        override val surfaceContainerLight = Color(0xFFFCEAE4)
        override val surfaceContainerHighLight = Color(0xFFF6E4DE)
        override val surfaceContainerHighestLight = Color(0xFFF1DFD9)

        override val primaryDark = Color(0xFFFFB598)
        override val onPrimaryDark = Color(0xFF552008)
        override val primaryContainerDark = Color(0xFF71361C)
        override val onPrimaryContainerDark = Color(0xFFFFDBCE)
        override val secondaryDark = Color(0xFFE7BEAE)
        override val onSecondaryDark = Color(0xFF442A20)
        override val secondaryContainerDark = Color(0xFF5D4035)
        override val onSecondaryContainerDark = Color(0xFFFFDBCE)
        override val tertiaryDark = Color(0xFFD3C78E)
        override val onTertiaryDark = Color(0xFF383006)
        override val tertiaryContainerDark = Color(0xFF4F471B)
        override val onTertiaryContainerDark = Color(0xFFF0E3A8)
        override val errorDark = Color(0xFFFFB4AB)
        override val onErrorDark = Color(0xFF690005)
        override val errorContainerDark = Color(0xFF93000A)
        override val onErrorContainerDark = Color(0xFFFFDAD6)
        override val backgroundDark = Color(0xFF1A110E)
        override val onBackgroundDark = Color(0xFFF1DFD9)
        override val surfaceDark = Color(0xFF1A110E)
        override val onSurfaceDark = Color(0xFFF1DFD9)
        override val surfaceVariantDark = Color(0xFF53433E)
        override val onSurfaceVariantDark = Color(0xFFD8C2BA)
        override val outlineDark = Color(0xFFA08D86)
        override val outlineVariantDark = Color(0xFF53433E)
        override val scrimDark = Color(0xFF000000)
        override val inverseSurfaceDark = Color(0xFFF1DFD9)
        override val inverseOnSurfaceDark = Color(0xFF382E2A)
        override val inversePrimaryDark = Color(0xFF8E4D31)
        override val surfaceDimDark = Color(0xFF1A110E)
        override val surfaceBrightDark = Color(0xFF423733)
        override val surfaceContainerLowestDark = Color(0xFF140C09)
        override val surfaceContainerLowDark = Color(0xFF231A16)
        override val surfaceContainerDark = Color(0xFF271E1A)
        override val surfaceContainerHighDark = Color(0xFF322824)
        override val surfaceContainerHighestDark = Color(0xFF3D322F)
    }

    data object Default : MyColors() {

        override val primaryLight = Color(0xFF8D4A5C)
        override val onPrimaryLight = Color(0xFFFFFFFF)
        override val primaryContainerLight = Color(0xFFFFD9E0)
        override val onPrimaryContainerLight = Color(0xFF3A071A)
        override val secondaryLight = Color(0xFF75565D)
        override val onSecondaryLight = Color(0xFFFFFFFF)
        override val secondaryContainerLight = Color(0xFFFFD9E0)
        override val onSecondaryContainerLight = Color(0xFF2B151B)
        override val tertiaryLight = Color(0xFF7B5733)
        override val onTertiaryLight = Color(0xFFFFFFFF)
        override val tertiaryContainerLight = Color(0xFFFFDCBE)
        override val onTertiaryContainerLight = Color(0xFF2D1600)
        override val errorLight = Color(0xFFBA1A1A)
        override val onErrorLight = Color(0xFFFFFFFF)
        override val errorContainerLight = Color(0xFFFFDAD6)
        override val onErrorContainerLight = Color(0xFF410002)
        override val backgroundLight = Color(0xFFFFF8F7)
        override val onBackgroundLight = Color(0xFF22191B)
        override val surfaceLight = Color(0xFFFFF8F7)
        override val onSurfaceLight = Color(0xFF22191B)
        override val surfaceVariantLight = Color(0xFFF3DDE0)
        override val onSurfaceVariantLight = Color(0xFF514346)
        override val outlineLight = Color(0xFF847376)
        override val outlineVariantLight = Color(0xFFD6C2C5)
        override val scrimLight = Color(0xFF000000)
        override val inverseSurfaceLight = Color(0xFF382E30)
        override val inverseOnSurfaceLight = Color(0xFFFEEDEF)
        override val inversePrimaryLight = Color(0xFFFFB1C3)
        override val surfaceDimLight = Color(0xFFE7D6D8)
        override val surfaceBrightLight = Color(0xFFFFF8F7)
        override val surfaceContainerLowestLight = Color(0xFFFFFFFF)
        override val surfaceContainerLowLight = Color(0xFFFFF0F2)
        override val surfaceContainerLight = Color(0xFFFBEAEC)
        override val surfaceContainerHighLight = Color(0xFFF5E4E6)
        override val surfaceContainerHighestLight = Color(0xFFEFDEE1)

        override val primaryDark = Color(0xFFFFB1C3)
        override val onPrimaryDark = Color(0xFF551D2E)
        override val primaryContainerDark = Color(0xFF713344)
        override val onPrimaryContainerDark = Color(0xFFFFD9E0)
        override val secondaryDark = Color(0xFFE4BDC4)
        override val onSecondaryDark = Color(0xFF43292F)
        override val secondaryContainerDark = Color(0xFF5B3F45)
        override val onSecondaryContainerDark = Color(0xFFFFD9E0)
        override val tertiaryDark = Color(0xFFEDBE92)
        override val onTertiaryDark = Color(0xFF472A09)
        override val tertiaryContainerDark = Color(0xFF60401E)
        override val onTertiaryContainerDark = Color(0xFFFFDCBE)
        override val errorDark = Color(0xFFFFB4AB)
        override val onErrorDark = Color(0xFF690005)
        override val errorContainerDark = Color(0xFF93000A)
        override val onErrorContainerDark = Color(0xFFFFDAD6)
        override val backgroundDark = Color(0xFF191113)
        override val onBackgroundDark = Color(0xFFEFDEE1)
        override val surfaceDark = Color(0xFF191113)
        override val onSurfaceDark = Color(0xFFEFDEE1)
        override val surfaceVariantDark = Color(0xFF514346)
        override val onSurfaceVariantDark = Color(0xFFD6C2C5)
        override val outlineDark = Color(0xFF9E8C8F)
        override val outlineVariantDark = Color(0xFF514346)
        override val scrimDark = Color(0xFF000000)
        override val inverseSurfaceDark = Color(0xFFEFDEE1)
        override val inverseOnSurfaceDark = Color(0xFF382E30)
        override val inversePrimaryDark = Color(0xFF8D4A5C)
        override val surfaceDimDark = Color(0xFF191113)
        override val surfaceBrightDark = Color(0xFF413738)
        override val surfaceContainerLowestDark = Color(0xFF140C0E)
        override val surfaceContainerLowDark = Color(0xFF22191B)
        override val surfaceContainerDark = Color(0xFF261D1F)
        override val surfaceContainerHighDark = Color(0xFF312829)
        override val surfaceContainerHighestDark = Color(0xFF3C3234)
    }
}

val playLight = Color(0xFF006A65)
val onPlayLight = Color(0xFFFFFFFF)
val playContainerLight = Color(0xFF9DF2EA)
val onPlayContainerLight = Color(0xFF00201E)
val pauseLight = Color(0xFF2B638B)
val onPauseLight = Color(0xFFFFFFFF)
val pauseContainerLight = Color(0xFFCCE5FF)
val onPauseContainerLight = Color(0xFF001E31)

val playDark = Color(0xFF81D5CE)
val onPlayDark = Color(0xFF003734)
val playContainerDark = Color(0xFF00504C)
val onPlayContainerDark = Color(0xFF9DF2EA)
val pauseDark = Color(0xFF98CCF9)
val onPauseDark = Color(0xFF003350)
val pauseContainerDark = Color(0xFF044B71)
val onPauseContainerDark = Color(0xFFCCE5FF)

val extendedLight = ExtendedColorScheme(
    play = ColorFamily(
        playLight,
        onPlayLight,
        playContainerLight,
        onPlayContainerLight,
    ),
    pause = ColorFamily(
        pauseLight,
        onPauseLight,
        pauseContainerLight,
        onPauseContainerLight,
    ),
)

val extendedDark = ExtendedColorScheme(
    play = ColorFamily(
        playDark,
        onPlayDark,
        playContainerDark,
        onPlayContainerDark,
    ),
    pause = ColorFamily(
        pauseDark,
        onPauseDark,
        pauseContainerDark,
        onPauseContainerDark,
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

@Immutable
data class ExtendedColorScheme(
    val play: ColorFamily,
    val pause: ColorFamily,
)
