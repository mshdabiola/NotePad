package com.mshdabiola.detail

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.mshdabiola.designsystem.icon.NoteIcon
import com.mshdabiola.designsystem.R as Rd

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBottomSheet2(
    currentColor: Int,
    currentImage: Int,
    isNoteCheck: Boolean,
    saveImage: (String) -> Unit = {},
    saveVoice: (String, String) -> Unit = { _, _ -> },
    getPhotoUri: () -> String = { "" },
    changeToCheckBoxes: () -> Unit = {},
    onDrawing: () -> Unit = {},
    onDismiss: () -> Unit = {},
    show: Boolean,
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
                saveImage(it.toString())
            }
        },
    )
    val snapPictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = {
            if (it) {
                saveImage(getPhotoUri())
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
                    saveVoice(audiouri.toString(), strArr?.joinToString() ?: "")
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
            containerColor = background,

        ) {
            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = NoteIcon.PhotoCamera,
                        contentDescription = "",
                    )
                },
                label = { Text(text = stringResource(Rd.string.modules_designsystem_take_photo)) },
                selected = false,
                onClick = {
                    snapPictureLauncher.launch(Uri.parse(getPhotoUri()))
                    onDismiss()
                },
                colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = background),
                modifier = androidx.compose.ui.Modifier.testTag("detail:take_photo"),
            )

            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = NoteIcon.Image,
                        contentDescription = "",
                    )
                },
                label = { Text(text = stringResource(Rd.string.modules_designsystem_add_image)) },
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
                modifier = androidx.compose.ui.Modifier.testTag("detail:add_image"),
            )
            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = NoteIcon.Brush,
                        contentDescription = "",
                    )
                },
                label = { Text(text = stringResource(Rd.string.modules_designsystem_drawing)) },
                selected = false,
                onClick = {
                    onDismiss()
                    onDrawing()
                },
                colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = background),
                modifier = androidx.compose.ui.Modifier.testTag("detail:drawing"),

            )
            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = NoteIcon.KeyboardVoice,
                        contentDescription = "",
                    )
                },
                label = { Text(text = stringResource(Rd.string.modules_designsystem_recording)) },
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
                modifier = androidx.compose.ui.Modifier.testTag("detail:recording"),
            )
            if (!isNoteCheck) {
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = NoteIcon.CheckBox,
                            contentDescription = "",
                        )
                    },
                    label = { Text(text = stringResource(Rd.string.modules_designsystem_checkboxes)) },
                    selected = false,
                    onClick = {
                        onDismiss()
                        changeToCheckBoxes()
                    },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = background),
                    modifier = androidx.compose.ui.Modifier.testTag("detail:checkboxes"),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun AddBottomSheet2Preview() {
    // val coroutineScope= rememberCoroutineScope()

    AddBottomSheet2(
        currentColor = 2,
        currentImage = 2,
        isNoteCheck = true,
        show = true,
    )
}
