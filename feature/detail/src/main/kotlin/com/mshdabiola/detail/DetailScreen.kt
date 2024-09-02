/*
 *abiola 2022
 */

package com.mshdabiola.detail

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.NotificationAdd
import androidx.compose.material.icons.outlined.PauseCircle
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.Unarchive
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ShareCompat
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.mshdabiola.designsystem.component.SkTextField
import com.mshdabiola.designsystem.component.SkTextFieldCheck
import com.mshdabiola.designsystem.icon.NoteIcon
import com.mshdabiola.model.NoteType
import com.mshdabiola.ui.DateDialog
import com.mshdabiola.ui.FirebaseScreenLog
import com.mshdabiola.ui.FlowLayout2
import com.mshdabiola.ui.LabelCard
import com.mshdabiola.ui.NotificationDialogNew
import com.mshdabiola.ui.ReminderCard
import com.mshdabiola.ui.TimeDialog
import com.mshdabiola.ui.state.NoteCheckUiState
import com.mshdabiola.ui.state.NotePadUiState
import com.mshdabiola.ui.state.NoteUriState
import com.mshdabiola.ui.state.NoteVoiceUiState
import com.mshdabiola.ui.toTime
import kotlinx.datetime.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DetailRoute(
    onShowSnackbar: suspend (String, String?) -> Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    editViewModel: DetailViewModel = hiltViewModel(),
) {

    val note = editViewModel.note.collectAsStateWithLifecycle()
    var showModalState by remember {
        mutableStateOf(false)
    }
    var noteModalState by remember {
        mutableStateOf(false)
    }
    var noteficationModalState by remember {
        mutableStateOf(false)
    }
    var colorModalState by remember {
        mutableStateOf(false)
    }

    val coroutineScope = rememberCoroutineScope()

    var showDialog by remember {
        mutableStateOf(false)
    }
    val notificationPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            if (it) {
                noteficationModalState = true
            }
        },
    )
    val context = LocalContext.current
//    LaunchedEffect(key1 = editViewModel.navigateToDrawing, block = {
//        if (editViewModel.navigateToDrawing) {
//            navigateToDrawing(editViewModel.notePadUiState.note.id, null)
//            editViewModel.navigateToDrawing = false
//        }
//    })
    FirebaseScreenLog(screen = "edit_screen")

    EditScreen(
        notepad = editViewModel.note.value,
        title = editViewModel.title,
        content = editViewModel.content,
//        onTitleChange = editViewModel::onTitleChange,
//        onSubjectChange = editViewModel::onDetailChange,
        onBackClick = onBack,
        onCheckChange = editViewModel::onCheckChange,
        onCheckDelete = editViewModel::onCheckDelete,
        onCheck = editViewModel::onCheck,
        addItem = editViewModel::addCheck,
        playVoice = editViewModel::playMusic,
        pauseVoice = editViewModel::pause,
        moreOptions = {
//            coroutineScope.launch { modalState.show() }
            showModalState = true
        },
        noteOption = { noteModalState = true },
        unCheckAllItems = editViewModel::unCheckAllItems,
        deleteCheckItems = editViewModel::deleteCheckedItems,
        hideCheckBoxes = editViewModel::hideCheckBoxes,
        pinNote = editViewModel::pinNote,
        onLabel = {
//            navigateToSelectLevel(
//                intArrayOf(
//                    editViewModel.notePadUiState.note.id.toInt(),
//                ),
//            )
        },
        onColorClick = { colorModalState = true },
        onNotification = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED
            ) {
                notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                noteficationModalState = true
            }
        },
        showNotificationDialog = {
            showDialog = true
        },
        onArchive = editViewModel::onArchive,
        deleteVoiceNote = editViewModel::deleteVoiceNote,
        navigateToGallery = { //navigateToGallery
        },
        navigateToDrawing = { //navigateToDrawing
        },

        )
    AddBottomSheet2(
        show = showModalState,
        currentColor = note.value.color,
        currentImage = note.value.background,
        isNoteCheck = note.value.isCheck,
//        saveImage = editViewModel::saveImage,
//        saveVoice = editViewModel::saveVoice,
//        getPhotoUri = editViewModel::getPhotoUri,
//        savePhoto = editViewModel::savePhoto,
        changeToCheckBoxes = editViewModel::changeToCheckBoxes,
        onDrawing = { //navigateToDrawing(editViewModel.notePadUiState.note.id, null)
        },
        onDismiss = { showModalState = false },
    )
