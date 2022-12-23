package com.mshdabiola.mainscreen

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.speech.RecognizerIntent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mshdabiola.mainscreen.component.ImageDialog
import com.mshdabiola.mainscreen.component.NoteCard
import com.mshdabiola.mainscreen.state.NotePadUiState
import com.mshdabiola.mainscreen.state.toNotePadUiState
import com.mshdabiola.model.NotePad
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun MainScreen(
    mainViewModel: MainViewModel = hiltViewModel(),
    navigateToEdit: (Long, String, Long) -> Unit = { _, _, _ -> }
) {

    val mainState = mainViewModel.mainState.collectAsStateWithLifecycle()
    MainScreen(
        notePads = mainState.value.notePads,
        navigateToEdit = navigateToEdit,
        saveImage = mainViewModel::savePhoto,
        saveVoice = mainViewModel::saveVoice,
        photoUri = mainViewModel::getPhotoUri
    )

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    notePads: ImmutableList<NotePadUiState>,
    navigateToEdit: (Long, String, Long) -> Unit = { _, _, _ -> },
    saveImage: (Uri, Long) -> Unit = { _, _ -> },
    saveVoice: (Uri, Long) -> Unit = { _, _ -> },
    photoUri: (Long) -> Uri = { Uri.EMPTY }
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var showImageDialog by remember {
        mutableStateOf(false)
    }
    var photoId by remember {
        mutableStateOf(0L)
    }

    val context = LocalContext.current
    val openimageLanuch = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            it?.let {
                Log.e("imageUir", "$it")
                showImageDialog = false
                val time = System.currentTimeMillis()
                saveImage(it, time)
                navigateToEdit(-3, "image text", time)
            }

        })

    val snapPic = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = {
            if (it) {
                navigateToEdit(-3, "image text", photoId)
            }
        })


    val voiceCa = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            it.data?.let { intent ->
                val strArr = intent.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                val audiouri = intent.data


                if (audiouri != null) {
                    val time = System.currentTimeMillis()
                    saveVoice(audiouri, time)
                    Log.e("voice ", "uri $audiouri ${strArr?.joinToString()}")
                    navigateToEdit(-4, strArr?.joinToString() ?: "", time)
                }


            }
        }
    )

    val audioPermission =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission(),
            onResult = {
                if (it) {
                    voiceCa.launch(
                        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                            putExtra(
                                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                            )
                            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speck Now Now")
                            putExtra("android.speech.extra.GET_AUDIO_FORMAT", "audio/AMR")
                            putExtra("android.speech.extra.GET_AUDIO", true)

                        })


                }

            }
        )


    ModalNavigationDrawer(
        drawerContent = { },
        drawerState = drawerState,
        gesturesEnabled = true
    ) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    title = { Text(text = "Note Pad") },
                    navigationIcon = {
                        Icon(imageVector = Icons.Default.Menu, contentDescription = "menu")
                    },
                    actions = {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "")
                    },
                    scrollBehavior = scrollBehavior
                )
            },
            bottomBar = {
                BottomAppBar(
                    actions = {

                        IconButton(onClick = { navigateToEdit(-2, "", 0) }) {
                            Icon(
                                imageVector = ImageVector
                                    .vectorResource(id = R.drawable.outline_check_box_24),
                                contentDescription = "note check"
                            )
                        }

                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                imageVector = ImageVector
                                    .vectorResource(id = R.drawable.baseline_brush_24),
                                contentDescription = "note check"
                            )
                        }


                        IconButton(onClick = {
                            //navigateToEdit(-4, "", Uri.EMPTY)
                            if (context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) ==
                                PackageManager.PERMISSION_GRANTED
                            ) {
                                voiceCa.launch(
                                    Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                        putExtra(
                                            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                                        )
                                        putExtra(RecognizerIntent.EXTRA_PROMPT, "Speck Now Now")
                                        putExtra(
                                            "android.speech.extra.GET_AUDIO_FORMAT",
                                            "audio/AMR"
                                        )
                                        putExtra("android.speech.extra.GET_AUDIO", true)

                                    })

                            } else {
                                audioPermission.launch(Manifest.permission.RECORD_AUDIO)
                            }


                        }) {
                            Icon(
                                imageVector = ImageVector
                                    .vectorResource(id = R.drawable.outline_keyboard_voice_24),
                                contentDescription = "note check"
                            )
                        }

                        IconButton(onClick = {//
                            showImageDialog = true
                        }) {
                            Icon(
                                imageVector = ImageVector
                                    .vectorResource(id = R.drawable.outline_image_24),
                                contentDescription = "note check"
                            )
                        }

                    },
                    floatingActionButton = {
                        FloatingActionButton(onClick = { navigateToEdit(-1, "", 0) }) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "add note")
                        }
                    }
                )
            }
        ) { paddingValues ->
            LazyVerticalStaggeredGrid(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(8.dp),
                columns = StaggeredGridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)

            ) {
                items(notePads) { notePadUiState ->
                    NoteCard(
                        notePadUiState = notePadUiState,
                        onCardClick = { navigateToEdit(it, "", 0) })
                }

            }
            ImageDialog(
                show = showImageDialog,
                onDismissRequest = { showImageDialog = false },
                onChooseImage = {
                    openimageLanuch.launch(PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                onSnapImage = {
                    photoId = System.currentTimeMillis()
                    snapPic.launch(photoUri(photoId))
                }
            )
        }
    }
}


@Preview
@Composable
fun MainScreenPreview() {
    MainScreen(
        notePads =
        listOf(
            NotePad().toNotePadUiState(),
            NotePad().toNotePadUiState(),
            NotePad().toNotePadUiState(),
            NotePad().toNotePadUiState(),
            NotePad().toNotePadUiState()
        )
            .toImmutableList()
    )
}
