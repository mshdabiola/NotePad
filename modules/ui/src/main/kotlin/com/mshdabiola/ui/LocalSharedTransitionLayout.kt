package com.mshdabiola.ui

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation3.ui.LocalNavAnimatedContentScope

@OptIn(ExperimentalSharedTransitionApi::class)
public val LocalSharedStScope: ProvidableCompositionLocal<SharedTransitionScope> =
    compositionLocalOf<SharedTransitionScope> {
        throw IllegalStateException(
            "Not declare",
        )
    }

@SuppressLint("UnusedSharedTransitionModifierParameter")
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PreviewContainer(
    content: @Composable () -> Unit,
) {
    SharedTransitionScope {
        AnimatedContent(true) {
            CompositionLocalProvider(
                LocalNavAnimatedContentScope provides this,
                LocalSharedStScope provides this@SharedTransitionScope,
            ) {
                if (it) {
                    content()
                }
            }
        }
    }
}