//
    val send = {
        val notePads = noteModalState
        val intent = ShareCompat.IntentBuilder(context)
            .setText(notePads.toString())
            .setType("text/*")
            .setChooserTitle("From Notepad")
            .createChooserIntent()
        context.startActivity(Intent(intent))
    }
    NoteOptionBottomSheet(
        show = noteModalState,
        currentColor = note.value.color,
        currentImage = note.value.background,
        onLabel = {
//            navigateToSelectLevel(
//                intArrayOf(
//                    editViewModel.notePadUiState.note.id.toInt(),
//                ),
//            )
        },
        onDelete = editViewModel::onDelete,
        onCopy = editViewModel::copyNote,
        onSendNote = send,
        onDismissRequest = { noteModalState = false },
    )
    ColorAndImageBottomSheet(
        show = colorModalState,
        currentColor = note.value.color,
        currentImage = note.value.background,
        onColorClick = editViewModel::onColorChange,
        onImageClick = editViewModel::onImageChange,
        onDismissRequest = { colorModalState = false },
    )
//
    NotificationBottomSheet(
        show = noteficationModalState,
        onAlarm = editViewModel::setAlarm,
        showDialog = { showDialog = true },
        currentColor = note.value.color,
        currentImage = note.value.background,

        ) { noteficationModalState = false }
    val dateDialogUiData = editViewModel.dateTimeState.collectAsStateWithLifecycle()
//
    NotificationDialogNew(
        showDialog = showDialog,
        dateDialogUiData = dateDialogUiData.value,
        onDismissRequest = { showDialog = false },
        onSetAlarm = editViewModel::setAlarm,
        onTimeChange = editViewModel::onSetTime,
        onDateChange = editViewModel::onSetDate,
        onIntervalChange = editViewModel::onSetInterval,
        onDeleteAlarm = editViewModel::deleteAlarm,
    )
