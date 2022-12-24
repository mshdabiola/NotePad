package com.mshdabiola.editscreen

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.speech.RecognizerIntent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.mshdabiola.bottomsheet.ModalBottomSheet
import com.mshdabiola.bottomsheet.rememberModalState
import com.mshdabiola.designsystem.icon.NoteIcon
import com.mshdabiola.editscreen.state.NoteCheckUiState
import com.mshdabiola.editscreen.state.NotePadUiState
import com.mshdabiola.editscreen.state.NoteVoiceUiState
import com.mshdabiola.editscreen.state.toNotePadUiState
import com.mshdabiola.model.Note
import com.mshdabiola.model.NoteCheck
import com.mshdabiola.model.NotePad
import kotlinx.coroutines.launch


@Composable
fun EditScreen(
    editViewModel: EditViewModel = hiltViewModel(),
    onBack: () -> Unit
) {

    EditScreen(
        notepad = editViewModel.notePadUiState,
        onTitleChange = editViewModel::onTitleChange,
        onSubjectChange = editViewModel::onDetailChange,
        addItem = editViewModel::addCheck,
        onCheckChange = editViewModel::onCheckChange,
        onCheck = editViewModel::onCheck,
        onCheckDelete = editViewModel::onCheckDelete,
        onBackClick = onBack,
        playVoice = editViewModel::playMusic,
        saveImage = editViewModel::saveImage,
        saveVoice = editViewModel::saveVoice,
        getPhotoUri = editViewModel::getPhotoUri,
        savePhoto = editViewModel::savePhoto
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    notepad: NotePadUiState,
    onTitleChange: (String) -> Unit = {},
    onSubjectChange: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    onDeleteNote: () -> Unit = {},
    onSave: () -> Unit = {},
    onCheckChange: (String, Long) -> Unit = { _, _ -> },
    onCheckDelete: (Long) -> Unit = {},
    onCheck: (Boolean, Long) -> Unit = { _, _ -> },
    addItem: () -> Unit = {},
    playVoice: (String) -> Unit = {},
    saveImage: (Uri, Long) -> Unit = { _, _ -> },
    saveVoice: (Uri, Long) -> Unit = { _, _ -> },
    getPhotoUri: () -> Uri = { Uri.EMPTY },
    savePhoto: () -> Unit = {}
) {
    val context = LocalContext.current
    var expand by remember {
        mutableStateOf(false)
    }


    val subjectFocus = remember {
        FocusRequester()
    }

    val checkNote by remember(notepad.checks) {
        derivedStateOf { notepad.checks.filter { it.isCheck } }
    }
    val notCheckNote by remember(notepad.checks) {
        derivedStateOf { notepad.checks.filter { !it.isCheck } }
    }
    var showCheckNote by remember {
        mutableStateOf(true)
    }

    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            it?.let {
                Log.e("imageUir", "$it")

                val time = System.currentTimeMillis()
                saveImage(it, time)

            }

        })
    val snapPictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = {
            if (it) {
                savePhoto()
                // navigateToEdit(-3, "image text", photoId)
            }
        })


    val voiceLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            it.data?.let { intent ->
                val strArr = intent.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                val audiouri = intent.data


                if (audiouri != null) {
                    val time = System.currentTimeMillis()
                    saveVoice(audiouri, time)

                }


            }
        }
    )

    val audioPermission =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission(),
            onResult = {
                if (it) {
                    voiceLauncher.launch(
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

    val modalState = rememberModalState()
    val coroutineScope = rememberCoroutineScope()
    val paddingValues = Modifier.navigationBarsPadding()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Edit Screen") },
                navigationIcon = {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "back button"
                        )
                    }
                },

                actions = {
                    IconButton(onClick = { onDeleteNote() }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "delete note")
                    }
                    Box {
                        IconButton(onClick = { expand = true }) {

                            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "")
                        }
                        DropdownMenu(expanded = expand, onDismissRequest = { expand = false }) {
                            DropdownMenuItem(
                                text = { Text(text = "Save to file") },
                                onClick = { onSave() },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "save"
                                    )
                                }

                            )

                        }
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                actions = {
                    Row(Modifier.fillMaxWidth()) {
                        IconButton(onClick = { coroutineScope.launch { modalState.show() } }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = NoteIcon.Addbox),
                                contentDescription = ""
                            )
                        }
                    }
                }
            )


        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .verticalScroll(state = rememberScrollState())
                .padding(paddingValues)
                .fillMaxHeight()


        ) {
            if (notepad.images.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .height(200.dp)
                        .horizontalScroll(rememberScrollState())
                ) {
                    notepad.images.forEach {
                        AsyncImage(
                            modifier = Modifier
                                .height(200.dp)
                                .aspectRatio(1f),
                            model = it.imageName, contentDescription = ""
                        )
                    }
                }


            }
            TextField(
                value = notepad.note.title,
                onValueChange = onTitleChange,
                placeholder = { Text(text = "Title") },
                textStyle = MaterialTheme.typography.titleMedium,
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    containerColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    autoCorrect = true,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .fillMaxWidth()

            )
            if (!notepad.note.isCheck) {
                TextField(
                    value = notepad.note.detail,
                    onValueChange = onSubjectChange,
                    textStyle = MaterialTheme.typography.bodySmall,
                    placeholder = { Text(text = "Subject") },
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        containerColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        autoCorrect = true,
                        keyboardType = KeyboardType.Text,
                        // imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { subjectFocus.freeFocus() }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .imePadding()
                        .focusRequester(subjectFocus)


                )

            } else {

                if (notCheckNote.isNotEmpty()) {

                    notCheckNote.forEach { noteCheckUiState ->
                        //  key(keys = arrayOf( noteCheckUiState.id)) {
                        NoteCheck(
                            noteCheckUiState = noteCheckUiState,
                            onCheckChange,
                            onCheckDelete,
                            onCheck
                            )
                            //  }
                        }
                    }
                    Row(
                        modifier = Modifier.clickable { addItem() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "")

                        Text(text = "Add list item")
                    }

                    if (checkNote.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { showCheckNote = !showCheckNote }) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(id = if (showCheckNote) R.drawable.baseline_expand_more_24 else R.drawable.baseline_expand_less_24),
                                    contentDescription = ""
                                )
                            }
                            Text(
                                text = "${checkNote.size} Checked Items",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        if (showCheckNote) {
                            checkNote.forEach { noteCheckUiState ->
                                //  key(keys = arrayOf( noteCheckUiState.id)) {
                                NoteCheck(
                                    noteCheckUiState = noteCheckUiState,
                                    onCheckChange,
                                    onCheckDelete,
                                    onCheck,
                                    strickText = true
                                )
                                //  }
                            }
                        }

                    }


            }
            Spacer(modifier = Modifier.height(8.dp))
            notepad.voices.forEach {
                NoteVoicePlayer(it, playVoice)
                Spacer(modifier = Modifier.height(4.dp))
            }


        }
        ModalBottomSheet(modalState = modalState) {
            Surface {
                Column(modifier = Modifier.padding(bottom = 36.dp)) {
                    NavigationDrawerItem(icon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = NoteIcon.Photo),
                            contentDescription = ""
                        )
                    }, label = { Text(text = "Take photo") },
                        selected = false, onClick = {
                            snapPictureLauncher.launch(getPhotoUri())
                        })

                    NavigationDrawerItem(icon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = NoteIcon.Image),
                            contentDescription = ""
                        )
                    }, label = { Text(text = "Add image") },
                        selected = false, onClick = {
                            imageLauncher.launch(
                                PickVisualMediaRequest(
                                    mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                            coroutineScope.launch { modalState.hide() }

                        })
                    NavigationDrawerItem(icon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = NoteIcon.Brush),
                            contentDescription = ""
                        )
                    }, label = { Text(text = "Drawing") },
                        selected = false,
                        onClick = {

                        })
                    NavigationDrawerItem(icon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = NoteIcon.Voice),
                            contentDescription = ""
                        )
                    }, label = { Text(text = "Recording") },
                        selected = false, onClick = {
                            coroutineScope.launch { modalState.hide() }
                            if (context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) ==
                                PackageManager.PERMISSION_GRANTED
                            ) {
                                voiceLauncher.launch(
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
                        })
                    NavigationDrawerItem(icon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = NoteIcon.Check),
                            contentDescription = ""
                        )
                    }, label = { Text(text = "Checkboxes") },
                        selected = false, onClick = { /*TODO*/ })
                }
            }

        }
    }
}


