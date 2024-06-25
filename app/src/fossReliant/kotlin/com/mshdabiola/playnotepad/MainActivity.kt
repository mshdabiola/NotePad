package com.mshdabiola.playnotepad

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
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
import com.mshdabiola.analytics.AnalyticsHelper
import com.mshdabiola.analytics.LocalAnalyticsHelper
import com.mshdabiola.common.IContentManager
import com.mshdabiola.data.repository.IDrawingPathRepository
import com.mshdabiola.data.repository.INoteImageRepository
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

    @Inject
    lateinit var contentManager: IContentManager

    @Inject
    lateinit var noteImageRepository: INoteImageRepository

    @Inject
    lateinit var drawingPathRepository: IDrawingPathRepository

    val viewModel: MainActivityViewModel by viewModels()

    @Inject
    lateinit var analyticsHelper: AnalyticsHelper

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        var uiState: MainActivityUiState by mutableStateOf(MainActivityUiState.Loading)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState
                    .onEach { uiState = it }
                    .collect()
            }
        }
        enableEdgeToEdge()

        setContent {
            val darkTheme = shouldUseDarkTheme(uiState)

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
                    disableDynamicTheming = shouldDisableDynamicTheming(uiState),
                ) {
                    NotePadApp(
                        windowSizeClass = calculateWindowSizeClass(activity = this@MainActivity),
                        saveImage = this@MainActivity::saveImage,
                    )
                }
            }
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
                re.density,
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
                        timestamp = System.currentTimeMillis(),
                    ),
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
