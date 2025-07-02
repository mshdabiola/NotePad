/*
 *abiola 2022
 */

package com.mshdabiola.detail

import android.content.Intent
import androidx.compose.animation.ExperimentalSharedTransitionApi
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
import androidx.compose.foundation.text.input.rememberTextFieldState
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.core.net.toUri
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import coil3.compose.AsyncImage
import com.mshdabiola.designsystem.component.NoteTextField
import com.mshdabiola.designsystem.icon.NoteIcon
import com.mshdabiola.model.Label
import com.mshdabiola.model.Note
import com.mshdabiola.model.NoteDrawing
import com.mshdabiola.model.NoteImage
import com.mshdabiola.model.NotePad
import com.mshdabiola.model.NoteType
import com.mshdabiola.model.NoteUri
import com.mshdabiola.model.NoteVoice
import com.mshdabiola.ui.BoardViewer
import com.mshdabiola.ui.FlowLayout2
import com.mshdabiola.ui.LabelCard
import com.mshdabiola.ui.LocalSharedStScope
import com.mshdabiola.ui.PreviewContainer
import com.mshdabiola.ui.ReminderCard
import com.mshdabiola.ui.toTime
import com.mshdabiola.designsystem.R as Rd

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    modifier: Modifier = Modifier,
    state: DetailState,
    onBackClick: () -> Unit = {},
    onCheckDelete: (Long) -> Unit = {},
//    onCheck: (Boolean, Long) -> Unit = { _, _ -> },
    addItem: () -> Unit = {},
    playVoice: (Int) -> Unit = {},
    pauseVoice: () -> Unit = {},
    moreOptions: () -> Unit = {},
    noteOption: () -> Unit = {},
