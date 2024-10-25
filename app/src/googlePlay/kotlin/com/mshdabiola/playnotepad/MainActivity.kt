package com.mshdabiola.playnotepad

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.mshdabiola.analytics.AnalyticsHelper
import com.mshdabiola.analytics.LocalAnalyticsHelper
import com.mshdabiola.data.util.NetworkMonitor
import com.mshdabiola.designsystem.theme.SkTheme
import com.mshdabiola.model.DarkThemeConfig
import com.mshdabiola.model.ThemeBrand
import com.mshdabiola.playnotepad.ui.NoteApp
import com.mshdabiola.playnotepad.ui.rememberNoteAppState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var analytics: FirebaseAnalytics? = null
    private var remoteConfig: FirebaseRemoteConfig? = null

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    private val viewModel: MainActivityViewModel by viewModels()

    @Inject
    lateinit var analyticsHelper: AnalyticsHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        remoteConfig = Firebase.remoteConfig
        analytics = FirebaseAnalytics.getInstance(this@MainActivity)

        remoteConfig?.setDefaultsAsync(R.xml.remote_config_defaults)
//        remoteConfig?.setConfigSettingsAsync(remoteConfigSettings {
//            minimumFetchIntervalInSeconds = 3600
//        })
        remoteConfig?.fetchAndActivate()

        var uiState: MainActivityUiState by mutableStateOf(MainActivityUiState.Loading)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState
                    .onEach { uiState = it }
                    .collect()
            }
        }
        enableEdgeToEdge()

//        remoteConfig?.addOnConfigUpdateListener(object :ConfigUpdateListener{
//            override fun onUpdate(configUpdate: ConfigUpdate) {
//
//            }
//
//            override fun onError(error: FirebaseRemoteConfigException) {
//                TODO("Not yet implemented")
//            }
//
//        })

//        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
//            if (!task.isSuccessful) {
//               // Timber.e("Fetching FCM registration token failed", task.exception)
//                return@OnCompleteListener
//            }
//
//            // Get new FCM registration token
//            val token = task.result
//
//            analytics?.setUserProperty("messageToken",token)
//            // Log and toast
//            Timber.e("token $token")
//        })

        setContent {
            val darkTheme = shouldUseDarkTheme(uiState)

            // Update the edge to edge configuration to match the theme
            // This is the same parameters as the default enableEdgeToEdge call, but we manually
            // resolve whether or not to show dark theme using uiState, since it can be different
            // than the configuration's dark theme value based on the user preference.
            DisposableEffect(darkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        Color.TRANSPARENT,
                        Color.TRANSPARENT,
                    ) { darkTheme },
                    navigationBarStyle = SystemBarStyle.auto(
                        Color.TRANSPARENT,
                        Color.TRANSPARENT,
                    ) { darkTheme },
                )
                onDispose {}
            }

            val appState = rememberNoteAppState(
                networkMonitor = networkMonitor,
            )

            CompositionLocalProvider(LocalAnalyticsHelper provides analyticsHelper) {
                SkTheme(
                    darkTheme = darkTheme,
                    disableDynamicTheming = shouldDisableDynamicTheming(uiState),
                ) {
                    NoteApp(appState = appState, viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
private fun chooseTheme(
    uiState: MainActivityUiState,
): ThemeBrand = when (uiState) {
    MainActivityUiState.Loading -> ThemeBrand.DEFAULT
    is MainActivityUiState.Success -> uiState.userData.themeBrand
}

@Composable
private fun shouldUseAndroidTheme(
    uiState: MainActivityUiState,
): Boolean = when (uiState) {
    MainActivityUiState.Loading -> false
    is MainActivityUiState.Success -> when (uiState.userData.themeBrand) {
        ThemeBrand.DEFAULT -> false
        ThemeBrand.PINK -> true
    }
}

@Composable
private fun shouldDisableDynamicTheming(
    uiState: MainActivityUiState,
): Boolean = when (uiState) {
    MainActivityUiState.Loading -> false
    is MainActivityUiState.Success -> !uiState.userData.useDynamicColor
}

@Composable
private fun shouldUseDarkTheme(
    uiState: MainActivityUiState,
): Boolean = when (uiState) {
    MainActivityUiState.Loading -> isSystemInDarkTheme()
    is MainActivityUiState.Success -> when (uiState.userData.darkThemeConfig) {
        DarkThemeConfig.FOLLOW_SYSTEM -> isSystemInDarkTheme()
        DarkThemeConfig.LIGHT -> false
        DarkThemeConfig.DARK -> true
    }
}

private val lightScrim = Color.argb(0xe6, 0xFF, 0xFF, 0xFF)
private val darkScrim = Color.argb(0x80, 0x1b, 0x1b, 0x1b)
