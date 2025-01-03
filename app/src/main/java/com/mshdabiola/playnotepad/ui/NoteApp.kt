/*
 *abiola 2022
 */

package com.mshdabiola.playnotepad.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration.Short
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult.ActionPerformed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.navOptions
import com.mshdabiola.about.navigateToAbout
import com.mshdabiola.designsystem.component.NoteBackground
import com.mshdabiola.designsystem.component.NoteGradientBackground
import com.mshdabiola.designsystem.icon.NoteIcon
import com.mshdabiola.designsystem.theme.GradientColors
import com.mshdabiola.designsystem.theme.LocalGradientColors
import com.mshdabiola.detail.navigation.DetailArg
import com.mshdabiola.detail.navigation.navigateToDetail
import com.mshdabiola.drawing.navigateToDrawing
import com.mshdabiola.labelscreen.navigateToLabel
import com.mshdabiola.main.navigation.navigateToMain
import com.mshdabiola.playnotepad.MainActivityViewModel
import com.mshdabiola.playnotepad.navigation.NoteNavHost
import com.mshdabiola.setting.navigation.navigateToSetting
import com.mshdabiola.ui.AudioDialog
import com.mshdabiola.ui.ImageDialog2
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NoteApp(
    viewModel: MainActivityViewModel,
    appState: NoteAppState,
    modifier: Modifier = Modifier,
) {
    val shouldShowGradientBackground = true
    val labels = viewModel.labels.collectAsStateWithLifecycle()
    var showAudio by remember { mutableStateOf(false) }
    var showImage by remember { mutableStateOf(false) }

    NoteBackground(modifier = modifier) {
        NoteGradientBackground(
            gradientColors = if (shouldShowGradientBackground) {
                LocalGradientColors.current
            } else {
                GradientColors()
            },
        ) {
            val snackbarHostState = remember { SnackbarHostState() }

            val isOffline by appState.isOffline.collectAsStateWithLifecycle()

            // If user is not connected to the internet show a snack bar to inform them.
            val notConnectedMessage = "not connected" // stringResource(R.string.not_connected)
            LaunchedEffect(isOffline) {
                if (isOffline) {
                    snackbarHostState.showSnackbar(
                        message = notConnectedMessage,
                        duration = Short,
                    )
                }
            }
            ModalNavigationDrawer(
                drawerContent = {
                    MainNavigation(
                        labels = labels.value,
                        currentMainArg = appState.mainArg,
                        onNavigation = {
                            //  onNavigationNoteType(it)
//                            appState.navController.navigateToMain()
                            appState.navController.popBackStack()
                            appState.navController.navigateToMain(
                                it,
                                navOptions {
                                    //  this.launchSingleTop=true
                                },
                            )
                            appState.closeDrawer()
                        },
                        navigateToLevel = {
                            appState.navController.navigateToLabel(it)
                            appState.closeDrawer()
                        },
                        navigateToAbout = {
                            appState.navController.navigateToAbout()
                            appState.closeDrawer()
                        },
                        navigateToSetting = {
                            appState.navController.navigateToSetting()
                            appState.closeDrawer()
                        },

                    )
                },
                drawerState = appState.drawerState,
                gesturesEnabled = appState.isMain,
            ) {
                Scaffold(
                    modifier = modifier.semantics {
                        testTagsAsResourceId = true
                    },
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    bottomBar = {
                        if (appState.isMain) {
                            NoteBottomBar(
                                onAddNewNote = {
                                    appState.coroutineScope.launch {
                                        val id = viewModel.insertNewNote()
                                        appState.navController.navigateToDetail(DetailArg(id))
                                    }
                                },
                                onAddVoiceNote = {
                                    showAudio = true
                                },
                                onAddCheckNote = {
                                    appState.coroutineScope.launch {
                                        val id = viewModel.insertNewCheckNote()
                                        appState.navController.navigateToDetail(DetailArg(id))
                                    }
                                },
                                onAddImageNote = {
                                    showImage = true
                                },
                                onAddDrawNote = {
                                    appState.coroutineScope.launch {
                                        val id = viewModel.insertNewDrawing()
                                        appState.navController.navigateToDetail(DetailArg(id.first))
                                        appState.navController.navigateToDrawing(id.first, id.second)
                                    }
                                },

                            )
                        }
                    },

                ) { padding ->
                    NoteNavHost(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .consumeWindowInsets(padding)
                            .windowInsetsPadding(
                                WindowInsets.safeDrawing.only(
                                    WindowInsetsSides.Horizontal,
                                ),
                            ),
                        appState = appState,
                        onShowSnackbar = { message, action ->
                            snackbarHostState.showSnackbar(
                                message = message,
                                actionLabel = action,
                                duration = Short,
                            ) == ActionPerformed
                        },
                    )
                }
            }

            AudioDialog(
                show = showAudio,
                dismiss = { showAudio = false },
                output = { uri, text ->
                    appState.coroutineScope.launch {
                        val id = viewModel.insertNewAudioNote(uri, text)
                        appState.navController.navigateToDetail(DetailArg(id))
                    }
                },

            )
            ImageDialog2(
                show = showImage,
                dismiss = { showImage = false },
                getUri = viewModel::pictureUri,
                saveImage = {
                    appState.coroutineScope.launch {
                        val id = viewModel.insertNewImageNote(it)
                        appState.navController.navigateToDetail(DetailArg(id))
                    }
                },
            )
        }
    }
}

private fun Modifier.notificationDot(): Modifier =
    composed {
        val tertiaryColor = MaterialTheme.colorScheme.tertiary
        drawWithContent {
            drawContent()
            drawCircle(
                tertiaryColor,
                radius = 5.dp.toPx(),
                // This is based on the dimensions of the NavigationBar's "indicator pill";
                // however, its parameters are private, so we must depend on them implicitly
                // (NavigationBarTokens.ActiveIndicatorWidth = 64.dp)
                center = center + Offset(
                    64.dp.toPx() * .45f,
                    32.dp.toPx() * -.45f - 6.dp.toPx(),
                ),
            )
        }
    }

@Composable
fun NoteBottomBar(
    modifier: Modifier = Modifier,
    onAddNewNote: () -> Unit = {},
    onAddCheckNote: () -> Unit = {},
    onAddDrawNote: () -> Unit = {},
    onAddVoiceNote: () -> Unit = {},
    onAddImageNote: () -> Unit = {},
) {
    BottomAppBar(
        actions = {
            IconButton(
                modifier = Modifier.testTag("main:check"),
                onClick = onAddCheckNote,
            ) {
                Icon(
                    imageVector = NoteIcon.CheckBox,
                    contentDescription = "add note check",
                )
            }

            IconButton(
                modifier = Modifier.testTag("main:draw"),
                onClick = onAddDrawNote,
            ) {
                Icon(
                    imageVector = NoteIcon.Brush,
                    contentDescription = "add note drawing",
                )
            }

            IconButton(
                modifier = Modifier.testTag("main:voice"),
                onClick = onAddVoiceNote,
            ) {
                Icon(
                    imageVector = NoteIcon.KeyboardVoice,
                    contentDescription = "add note voice",
                )
            }

            IconButton(
                modifier = Modifier.testTag("main:image"),
                onClick = onAddImageNote,
            ) {
                Icon(
                    imageVector = NoteIcon.Image,
                    contentDescription = "add note image",
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.testTag("main:add"),
                onClick = onAddNewNote,
                containerColor = MaterialTheme.colorScheme.primary,
                elevation = FloatingActionButtonDefaults.elevation(),
            ) {
                Icon(imageVector = NoteIcon.Add, contentDescription = "add note")
            }
        },
    )
}
