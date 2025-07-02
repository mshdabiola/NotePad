package com.mshdabiola.about.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import com.mshdabiola.about.AboutScreen
import com.mshdabiola.ui.FirebaseScreenLog
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun EntryProviderBuilder<NavKey>.aboutScreen(onBack: () -> Unit) {
    entry<AboutArg> {
        val context = LocalContext.current
        var lastUpdate by remember {
            mutableStateOf("")
        }
        var version by remember {
            mutableStateOf("")
        }
        FirebaseScreenLog(screen = "about_screen")
        LaunchedEffect(
            key1 = Unit,
            block = {
                val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                val datetime = Instant.fromEpochMilliseconds(pInfo.lastUpdateTime)
                    .toLocalDateTime(TimeZone.currentSystemDefault()).date
                lastUpdate = "${datetime.dayOfMonth} ${
                    datetime.month.name.lowercase().replaceFirstChar { it.uppercaseChar() }
                } ${datetime.year}"
                version = pInfo?.versionName ?: "0.0.0"
            },
        )

        AboutScreen(
            onBack = onBack,
            lastUpdate = lastUpdate,
            version = version,
        )
    }
}

fun NavBackStack.navigateToAbout() {
    add(AboutArg)
}