//    unCheckAllItems: () -> Unit = {},
    deleteCheckItems: () -> Unit = {},
    hideCheckBoxes: () -> Unit = {},
    pinNote: () -> Unit = {},
    onLabel: () -> Unit = {},
    onColorClick: () -> Unit = {},
    onNotification: () -> Unit = {},
    showNotificationDialog: () -> Unit = {},
    onArchive: () -> Unit = {},
    deleteVoiceNote: (Int) -> Unit = {},
    navigateToGallery: (Long, Int, Int, String) -> Unit = { _, _, _, _ -> },
    navigateToDrawing: (Long?) -> Unit = {},
) {
    var expandCheck by remember {
        mutableStateOf(false)
    }

    val notepad = remember(state.notePad) {
        state.notePad
    }

    val subjectFocus = remember {
        FocusRequester()
    }

//    val checkNote by remember(state.checks) {
//        derivedStateOf { state.checks.filter { it.isCheck } }
//    }
//    val notCheckNote by remember(state.checks) {
//        derivedStateOf { state.checks.filter { !it.isCheck } }
//    }
    var showCheckNote by remember {
        mutableStateOf(false)
    }

    val bg = if (notepad.note.background != -1) {
        Color.Transparent
    } else {
        if (notepad.note.color != -1) {
            NoteIcon.noteColors[notepad.note.color]
        } else {
            MaterialTheme.colorScheme.background
        }
    }
    val color = NoteIcon.noteColors.getOrNull(notepad.note.color) ?: Color.Transparent

    val sColor = if (notepad.note.background != -1) {
        NoteIcon.background[notepad.note.background].fgColor
    } else {
        MaterialTheme.colorScheme.secondaryContainer
    }

    val painter = if (notepad.note.background != -1) {
        rememberVectorPainter(image = ImageVector.vectorResource(id = NoteIcon.background[notepad.note.background].bg))
    } else {
        null
    }

    val images = remember(notepad.images, notepad.drawings) {
        notepad.getVisuals().reversed().chunked(3)
    }

//    LaunchedEffect(
//        key1 = notepad,
//        block = {
//            if (notepad.focus) {
//                subjectFocus.requestFocus()
//            }
//        },
//    )
    val sharedTransitionScope = LocalSharedStScope.current
    val animatedContentScope = LocalNavAnimatedContentScope.current
    with(sharedTransitionScope) {
        Scaffold(
            containerColor = bg,
            modifier = modifier
                .sharedBounds(
                    sharedContentState = rememberSharedContentState("note_${notepad.note.id}"),
                    animatedVisibilityScope = animatedContentScope,
                )
                .drawBehind {
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
                        IconButton(
                            modifier = Modifier.testTag("detail:back"),
                            onClick = { onBackClick() },
                        ) {
                            Icon(
                                imageVector = NoteIcon.ArrowBack,
                                contentDescription = "back",
                            )
                        }
                    },

                    actions = {
                        IconButton(onClick = { pinNote() }) {
                            Icon(
                                modifier = Modifier.testTag("detail:pin"),

                                imageVector = if (notepad.note.isPin) NoteIcon.PushPinD else NoteIcon.PushPin,
                                contentDescription = "pin",
                            )
                        }
                        IconButton(onClick = { onNotification() }) {
                            Icon(
                                modifier = Modifier.testTag("detail:notification"),

                                imageVector = NoteIcon.NotificationAdd,
                                contentDescription = "notification",
                            )
                        }
                        IconButton(onClick = { onArchive() }) {
                            Icon(
                                modifier = Modifier.testTag("detail:archive"),

                                imageVector = if (notepad.note.noteType == NoteType.ARCHIVE) NoteIcon.Unarchive else NoteIcon.Archive,
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
                        .testTag("detail:list"),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    if (images.isNotEmpty()) {
                        item(images) {
                            images.forEach { imageList ->
                                Row(
                                    modifier = Modifier
                                        .testTag("detail:images")
                                        .fillMaxWidth()
                                        .height(200.dp),
                                ) {
                                    imageList.forEachIndexed { index, it ->
                                        when (it) {
                                            is NoteImage -> {
                                                AsyncImage(
                                                    modifier = Modifier
                                                        .clickable {
                                                            navigateToGallery(
                                                                notepad.note.id,
                                                                index,
                                                                imageList.size,
                                                                it.path,
                                                            )
                                                        }
                                                        .sharedElement(
                                                            sharedContentState = rememberSharedContentState(
                                                                "image_$index",
                                                            ),
                                                            animatedVisibilityScope = animatedContentScope,

                                                        )
                                                        .weight(1f)
                                                        .height(200.dp),
                                                    model = it.path,
                                                    contentDescription = "note image",
                                                    contentScale = ContentScale.Crop,
                                                )
                                            }

                                            is NoteDrawing -> {
                                                BoardViewer(
                                                    modifier = Modifier
                                                        .clickable {
                                                            navigateToDrawing(it.id)
                                                        }
                                                        .sharedElement(
                                                            sharedContentState = rememberSharedContentState(
                                                                "drwaing_$index",
                                                            ),
                                                            animatedVisibilityScope = animatedContentScope,

                                                        )
                                                        .weight(1f)
                                                        .height(200.dp),
                                                    drawingPaths = it.drawingPaths,
                                                )
                                            }
                                        }
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
                            NoteTextField(
                                state = state.title,
                                placeholder = stringResource(Rd.string.modules_designsystem_title),
                                imeAction = ImeAction.Next,
                                modifier = Modifier
                                    .padding(0.dp)
                                    .weight(1f)
                                    .testTag("detail:title"),

                            )
                            if (notepad.note.isCheck) {
                                Box {
                                    IconButton(
                                        modifier = Modifier.testTag("detail:morecheck"),
                                        onClick = { expandCheck = true },
                                    ) {
                                        Icon(
                                            imageVector = NoteIcon.MoreVert,
                                            contentDescription = "",
                                        )
                                    }
                                    DropdownMenu(
                                        expanded = expandCheck,
                                        onDismissRequest = { expandCheck = false },
                                    ) {
                                        DropdownMenuItem(
                                            modifier = Modifier.testTag("detail:hidecheck"),
                                            text = { Text(text = stringResource(Rd.string.modules_designsystem_hide_checkboxes)) },
                                            onClick = {
                                                hideCheckBoxes()
                                                expandCheck = false
                                            },
                                        )
                                        if (state.checks.isNotEmpty()) {
                                            DropdownMenuItem(
                                                modifier = Modifier.testTag("detail:uncheckall"),
                                                text = { Text(text = stringResource(Rd.string.modules_designsystem_uncheck_all_items)) },
                                                onClick = {
                                                    val checks = state.checks
                                                        .map { it.copy(isCheck = false) }
                                                    state.checks.clear()
                                                    state.unChecks.addAll(checks)
                                                    state.unChecks.sortBy { it.id }
//                                                unCheckAllItems()
                                                    expandCheck = false
                                                },
                                            )
                                            DropdownMenuItem(
                                                modifier = Modifier.testTag("detail:deletecheck"),
                                                text = { Text(text = stringResource(Rd.string.modules_designsystem_delete_checked_items)) },
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
                    if (!notepad.note.isCheck) {
                        item {
                            NoteTextField(
                                state = state.detail,
                                placeholder = stringResource(Rd.string.modules_designsystem_subject),
                                imeAction = ImeAction.None,
                                keyboardAction = { subjectFocus.freeFocus() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .imePadding()
                                    .focusRequester(subjectFocus)
                                    .testTag("detail:content"),

                            )
                        }
                    }
                    if (notepad.note.isCheck) {
                        itemsIndexed(state.unChecks, key = { i, it -> it.id }) { index, item ->
                            NoteCheckUi(
                                noteCheckUiState = item,
                                onCheckDelete = {
                                    onCheckDelete(it)
                                    state.unChecks.removeAt(index)
                                },
                                onCheck = {
                                    val value = state.unChecks.removeAt(index)
                                    state.checks.add(value.copy(isCheck = true))
                                    state.checks.sortBy { it.id }
                                },
                                onNextCheck = addItem,
                            )
                        }

                        item {
                            TextButton(onClick = addItem) {
                                Icon(imageVector = NoteIcon.Add, contentDescription = "")

                                Text(text = stringResource(Rd.string.modules_designsystem_add_list_item))
                            }
                        }

                        if (state.checks.isNotEmpty()) {
                            item {
                                TextButton(onClick = { showCheckNote = !showCheckNote }) {
                                    Icon(
                                        imageVector = if (showCheckNote) NoteIcon.More else NoteIcon.Less,
                                        contentDescription = "",
                                    )
                                    Text(
                                        text = "${state.checks.size} ${stringResource(Rd.string.modules_designsystem_checked_items)}",
                                        style = MaterialTheme.typography.titleMedium,
                                    )
                                }
                            }
                        }

                        if (showCheckNote) {
                            itemsIndexed(state.checks, key = { i, it -> it.id }) { index, item ->
                                NoteCheckUi(
                                    noteCheckUiState = item,
                                    onCheckDelete = {
                                        onCheckDelete(it)
                                        state.checks.removeAt(index)
                                    },
                                    onCheck = {
                                        val value = state.checks.removeAt(index)
                                        state.unChecks.add(value.copy(isCheck = false))
                                        state.unChecks.sortBy { it.id }
                                    },
                                    strickText = true,
                                    onNextCheck = {},
                                )
                            }
                        }
                    }
                    itemsIndexed(
                        items = notepad.voices,
                        key = { _, item -> item.id },
                    ) { index, item ->
                        val playerState =
                            if (state.playerState != null && state.playerState.indexPlaying == index) {
                                state.playerState
                            } else {
                                PlayerState()
                            }
                        NoteVoicePlayer(
                            item,
                            playVoice = { playVoice(index) },
                            pauseVoice = pauseVoice,
                            delete = { deleteVoiceNote(index) },
                            color = sColor,
                            isPlay = playerState.isPlaying,
                            currentProgress = playerState.currentPosition,
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
                            notepad.notification?.let {
                                ReminderCard(
                                    notification = it,
                                    color = sColor,
                                    style = MaterialTheme.typography.bodyLarge,
                                    onClick = showNotificationDialog,
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            notepad.labels.forEach {
                                LabelCard(
                                    name = it.label,
                                    color = sColor,
                                    style = MaterialTheme.typography.bodyLarge,
                                    onClick = onLabel,
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            if (notepad.note.background > -1 && notepad.note.color > -1) {
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
                        modifier = Modifier.testTag("detail:more"),
                        onClick = { moreOptions() },
                    ) {
                        Icon(
                            imageVector = NoteIcon.AddBox,
                            contentDescription = "more note",
                        )
                    }
                    IconButton(
                        modifier = Modifier.testTag("detail:colors"),
                        onClick = { onColorClick() },
                    ) {
                        Icon(
                            imageVector = NoteIcon.ColorLens,
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
                            text = "${stringResource(Rd.string.modules_designsystem_edited)} ${state.updateAt}",
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                    IconButton(
                        modifier = Modifier.testTag("detail:options"),
                        onClick = { noteOption() },
                    ) {
                        Icon(
                            imageVector = NoteIcon.MoreVert,
                            contentDescription = "note options",
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NoteCheckUi(
    noteCheckUiState: NoteCheckUiState,
    onCheckDelete: (Long) -> Unit = {},
    onCheck: (Boolean) -> Unit = { },
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
            onCheckedChange = { onCheck(it) },
        )
        NoteTextField(
            modifier = Modifier
                .focusRequester(focusRequester)
                .weight(1f),
            state = noteCheckUiState.content,
            textStyle = if (strickText) TextStyle.Default.copy(textDecoration = TextDecoration.LineThrough) else TextStyle.Default,
            interactionSource = mutableInteractionSource,
            trailingIcon = {
                if (focused) {
                    IconButton(
                        onClick = {
                            onCheckDelete(noteCheckUiState.id)
                        },
                    ) {
                        Icon(imageVector = NoteIcon.Clear, contentDescription = "")
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
    noteVoiceUiState: NoteVoice,
    playVoice: () -> Unit = {},
    pauseVoice: () -> Unit = {},
    delete: () -> Unit = {},
    color: Color = Color.Red,
    isPlay: Boolean = false,
    currentProgress: Int = 0,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.padding(horizontal = 16.dp),
        color = color,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box {
                if (isPlay) {
                    IconButton(onClick = pauseVoice) {
                        Icon(imageVector = NoteIcon.PauseCircle, contentDescription = "pause")
                    }
                } else {
                    IconButton(onClick = playVoice) {
                        Icon(imageVector = NoteIcon.PlayCircle, contentDescription = "play")
                    }
                }
            }
            LinearProgressIndicator(
                progress = { (currentProgress.toFloat() / noteVoiceUiState.length) },
                modifier = Modifier.weight(1f),
            )
            Text(text = noteVoiceUiState.length.toTime())
            IconButton(onClick = { delete() }) {
                Icon(imageVector = NoteIcon.Delete, contentDescription = "delete")
            }
        }
    }
}

@Preview
@Composable
fun NoteVoicePlayerPreview() {
    NoteVoicePlayer(
        NoteVoice(3, 4, "", length = 14),

    )
}

@Composable
fun NoteUri(
    uriState: NoteUri,
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
    NoteUri(uriState = NoteUri(1, "", "Path", "akdkdk"))
}

