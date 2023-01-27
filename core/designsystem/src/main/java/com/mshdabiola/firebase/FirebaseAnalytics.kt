package com.mshdabiola.firebase

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics

@SuppressLint("MissingPermission")
@Composable
fun FirebaseScreenLog(screen: String) {
    val context = LocalContext.current
    LaunchedEffect(key1 = Unit, block = {
        FirebaseAnalytics.getInstance(context)
            .logEvent(
                FirebaseAnalytics.Event.SCREEN_VIEW,
                bundleOf(
                    FirebaseAnalytics.Param.SCREEN_NAME to screen,
                    FirebaseAnalytics.Param.SCREEN_CLASS to screen,
                ),
            )
    })
}
