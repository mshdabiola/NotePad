/*
 *abiola 2024
 */

package com.mshdabiola.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.mshdabiola.analytics.AnalyticsEvent
import com.mshdabiola.analytics.AnalyticsEvent.Param
import com.mshdabiola.analytics.AnalyticsEvent.ParamKeys
import com.mshdabiola.analytics.AnalyticsEvent.Types
import com.mshdabiola.analytics.AnalyticsHelper
import com.mshdabiola.analytics.LocalAnalyticsHelper

/**
 * Classes and functions associated with analytics events for the UI.
 */
fun AnalyticsHelper.logScreenView(screenName: String) {
    logEvent(
        AnalyticsEvent(
            type = Types.SCREEN_VIEW,
            extras = listOf(
                Param(ParamKeys.SCREEN_NAME, screenName),
            ),
        ),
    )
}

fun AnalyticsHelper.logNoteOpened(newsResourceId: String) {
    logEvent(
        event = AnalyticsEvent(
            type = "open_opened",
            extras = listOf(
                Param("open_opened", newsResourceId),
            ),
        ),
    )
}

/**
 * A side-effect which records a screen view event.
 */
@Composable
fun TrackScreenViewEvent(
    screenName: String,
    analyticsHelper: AnalyticsHelper = LocalAnalyticsHelper.current,
) = DisposableEffect(Unit) {
    analyticsHelper.logScreenView(screenName)
    onDispose {}
}
