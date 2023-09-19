package com.mshdabiola.playnotepad

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.google.android.gms.games.AchievementsClient
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.mshdabiola.designsystem.theme.NotePadAppTheme
import com.mshdabiola.playnotepad.ui.NotePadApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var show by mutableStateOf(false)
    private val appUpdateInfoManager by lazy { AppUpdateManagerFactory.create(this) }
    private var listener: InstallStateUpdatedListener? = null
    private var analytics: FirebaseAnalytics? = null
    private var remoteConfig: FirebaseRemoteConfig? = null

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
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
            WindowCompat.setDecorFitsSystemWindows(window, false)
            NotePadAppTheme {
                Box {


                    // A surface container using the 'background' color from the theme
                    NotePadApp(windowSizeClass = calculateWindowSizeClass(activity = this@MainActivity))

                    if (show) {
                        Snackbar(
                            modifier = Modifier
                                .navigationBarsPadding()
                                .padding(horizontal = 4.dp)
                                .align(Alignment.BottomCenter),
                            action = {
                                Button(onClick = {
                                    appUpdateInfoManager.completeUpdate()
                                    show = false
                                }) {
                                    Text(text = "Reload")
                                }
                            }
                        ) {
                            Text(text = "Play note just download an update")
                        }
                    }

                }

            }
        }
    }

    override fun onResume() {
        super.onResume()

        appUpdateInfoManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    show = true
                }
                if (appUpdateInfo.installStatus() == InstallStatus.INSTALLED) {
                    listener?.let { appUpdateInfoManager.unregisterListener(it) }
                }
            }
    }
    override fun onStart() {
        super.onStart()

        val appUpdateInfoTask = appUpdateInfoManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {

                listener = InstallStateUpdatedListener { state ->

//                    if (state.installStatus() == InstallStatus.DOWNLOADING) {
//                        val bytesDownloaded = state.bytesDownloaded()
//                        val totalBytesToDownload = state.totalBytesToDownload()
//                        // Show update progress bar.
//                    }
                    if (state.installStatus() == InstallStatus.DOWNLOADED) {
                        show = true
                    }
                }

                listener?.let { appUpdateInfoManager.registerListener(it) }

                appUpdateInfoManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.FLEXIBLE,
                    this,
                    343
                )

            }
            //  log("update ${appUpdateInfo.packageName()} ${appUpdateInfo.availableVersionCode()}",)
        }.addOnFailureListener {
            it.printStackTrace()
        }
    }
}
