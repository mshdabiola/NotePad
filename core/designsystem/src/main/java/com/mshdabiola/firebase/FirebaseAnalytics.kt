package com.mshdabiola.firebase

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.analytics.FirebaseAnalytics

@SuppressLint("MissingPermission")
@Composable
fun FirebaseScreenLog(screen: String) {
    val context = LocalContext.current
    LaunchedEffect(key1 = Unit, block = {
        FirebaseAnalytics.getInstance(context)
            .logEvent(
                FirebaseAnalytics.Event.SCREEN_VIEW,
                Bundle().apply {
                    putString(FirebaseAnalytics.Param.SCREEN_NAME,screen)
                    putString(FirebaseAnalytics.Param.SCREEN_CLASS,screen)
                }
            )
    })
}