//
    TimeDialog(
        state = editViewModel.timePicker,
        showDialog = dateDialogUiData.value.showTimeDialog,
        onDismissRequest = editViewModel::hideTime,
        onSetTime = editViewModel::onSetTime,
    )
    DateDialog(
        state = editViewModel.datePicker,
        showDialog = dateDialogUiData.value.showDateDialog,
        onDismissRequest = editViewModel::hideDate,
        onSetDate = editViewModel::onSetDate,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    notepad: NotePadUiState,
    title: TextFieldState,
    content: TextFieldState,
//    onTitleChange: (String) -> Unit = {},
//    onSubjectChange: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    onCheckChange: (String, Long) -> Unit = { _, _ -> },
    onCheckDelete: (Long) -> Unit = {},
    onCheck: (Boolean, Long) -> Unit = { _, _ -> },
    addItem: () -> Unit = {},
    playVoice: (Int) -> Unit = {},
    pauseVoice: () -> Unit = {},
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
    onArchive: () -> Unit = {},
    deleteVoiceNote: (Int) -> Unit = {},
    navigateToGallery: (Long) -> Unit = {},
    navigateToDrawing: (Long) -> Unit = {},
) {
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

    val bg = if (notepad.background != -1) {
        Color.Transparent
    } else {
        if (notepad.color != -1) {
            NoteIcon.noteColors[notepad.color]
        } else {
            MaterialTheme.colorScheme.background
        }
    }
    val color = NoteIcon.noteColors.getOrNull(notepad.color) ?: Color.Transparent

    val sColor = if (notepad.background != -1) {
        NoteIcon.background[notepad.background].fgColor
    } else {
        MaterialTheme.colorScheme.secondaryContainer
    }

    val painter = if (notepad.background != -1) {
        rememberVectorPainter(image = ImageVector.vectorResource(id = NoteIcon.background[notepad.background].bg))
    } else {
        null
    }

    val images = remember(notepad.images) {
        notepad.images.reversed().chunked(3)
    }



    LaunchedEffect(
        key1 = notepad,
        block = {
            if (notepad.focus) {
                subjectFocus.requestFocus()
            }
        },
    )

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
                            contentDescription = "back",
                        )
                    }
                },

                actions = {
                    IconButton(onClick = { pinNote() }) {
                        Icon(
                            imageVector = if (notepad.isPin) Icons.Default.PushPin else Icons.Outlined.PushPin,
                            contentDescription = "pin",
                        )
                    }
                    IconButton(onClick = { onNotification() }) {
                        Icon(
                            imageVector = Icons.Outlined.NotificationAdd,
                            contentDescription = "notification",
                        )
                    }
                    IconButton(onClick = { onArchive() }) {
                        Icon(
                            imageVector = if (notepad.noteType.type == NoteType.ARCHIVE) Icons.Outlined.Unarchive else Icons.Outlined.Archive,
                            contentDescription = "archive",
                        )
                    }
                },
            )
        },

        ) { paddingValues ->
        Column(
            Modifier
                .padding(paddingValues)
                .fillMaxHeight(),
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .testTag("edit:lazy"),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                if (notepad.images.isNotEmpty()) {
                    item(images) {
                        images.forEach { imageList ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                            ) {
                                imageList.forEach {
                                    AsyncImage(
                                        modifier = Modifier
                                            .clickable {
                                                if (it.isDrawing) {
                                                    navigateToDrawing(it.id)
                                                } else {
                                                    navigateToGallery(notepad.id)
                                                }
                                            }
                                            .weight(1f)
                                            .height(200.dp),
                                        model = it.path,
                                        contentDescription = "note image",
                                        contentScale = ContentScale.Crop,
                                    )
                                }
                            }
                        }
                    }
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        SkTextField(
                            state = title,
                            placeholder = stringResource(R.string.feature_detail_title),
                            //  textStyle = MaterialTheme.typography.titleLarge,
//                            colors = TextFieldDefaults.colors(
//                                focusedContainerColor = Color.Transparent,
//                                unfocusedContainerColor = Color.Transparent,
//                                disabledContainerColor = Color.Transparent,
//                                focusedIndicatorColor = Color.Transparent,
//                                unfocusedIndicatorColor = Color.Transparent,
//                            ),
                            imeAction = ImeAction.Next,
                            modifier = Modifier
                                .padding(0.dp)
                                .weight(1f)
                                .testTag("title"),

                            )
                        if (notepad.isCheck) {
                            Box {
                                IconButton(onClick = { expandCheck = true }) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = "",
                                    )
                                }
                                DropdownMenu(
                                    expanded = expandCheck,
                                    onDismissRequest = { expandCheck = false },
                                ) {
                                    DropdownMenuItem(
                                        text = { Text(text = stringResource(R.string.feature_detail_hide_checkboxes)) },
                                        onClick = {
                                            hideCheckBoxes()
                                            expandCheck = false
                                        },
                                    )
                                    if (checkNote.isNotEmpty()) {
                                        DropdownMenuItem(
                                            text = { Text(text = stringResource(R.string.feature_detail_uncheck_all_items)) },
                                            onClick = {
                                                unCheckAllItems()
                                                expandCheck = false
                                            },
                                        )
                                        DropdownMenuItem(
                                            text = { Text(text = stringResource(R.string.feature_detail_delete_checked_items)) },
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
                }
                if (!notepad.isCheck) {
                    item {
                        SkTextField(
                            state = content,
//                            textStyle = MaterialTheme.typography.bodyMedium,
                            placeholder = stringResource(R.string.feature_detail_subject),
//                            colors = TextFieldDefaults.colors(
//                                focusedContainerColor = Color.Transparent,
//                                unfocusedContainerColor = Color.Transparent,
//                                disabledContainerColor = Color.Transparent,
//                                focusedIndicatorColor = Color.Transparent,
//                                unfocusedIndicatorColor = Color.Transparent,
//                            ),
                            imeAction = ImeAction.Next,
                            keyboardAction = { subjectFocus.freeFocus() },
                            //  keyboardActions = KeyboardActions(onDone = { subjectFocus.freeFocus() }),
                            modifier = Modifier
                                .fillMaxWidth()
                                .imePadding()
                                .focusRequester(subjectFocus)
                                .testTag("detail"),

                            )
                    }
                }
                if (notepad.isCheck) {
                    items(notCheckNote, key = { it.id }) {
                        NoteCheck(
                            noteCheckUiState = it,
                            onCheckChange = onCheckChange,
                            onCheckDelete = onCheckDelete,
                            onCheck = onCheck,
                            onNextCheck = addItem,
                        )
                    }

                    item {
                        TextButton(onClick = addItem) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "")

                            Text(text = stringResource(R.string.feature_detail_add_list_item))
                        }
                    }

                    if (checkNote.isNotEmpty()) {
                        item {
                            TextButton(onClick = { showCheckNote = !showCheckNote }) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(id = if (showCheckNote) R.drawable.feature_editscreen_baseline_expand_more_24 else R.drawable.feature_editscreen_baseline_expand_less_24),
                                    contentDescription = "",
                                )
                                Text(
                                    text = "${checkNote.size} ${stringResource(R.string.feature_detail_checked_items)}",
                                    style = MaterialTheme.typography.titleMedium,
                                )
                            }
                        }
                    }

                    if (showCheckNote) {
                        items(checkNote, key = { it.id }) {
                            NoteCheck(
                                noteCheckUiState = it,
                                onCheckChange = onCheckChange,
                                onCheckDelete = onCheckDelete,
                                onCheck = onCheck,
                                strickText = true,
                                onNextCheck = addItem,
                            )
                        }
                    }
                }
                itemsIndexed(items = notepad.voices, key = { _, item -> item.id }) { index, item ->
                    NoteVoicePlayer(
                        item,
                        playVoice = { playVoice(index) },
                        pauseVoice = pauseVoice,
                        delete = { deleteVoiceNote(index) },
                        color = sColor,
                    )
                }
                items(items = notepad.uris, key = { it.id }) {
                    NoteUri(uriState = it, sColor)
                }
                item {
                    FlowLayout2(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalSpacing = 8.dp,
                    ) {
                        if (notepad.reminder > 0) {
                            ReminderCard(
                                date = notepad.date,
                                interval = notepad.interval,
                                color = sColor,
                                style = MaterialTheme.typography.bodyLarge,
                                onClick = showNotificationDialog,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        notepad.labels.forEach {
                            LabelCard(
                                name = it,
                                color = sColor,
                                style = MaterialTheme.typography.bodyLarge,
                                onClick = onLabel,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        if (notepad.background > -1 && notepad.color > -1) {
                            Box(
                                modifier = Modifier
                                    .clickable { onColorClick() }
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(1.dp, Color.Gray, CircleShape)
                                    .size(30.dp),

                                )
                        }
                    }
                }
//                item {
//                    AsyncImage(modifier = Modifier.size(200.dp), model = "https://icon.horse/icon/fb.com", contentDescription = "")
//                }
            }

            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    modifier = Modifier.testTag("edit:more"),
                    onClick = { moreOptions() },
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AddBox,
                        contentDescription = "more note",
                    )
                }
                IconButton(
                    modifier = Modifier.testTag("edit:color"),
                    onClick = { onColorClick() },
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ColorLens,
                        contentDescription = "colors",
                    )
                }
                Row(
                    Modifier
                        .weight(1f)
                        .padding(end = 32.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = "${stringResource(R.string.feature_detail_edited)} ${notepad.lastEdit}",
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
                IconButton(
                    modifier = Modifier.testTag("edit:option"),
                    onClick = { noteOption() },
                ) {
                    Icon(
                        imageVector = Icons.Outlined.MoreVert,
                        contentDescription = "note options",
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun DetailScreenPreview() {
//    DetailScreen()
}


@Composable
fun NoteCheck(
    noteCheckUiState: NoteCheckUiState,
    onCheckChange: (String, Long) -> Unit = { _, _ -> },
    content: TextFieldState = rememberTextFieldState(),
    onCheckDelete: (Long) -> Unit = {},
    onCheck: (Boolean, Long) -> Unit = { _, _ -> },
    strickText: Boolean = false,
    onNextCheck: () -> Unit,
) {
    val mutableInteractionSource = remember {
        MutableInteractionSource()
    }
    LaunchedEffect(
        key1 = Unit,
        block = {
            if (noteCheckUiState.id == 1L) {
                mutableInteractionSource.emit(FocusInteraction.Focus())
            }
        },
    )
    val focused by mutableInteractionSource.collectIsFocusedAsState()
    val focusRequester = remember {
        FocusRequester()
    }

    LaunchedEffect(
        key1 = noteCheckUiState,
        block = {
            if (noteCheckUiState.focus) {
                focusRequester.requestFocus()
            } else {
                focusRequester.freeFocus()
            }
        },
    )

    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = noteCheckUiState.isCheck,
            onCheckedChange = { onCheck(it, noteCheckUiState.id) },
        )
        SkTextFieldCheck(
            modifier = Modifier
                .focusRequester(focusRequester)
                .weight(1f),
            text = noteCheckUiState.content,
             onTextChange = { onCheckChange(it, noteCheckUiState.id) },
            textStyle = if (strickText) TextStyle.Default.copy(textDecoration = TextDecoration.LineThrough) else TextStyle.Default,
            interactionSource = mutableInteractionSource,
            trailingIcon = {
                if (focused) {
                    IconButton(
                        onClick = {
                            onCheckDelete(noteCheckUiState.id)
                        },
                    ) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "")
                    }
                }
            },
            imeAction = ImeAction.Next,
            keyboardAction = { onNextCheck() },
        )
    }
}