@Preview
@Composable
fun EditScreenPreview() {
    EditScreen(
        notepad = NotePad(
            note = Note(isCheck = true),
            checks = listOf(
                NoteCheck(1, 2, "Food", true),
                NoteCheck(1, 2, "Food", true),
                NoteCheck(1, 2, "Food", false),
                NoteCheck(1, 2, "Food", false),
                NoteCheck(1, 2, "Food", true),
                NoteCheck(1, 2, "Food", true),


                )
        ).toNotePadUiState()
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteCheck(
    noteCheckUiState: NoteCheckUiState,
    onCheckChange: (String, Long) -> Unit = { _, _ -> },
    onCheckDelete: (Long) -> Unit = {},
    onCheck: (Boolean, Long) -> Unit = { _, _ -> },
    strickText: Boolean = false
) {

    val mutableInteractionSource = remember {
        MutableInteractionSource()
    }
    LaunchedEffect(key1 = Unit, block = {
        if (noteCheckUiState.id == 1L) {
            mutableInteractionSource.emit(FocusInteraction.Focus())
        }
    })
    val focused by mutableInteractionSource.collectIsFocusedAsState()
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = noteCheckUiState.isCheck,
            onCheckedChange = { onCheck(it, noteCheckUiState.id) })
        TextField(
            modifier = Modifier.weight(1f),
            value = noteCheckUiState.content,
            onValueChange = { onCheckChange(it, noteCheckUiState.id) },
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                containerColor = Color.Transparent
            ),
            textStyle = if (strickText) TextStyle.Default.copy(textDecoration = TextDecoration.LineThrough) else TextStyle.Default,
            interactionSource = mutableInteractionSource,
            trailingIcon = {
                if (focused) {
                    IconButton(onClick = {
                        onCheckDelete(noteCheckUiState.id)
                    }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "")
                    }
                }
            }

        )

    }
}

