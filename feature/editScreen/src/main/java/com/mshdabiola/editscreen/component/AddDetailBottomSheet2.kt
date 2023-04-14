package com.mshdabiola.editscreen.component

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Brush
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.KeyboardVoice
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.mshdabiola.designsystem.icon.NoteIcon
import com.mshdabiola.editscreen.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBottomSheet2(
    currentColor: Int,
    currentImage: Int,
    isNoteCheck: Boolean,
    saveImage: (Uri, Long) -> Unit = { _, _ -> },
    saveVoice: (Uri, String, Long) -> Unit = { _, _, _ -> },
    getPhotoUri: () -> Uri = { Uri.EMPTY },
    savePhoto: () -> Unit = {},
    changeToCheckBoxes: () -> Unit = {},
    onDrawing: () -> Unit = {},
    onDismiss: () -> Unit = {},
    show: Boolean
) {
    val background = if (currentImage != -1) {
        NoteIcon.background[currentImage].fgColor
    } else {
        if (currentColor != -1) {
            NoteIcon.noteColors[currentColor]
        } else {
            MaterialTheme.colorScheme.surface
        }
    }

    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            it?.let {
                val time = System.currentTimeMillis()
                saveImage(it, time)
            }
        },
    )
    val snapPictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = {
            if (it) {
                savePhoto()
                // navigateToEdit(-3, "image text", photoId)
            }
        },
    )

    val voiceLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            it.data?.let { intent ->
                val strArr = intent.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                val audiouri = intent.data

                if (audiouri != null) {
                    val time = System.currentTimeMillis()
                    saveVoice(audiouri, strArr?.joinToString() ?: "", time)
                }
            }
        },
    )

    val audioPermission =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = {
                if (it) {
                    voiceLauncher.launch(
                        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                            putExtra(
                                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM,
                            )
                            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speck Now Now")
                            putExtra("android.speech.extra.GET_AUDIO_FORMAT", "audio/AMR")
                            putExtra("android.speech.extra.GET_AUDIO", true)
                        },
                    )
                }
            },
        )


    val context = LocalContext.current

    if (show) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            containerColor = background
        ) {

            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.PhotoCamera,
                        contentDescription = "",
                    )
                },
                label = { Text(text = stringResource(R.string.take_photo)) },
                selected = false,
                onClick = {
                    snapPictureLauncher.launch(getPhotoUri())
                    onDismiss()
                },
                colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = background),
            )


            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Image,
                        contentDescription = "",
                    )
                },
                label = { Text(text = stringResource(R.string.add_image)) },
                selected = false,
                onClick = {
                    imageLauncher.launch(
                        PickVisualMediaRequest(
                            mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly,
                        ),
                    )
                    onDismiss()
                },
                colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = background),
            )
            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Brush,
                        contentDescription = "",
                    )
                },
                label = { Text(text = stringResource(R.string.drawing)) },
                selected = false,
                onClick = {
                    onDismiss()
                    onDrawing()
                },
                colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = background),

                )
            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.KeyboardVoice,
                        contentDescription = "",
                    )
                },
                label = { Text(text = stringResource(R.string.recording)) },
                selected = false,
                onClick = {
                    onDismiss()
                    if (context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) ==
                        PackageManager.PERMISSION_GRANTED
                    ) {
                        voiceLauncher.launch(
                            Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                putExtra(
                                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM,
                                )
                                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speck Now Now")
                                putExtra(
                                    "android.speech.extra.GET_AUDIO_FORMAT",
                                    "audio/AMR",
                                )
                                putExtra("android.speech.extra.GET_AUDIO", true)
                            },
                        )
                    } else {
                        audioPermission.launch(Manifest.permission.RECORD_AUDIO)
                    }
                },
                colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = background),
            )
            if (!isNoteCheck) {
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.CheckBox,
                            contentDescription = "",
                        )
                    },
                    label = { Text(text = stringResource(R.string.checkboxes)) },
                    selected = false,
                    onClick = {
                        onDismiss()
                        changeToCheckBoxes()
                    },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = background),
                )
            }


        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun AddBottomSheet2Preview() {
    //val coroutineScope= rememberCoroutineScope()


    AddBottomSheet2(
        currentColor = 2,
        currentImage = 2,
        isNoteCheck = true,
        show = true
    )
}

