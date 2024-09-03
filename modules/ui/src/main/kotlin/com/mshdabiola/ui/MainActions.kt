package com.mshdabiola.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AudioDialog(
    show: Boolean = false,
    dismiss: () -> Unit = {},
    output: (String, String) -> Unit = { _, _ -> },
) {
    val context = LocalContext.current
    val voiceLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            it.data?.let { intent ->
                val text = intent
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    ?.joinToString() ?: ""

                output(intent.data!!.toString(), text)
            }
            dismiss()
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

    LaunchedEffect(
        key1 = show,
        block = {
            if (show) {
                // navigateToEdit(-4, "", Uri.EMPTY)
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
            }
        },
    )
}

@Composable
fun ImageDialog2(
    modifier: Modifier = Modifier,
    show: Boolean = false,
    dismiss: () -> Unit = {},
    saveImage: (String) -> Unit = {},
    getUri: () -> String = { "" },

) {
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            it?.let {
//                showImageDialog = false
//                val time = System.currentTimeMillis()
                saveImage(it.toString())
//                navigateToEdit(-3, "image text", time)
                dismiss()
            }
        },
    )

    val snapPictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = {
            if (it) {
                saveImage(getUri())
                dismiss()
                // navigateToEdit(-3, "image text", photoId)
            }
        },
    )

    ImageDialog(
        show = show,
        onDismissRequest = dismiss,
        onChooseImage = {
            imageLauncher.launch(PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly))
        },
        onSnapImage = {
            snapPictureLauncher.launch(Uri.parse(getUri()))
        },
    )
}

@Composable
fun ImageDialog(
    show: Boolean = false,
    onDismissRequest: () -> Unit = {},
    onChooseImage: () -> Unit = {},
    onSnapImage: () -> Unit = {},

) {
    AnimatedVisibility(visible = show) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            //  title = { Text(text = stringResource(R.string.feature_mainscreen_add_image)) },
            text = {
                Column {
                    Row(
                        modifier = Modifier
                            .clickable { onSnapImage() }
                            .fillMaxWidth()
                            .padding(16.dp),

                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PhotoCamera,
                            contentDescription = "take image",
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "take image", // stringResource(R.string.feature_mainscreen_take_image)
                        )
                    }
                    Row(
                        modifier = Modifier
                            .clickable { onChooseImage() }
                            .fillMaxWidth()
                            .padding(16.dp),

                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Image,
                            contentDescription = "take phone",
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "choose image", // stringResource(R.string.feature_mainscreen_choose_image)
                        )
                    }
                }
            },
            confirmButton = {},
        )
    }
}

@Preview
@Composable
fun ImageDialogPreview() {
    ImageDialog(true)
}
