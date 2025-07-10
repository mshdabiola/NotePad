/*
 *abiola 2024
 */

package com.mshdabiola.model

import kotlinx.serialization.Serializable

/**
 * Class summarizing user interest data
 */
@Serializable
data class UserData(
    val themeBrand: ThemeBrand,
    val darkThemeConfig: DarkThemeConfig,
    val useDynamicColor: Boolean,
    val shouldHideOnboarding: Boolean,
    val contrast: Contrast,
    val noteDisplayCategory: NoteDisplayCategory,
    val isGrid: Boolean,
)