@Composable
fun NoteVoicePlayer(
    noteVoiceUiState: NoteVoiceUiState,
    playVoice: (String) -> Unit = {}
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.padding(8.dp),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        val duration by remember {

            val mediaMetadataRetriever = MediaMetadataRetriever()
            derivedStateOf {
                mediaMetadataRetriever.setDataSource(noteVoiceUiState.voiceName)
                val time =
                    mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                (time?.toIntOrNull() ?: 0)
            }
        }
        LaunchedEffect(key1 = duration, block = {
            Log.e("duration", "$duration")
        })
        var isPlay by remember {
            mutableStateOf(false)
        }

        val flow = remember {
            Animatable(0f)
        }
        LaunchedEffect(key1 = isPlay, block = {
            if (isPlay) {
                flow.animateTo(1f, animationSpec = tween(duration))
                // delay(duration)
                isPlay = false
            } else {
                flow.snapTo(0f)
            }
        })


        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {
                playVoice(noteVoiceUiState.voiceName)
                isPlay = true
            }) {
                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "play")
            }
            LinearProgressIndicator(modifier = Modifier.weight(1f), progress = flow.value)
            Text(text = "${duration / 60000} : ${(duration) / 1000 % 60}")
            IconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Outlined.Delete, contentDescription = "delete")
            }
        }

    }

}

@Preview()
@Composable
fun NoteVoicePlayerPreview() {
    NoteVoicePlayer(NoteVoiceUiState(3, 4, ""))
}