@Composable
fun NoteVoicePlayer(
    noteVoiceUiState: NoteVoiceUiState,
    playVoice: () -> Unit = {},
    pauseVoice: () -> Unit = {},
    delete: () -> Unit = {},
    color: Color = Color.Red,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.padding(horizontal = 16.dp),
        color = color,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box {
                if (noteVoiceUiState.isPlaying) {
                    IconButton(onClick = pauseVoice) {
                        Icon(imageVector = Icons.Outlined.PauseCircle, contentDescription = "pause")
                    }
                } else {
                    IconButton(onClick = playVoice) {
                        Icon(imageVector = Icons.Outlined.PlayCircle, contentDescription = "play")
                    }
                }
            }
            LinearProgressIndicator(
                modifier = Modifier.weight(1f),
                progress = (noteVoiceUiState.currentProgress / noteVoiceUiState.length),
            )
            Text(text = noteVoiceUiState.length.toTime())
            IconButton(onClick = { delete() }) {
                Icon(imageVector = Icons.Outlined.Delete, contentDescription = "delete")
            }
        }
    }
}

@Preview
@Composable
fun NoteVoicePlayerPreview() {
    NoteVoicePlayer(
        NoteVoiceUiState(3, 4, "", length = Clock.System.now().toEpochMilliseconds()),

        )
}

@Composable
fun NoteUri(
    uriState: NoteUriState,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    val context = LocalContext.current

    ListItem(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = uriState.uri.toUri()
                }
                context.startActivity(intent)
            },
        colors = ListItemDefaults.colors(containerColor = color),
        leadingContent = {
            AsyncImage(
                modifier = Modifier.size(64.dp),
                model = uriState.icon,
                contentDescription = "icon",
            )
        },
        headlineContent = { Text(text = uriState.path) },
        supportingContent = { Text(text = uriState.uri, maxLines = 2) },
        shadowElevation = 8.dp,
        tonalElevation = 8.dp,
    )
}

@Preview
@Composable
fun NoteUriPreview() {
    NoteUri(uriState = NoteUriState(1, "", "Path", "akdkdk"))
}
