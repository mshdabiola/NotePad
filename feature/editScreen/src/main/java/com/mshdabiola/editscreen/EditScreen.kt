package com.mshdabiola.editscreen

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ShareCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.mshdabiola.bottomsheet.rememberModalState
import com.mshdabiola.designsystem.component.LabelCard
import com.mshdabiola.designsystem.component.NotificationDialog
import com.mshdabiola.designsystem.component.ReminderCard
import com.mshdabiola.designsystem.component.state.NoteCheckUiState
import com.mshdabiola.designsystem.component.state.NotePadUiState
import com.mshdabiola.designsystem.component.state.NoteTypeUi
import com.mshdabiola.designsystem.component.state.NoteUiState
import com.mshdabiola.designsystem.component.state.NoteVoiceUiState
import com.mshdabiola.designsystem.icon.NoteIcon
import com.mshdabiola.editscreen.component.AddBottomSheet
import com.mshdabiola.editscreen.component.ColorAndImageBottomSheet
import com.mshdabiola.editscreen.component.NoteOptionBottomSheet
import com.mshdabiola.editscreen.component.NotificationBottomSheet
import com.mshdabiola.searchscreen.FlowLayout2
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock


@Composable
fun EditScreen(
    editViewModel: EditViewModel = hiltViewModel(),
    onBack: () -> Unit,
    navigateToSelectLevel: (IntArray) -> Unit
) {

    val modalState = rememberModalState()
    val noteModalState = rememberModalState()
    val colorModalState = rememberModalState()
    val notificationModalState = rememberModalState()
    val coroutineScope = rememberCoroutineScope()

    var showDialog by remember {
        mutableStateOf(false)
    }
    val notificationPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            if (it) {
                coroutineScope.launch { notificationModalState.show() }
            }
        }
    )
    val context = LocalContext.current
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
        moreOptions = { coroutineScope.launch { modalState.show() } },
        noteOption = { coroutineScope.launch { noteModalState.show() } },
        onColorClick = { coroutineScope.launch { colorModalState.show() } },
        unCheckAllItems = editViewModel::unCheckAllItems,
        deleteCheckItems = editViewModel::deleteCheckedItems,
        hideCheckBoxes = editViewModel::hideCheckBoxes,
        pinNote = editViewModel::pinNote,
        onLabel = {
            navigateToSelectLevel(
                intArrayOf(
                    editViewModel.notePadUiState.note.id?.toInt() ?: -1
                )
            )
        },
        onNotification = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED
            ) {
                notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                coroutineScope.launch { notificationModalState.show() }
            }
        },
        showNotificationDialog = {
            showDialog = true
        },
        onArchive = editViewModel::onArchive


    )
    AddBottomSheet(
        modalState = modalState,
        currentColor = editViewModel.notePadUiState.note.color,
        currentImage = editViewModel.notePadUiState.note.background,
        isNoteCheck = editViewModel.notePadUiState.note.isCheck,
        saveImage = editViewModel::saveImage,
        saveVoice = editViewModel::saveVoice,
        getPhotoUri = editViewModel::getPhotoUri,
        savePhoto = editViewModel::savePhoto,
        changeToCheckBoxes = editViewModel::changeToCheckBoxes
    )

    val send = {
        val notePads = editViewModel.notePadUiState
        val intent = ShareCompat.IntentBuilder(context)
            .setText(notePads.toString())
            .setType("text/*")
            .setChooserTitle("From Notepad")
            .createChooserIntent()
        context.startActivity(Intent(intent))

    }
    NoteOptionBottomSheet(
        modalState = noteModalState,
        currentColor = editViewModel.notePadUiState.note.color,
        currentImage = editViewModel.notePadUiState.note.background,
        onLabel = {
            navigateToSelectLevel(
                intArrayOf(
                    editViewModel.notePadUiState.note.id?.toInt() ?: -1
                )
            )
        },
        onDelete = editViewModel::onDelete,
        onCopy = editViewModel::copyNote,
        onSendNote = send
    )
    ColorAndImageBottomSheet(
        modalState = colorModalState,
        currentColor = editViewModel.notePadUiState.note.color,
        currentImage = editViewModel.notePadUiState.note.background,
        onColorClick = editViewModel::onColorChange,
        onImageClick = editViewModel::onImageChange
    )


    NotificationBottomSheet(
        modalState = notificationModalState,
        onAlarm = editViewModel::setAlarm,
        showDialog = { showDialog = true }
    )

    NotificationDialog(
        showDialog,
        onDismissRequest = { showDialog = false },
        remainder = editViewModel.notePadUiState.note.reminder,
        interval = if (editViewModel.notePadUiState.note.interval == (-1L)) null else editViewModel.notePadUiState.note.interval,
        onSetAlarm = editViewModel::setAlarm,
        onDeleteAlarm = editViewModel::deleteAlarm
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
    moreOptions: () -> Unit = {},
    noteOption: () -> Unit = {},
    unCheckAllItems: () -> Unit = {},
    deleteCheckItems: () -> Unit = {},
    hideCheckBoxes: () -> Unit = {},
    pinNote: () -> Unit = {},
    onLabel: () -> Unit = {},
    onColorClick: () -> Unit = {},
    onNotification: () -> Unit = {},
    showNotificationDialog: () -> Unit = {},
    onArchive: () -> Unit = {}
) {

    var expand by remember {
        mutableStateOf(false)
    }

    var expandCheck by remember {
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

    val fg = if (notepad.note.background != -1) {
        NoteIcon.background[notepad.note.background].fgColor
    } else {
        if (notepad.note.color != -1) {
            NoteIcon.noteColors[notepad.note.color]
        } else {
            MaterialTheme.colorScheme.background
        }
    }
    val bg = if (notepad.note.background != -1) {
        Color.Transparent
    } else {
        if (notepad.note.color != -1)
            NoteIcon.noteColors[notepad.note.color]
        else
            MaterialTheme.colorScheme.background
    }

    val sColor = if (notepad.note.background != -1)
        NoteIcon.background[notepad.note.background].fgColor
    else
        MaterialTheme.colorScheme.secondaryContainer


    val painter = if (notepad.note.background != -1)
        rememberVectorPainter(image = ImageVector.vectorResource(id = NoteIcon.background[notepad.note.background].bg))
    else
        null

    Scaffold(
        containerColor = bg,
        modifier = Modifier.drawBehind {
            if (painter != null) {
                with(painter) {
                    draw(size)
                }
            }
        },
        topBar = {
            TopAppBar(
                title = { },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                navigationIcon = {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "back button"
                        )
                    }
                },

                actions = {
                    IconButton(onClick = { pinNote() }) {
                        Icon(
                            painter = painterResource(id = if (notepad.note.isPin) NoteIcon.PinFill else NoteIcon.Pin),
                            contentDescription = "pin"
                        )
                    }
                    IconButton(onClick = { onNotification() }) {
                        Icon(
                            painter = painterResource(id = NoteIcon.Alarm),
                            contentDescription = "notification"
                        )
                    }
                    IconButton(onClick = { onArchive() }) {
                        Icon(
                            painter = painterResource(id = if (notepad.note.noteType == NoteTypeUi.ARCHIVE) NoteIcon.Unarchive else NoteIcon.Archive),
                            contentDescription = "archive"
                        )
                    }

                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color.Transparent,
                actions = {
                    Row(Modifier.fillMaxWidth()) {
                        IconButton(onClick = { moreOptions() }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = NoteIcon.Addbox),
                                contentDescription = ""
                            )
                        }
                        IconButton(onClick = { onColorClick() }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = NoteIcon.ColorLens),
                                contentDescription = ""
                            )
                        }
                        Row(Modifier.weight(1f)) {

                        }
                        IconButton(onClick = { noteOption() }) {
                            Icon(
                                imageVector = Icons.Outlined.MoreVert,
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                        .weight(1f)

                )
                if (notepad.note.isCheck) {
                    Box {
                        IconButton(onClick = { expandCheck = true }) {

                            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "")
                        }
                        DropdownMenu(
                            expanded = expandCheck,
                            onDismissRequest = { expandCheck = false }) {
                            DropdownMenuItem(
                                text = { Text(text = "Hide checkboxes") },
                                onClick = {
                                    hideCheckBoxes()
                                    expandCheck = false
                                },
                            )
                            if (checkNote.isNotEmpty()) {
                                DropdownMenuItem(
                                    text = { Text(text = "UnCheck all items") },
                                    onClick = {
                                        unCheckAllItems()
                                        expandCheck = false
                                    },
                                )
                                DropdownMenuItem(
                                    text = { Text(text = "Delete checked items") },
                                    onClick = {
                                        deleteCheckItems()
                                        expandCheck = false
                                    },
                                )
                            }


                        }
                    }
                }
            }

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
            Spacer(modifier = Modifier.height(8.dp))


            FlowLayout2(
                verticalSpacing = 8.dp
            ) {
                if (notepad.note.reminder > 0) {
                    ReminderCard(
                        remainder = notepad.note.reminder,
                        interval = notepad.note.interval,
                        color = sColor,
                        style = MaterialTheme.typography.bodyLarge,
                        onClick = showNotificationDialog
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                notepad.labels.forEach {
                    LabelCard(
                        name = it,
                        color = sColor,
                        style = MaterialTheme.typography.bodyLarge,
                        onClick = onLabel
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }


        }

    }
}


@Preview
@Composable
fun EditScreenPreview() {
    EditScreen(
        notepad = NotePadUiState(
            note = NoteUiState(
                color = 1,
                background = 1,
                reminder = Clock.System.now().toEpochMilliseconds(),
                interval = 1
            ),
            labels = listOf("abiola", "moshood").toImmutableList()

        )
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










