package com.mshdabiola.playnotepad

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.mshdabiola.analytics.AnalyticsHelper
import com.mshdabiola.analytics.LocalAnalyticsHelper
import com.mshdabiola.common.ContentManager
import com.mshdabiola.database.repository.DrawingPathRepository
import com.mshdabiola.database.repository.NoteImageRepository
import com.mshdabiola.designsystem.theme.SkTheme
import com.mshdabiola.model.Contrast
import com.mshdabiola.model.DarkThemeConfig
import com.mshdabiola.model.DrawingUtil
import com.mshdabiola.model.NoteImage
import com.mshdabiola.model.ThemeBrand
import com.mshdabiola.playnotepad.ui.NotePadApp
import com.mshdabiola.worker.util.DrawPathPojo
import com.mshdabiola.worker.util.changeToPathAndData
import com.mshdabiola.worker.util.getBitMap
import com.mshdabiola.worker.util.toDrawPath
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var show by mutableStateOf(false)
    private val appUpdateInfoManager by lazy { AppUpdateManagerFactory.create(this) }
    private var listener: InstallStateUpdatedListener? = null
    private var analytics: FirebaseAnalytics? = null
    private var remoteConfig: FirebaseRemoteConfig? = null

    @Inject
    lateinit var contentManager: ContentManager
    @Inject
    lateinit var noteImageRepository: NoteImageRepository
    @Inject
    lateinit var drawingPathRepository: DrawingPathRepository

    val viewModel: MainActivityViewModel by viewModels()

    @Inject
    lateinit var analyticsHelper: AnalyticsHelper


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
            CompositionLocalProvider(LocalAnalyticsHelper provides analyticsHelper) {
                SkTheme(
                    darkTheme = darkTheme,
                    themeBrand = chooseTheme(uiState),
                    themeContrast = chooseContrast(uiState),
                    disableDynamicTheming = shouldDisableDynamicTheming(uiState),
                    useAndroidTheme = shouldUseAndroidTheme(uiState),
                ) {
                    Box {


                        // A surface container using the 'background' color from the theme
                        NotePadApp(
                            windowSizeClass = calculateWindowSizeClass(activity = this@MainActivity),
                            saveImage = this@MainActivity::saveImage
                        )

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

    var job: Job? = null

    @OptIn(ExperimentalSerializationApi::class)
    private fun saveImage(imageId: Long, noteId: Long) {

        job?.cancel()

        Timber.e("save image function")
        job = lifecycleScope.launch(Dispatchers.IO) {


            val file = contentManager.dataFile(imageId)
            if (!file.exists()) {
                return@launch
            }
            val pathList = Json.decodeFromStream<List<DrawPathPojo>>(file.inputStream())

            val drawPathList = pathList.map { it.toDrawPath() }
            val pathsMap = drawPathList.let { DrawingUtil.toPathMap(it) }

            val re = this@MainActivity.resources.displayMetrics
            val bitmap = getBitMap(
                changeToPathAndData(pathsMap),
                re.widthPixels,
                re.heightPixels,
                re.density
            )
            val path = contentManager.getImagePath(imageId)
            contentManager.saveBitmap(path, bitmap)

            if (pathsMap.isEmpty()) {
                drawingPathRepository.delete(imageId)
                noteImageRepository.delete(imageId)
                File(contentManager.getImagePath(imageId)).deleteOnExit()
            } else {
                noteImageRepository.upsert(
                    NoteImage(
                        imageId,
                        noteId,
                        isDrawing = true,
                        timestamp = System.currentTimeMillis()
                    )
                )
                drawingPathRepository.delete(imageId)
                drawingPathRepository.insert(drawPathList)
            }

            file.delete()
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
        ThemeBrand.GREEN -> true
    }
}

@Composable
private fun chooseContrast(
    uiState: MainActivityUiState,
): Contrast = when (uiState) {
    MainActivityUiState.Loading -> Contrast.Normal
    is MainActivityUiState.Success -> uiState.userData.contrast
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
