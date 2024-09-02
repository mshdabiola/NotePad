/*
 *abiola 2022
 */

package com.mshdabiola.main

import ArchiveTopAppBar
import LabelTopAppBar
import MainTopAppBar
import NoteCard
import SelectTopBar
import TrashTopAppBar
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.mshdabiola.analytics.LocalAnalyticsHelper
import com.mshdabiola.common.result.Result
import com.mshdabiola.designsystem.component.SkLoadingWheel
import com.mshdabiola.model.Note
import com.mshdabiola.model.NotePad
import com.mshdabiola.model.NoteType
import com.mshdabiola.ui.ColorDialog
import com.mshdabiola.ui.DateDialog
import com.mshdabiola.ui.FirebaseScreenLog
import com.mshdabiola.ui.NotificationDialogNew
import com.mshdabiola.ui.TimeDialog
import com.mshdabiola.ui.TrackScrollJank
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay

// import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun MainRoute(
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedVisibilityScope,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    navigateToDetail: (Long) -> Unit,
) {
    val mainViewModel: MainViewModel = hiltViewModel()

    FirebaseScreenLog(screen = "main_screen")
    val mainState = mainViewModel.mainState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit, block = {
        delay(2000)
        mainViewModel.deleteEmptyNote()
    })

    var showDialog by remember {
        mutableStateOf(false)
    }
    var showColor by remember {
        mutableStateOf(false)
    }
    var showRenameLabel by remember {
        mutableStateOf(false)
    }
    var showDeleteLabel by remember {
        mutableStateOf(false)
    }
//    val selectId = remember(mainState.value.notePads) {
//        mainState.value.notePads.filter { it.note.selected }.map { it.note.id.toInt() }
//            .toIntArray()
//    }
    val context = LocalContext.current
//    val send = {
//        val notePads = mainState.value.notePads.single { it.note.selected }
//        val intent = ShareCompat.IntentBuilder(context)
//            .setText(notePads.toString())
//            .setType("text/*")
//            .setChooserTitle("From Notepad")
//            .createChooserIntent()
//        context.startActivity(Intent(intent))
//    }
    MainScreen(
        sharedTransitionScope = sharedTransitionScope,
        animatedContentScope = animatedContentScope,
        modifier = modifier,
        mainState = mainState.value,
        navigateToEdit = navigateToDetail,
        onSelectedCard = mainViewModel::onSelectCard,
        onClearSelected = mainViewModel::clearSelected,
        setAllPin = mainViewModel::setPin,
        setAllAlarm = { showDialog = true },
        setAllColor = { showColor = true },
        setAllLabel = {
//            navigateToSelectLevel(selectId)
        },
        onCopy = mainViewModel::copyNote,
        onDelete = mainViewModel::setAllDelete,
        onArchive = mainViewModel::setAllArchive,
        onSend = {
            mainViewModel.clearSelected()
        },
        onRenameLabel = { showRenameLabel = true },
        onDeleteLabel = { showDeleteLabel = true },
        onEmptyTrash = mainViewModel::emptyTrash,
        //   items = timeline,

    )

//    val note = remember(mainState.value) {
//        val noOfSelected = mainState.value.notePads.count { it.note.selected }
//        if (noOfSelected == 1) {
//            mainState.value.notePads.singleOrNull { it.note.selected }?.note
//        } else {
//            null
//        }
//    }
//
    val colorIndex by remember { mutableStateOf(0) }
//        remember(mainState.value.notePads) {
//        val noOfSelected = mainState.value.notePads.count { it.note.selected }
//        if (noOfSelected == 1) {
//            mainState.value.notePads.singleOrNull { it.note.selected }?.note?.color
//        } else {
//            null
//        }
//    }

//    NotificationDialog(
//        showDialog,
//        onDismissRequest = { showDialog = false },
//        remainder = note?.reminder ?: -1,
//        interval = if (note?.interval == (-1L)) null else note?.interval,
//        onSetAlarm = mainViewModel::setAlarm,
//        onDeleteAlarm = mainViewModel::deleteAlarm,
//    )
    val dateDialogUiData = mainViewModel.dateTimeState.collectAsStateWithLifecycle()

    NotificationDialogNew(
        showDialog = showDialog,
        dateDialogUiData = dateDialogUiData.value,
        onDismissRequest = { showDialog = false },
        onSetAlarm = mainViewModel::setAlarm,
        onTimeChange = mainViewModel::onSetTime,
        onDateChange = mainViewModel::onSetDate,
        onIntervalChange = mainViewModel::onSetInterval,
        onDeleteAlarm = mainViewModel::deleteAlarm,
    )

    TimeDialog(
        state = mainViewModel.timePicker,
        showDialog = dateDialogUiData.value.showTimeDialog,
        onDismissRequest = mainViewModel::hideTime,
        onSetTime = mainViewModel::onSetTime,
    )
    DateDialog(
        state = mainViewModel.datePicker,
        showDialog = dateDialogUiData.value.showDateDialog,
        onDismissRequest = mainViewModel::hideDate,
        onSetDate = mainViewModel::onSetDate,
    )

    ColorDialog(
        show = showColor,
        onDismissRequest = { showColor = false },
        onColorClick = mainViewModel::setAllColor,
        currentColor = colorIndex ?: -1,
    )

    RenameLabelAlertDialog(
        show = showRenameLabel,
        label = "Name", // (mainState.value.noteType.type).name,
        onDismissRequest = { showRenameLabel = false },
        onChangeName = mainViewModel::renameLabel,
    )

    DeleteLabelAlertDialog(
        show = showDeleteLabel,
        onDismissRequest = { showDeleteLabel = false },
        onDelete = mainViewModel::deleteLabel,
    )
}

@OptIn(
    ExperimentalSharedTransitionApi::class,
)
@Composable
internal fun MainScreen(
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedVisibilityScope,
    mainState: MainState,
    navigateToEdit: (Long) -> Unit = {},
    navigateToSearch: () -> Unit = {},
    onSelectedCard: (Long) -> Unit = {},
    onClearSelected: () -> Unit = {},
    setAllPin: () -> Unit = {},
    setAllAlarm: () -> Unit = {},
    setAllColor: () -> Unit = {},
    setAllLabel: () -> Unit = {},
    onArchive: () -> Unit = {},
    onDelete: () -> Unit = {},
    onSend: () -> Unit = {},
    onCopy: () -> Unit = {},
    onRenameLabel: () -> Unit = {},
    onDeleteLabel: () -> Unit = {},
    onEmptyTrash: () -> Unit = {},
) {
    val state = rememberLazyListState()
    TrackScrollJank(scrollableState = state, stateName = "topic:screen")

    when (mainState) {
        is MainState.Success -> {
            MainContent(
                modifier = modifier,
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope,
                success = mainState,
                navigateToEdit = navigateToEdit,
                onSelectedCard = onSelectedCard,
                onClearSelected = onClearSelected,
                setAllPin = setAllPin,
                setAllAlarm = setAllAlarm,
                setAllColor = setAllColor,
                setAllLabel = setAllLabel,
                onArchive = onArchive,
                onDelete = onDelete,
                onSend = onSend,
                onCopy = onCopy,
                onRenameLabel = onRenameLabel,
                onDeleteLabel = onDeleteLabel,
                onEmptyTrash = onEmptyTrash,
            )
        }

        is MainState.Loading -> {
            LoadingState()
        }

        is MainState.Empty -> {
            EmptyState()
        }

        is MainState.Finish -> {}
    }

//    with(sharedTransitionScope) {
//        Box(
//            modifier = modifier
//                .testTag("main:screen")
//                .sharedBounds(
//                    sharedContentState = rememberSharedContentState("container"),
//                    animatedVisibilityScope = animatedContentScope,
//                ),
//        )
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("main:loading"),
        contentAlignment = Alignment.Center,
    ) {
        SkLoadingWheel(
            contentDesc = "Loading",
        )
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
            .testTag("main:empty"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Empty",
            textAlign = TextAlign.Center,
        )
    }
}

private fun noteUiStateItemsSize(
    topicUiState: Result<List<Note>>,
) = when (topicUiState) {
    is Result.Error -> 0 // Nothing
    is Result.Loading -> 1 // Loading bar
    is Result.Success -> topicUiState.data.size + 2
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun MainContent(
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedVisibilityScope,
    success: MainState.Success,
    navigateToEdit: (Long) -> Unit = {},
    onSelectedCard: (Long) -> Unit = {},
    onClearSelected: () -> Unit = {},
    setAllPin: () -> Unit = {},
    setAllAlarm: () -> Unit = {},
    setAllColor: () -> Unit = {},
    setAllLabel: () -> Unit = {},
    onArchive: () -> Unit = {},
    onDelete: () -> Unit = {},
    onSend: () -> Unit = {},
    onCopy: () -> Unit = {},
    onRenameLabel: () -> Unit = {},
    onDeleteLabel: () -> Unit = {},
    onEmptyTrash: () -> Unit = {},
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val pinScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val pinNotePad by remember(success.notePads) {
        derivedStateOf {
            success.notePads.partition { it.isPin }
        }
    }

    val noOfSelected = remember(success.notePads) {
        success.notePads.count { it.selected }
    }
    val isAllPin = remember(success.notePads) {
        success.notePads.filter { it.selected }
            .all { it.isPin }
    }
    var isGrid by rememberSaveable { mutableStateOf(true) }
    val onNoteClick: (Long) -> Unit = {
        if (noOfSelected > 0) {
            onSelectedCard(it)
        } else {
            navigateToEdit(it)
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(if (noOfSelected > 0) pinScrollBehavior.nestedScrollConnection else scrollBehavior.nestedScrollConnection),
        topBar = {
            if (noOfSelected > 0) {
                SelectTopBar(
                    selectNumber = noOfSelected,
                    isAllPin = isAllPin,
                    scrollBehavior = pinScrollBehavior,
                    onClear = onClearSelected,
                    onPin = setAllPin,
                    onNoti = setAllAlarm,
                    onColor = setAllColor,
                    onLabel = setAllLabel,
                    onArchive = onArchive,
                    onDelete = onDelete,
                    onSend = onSend,
                    onCopy = onCopy,
                )
            } else {
//
                when (success.noteType) {
                    NoteType.LABEL -> {
                        LabelTopAppBar(
                            label = "Label Name", // labels.single { it.id == currentNoteType.id }.label,
                            onNavigate = { },
                            scrollBehavior = scrollBehavior,
                            onDeleteLabel = onDeleteLabel,
                            onRenameLabel = onRenameLabel,
                        )
                    }

                    NoteType.TRASH -> {
                        TrashTopAppBar(
                            onNavigate = { },
                            scrollBehavior = scrollBehavior,
                            onEmptyTrash = onEmptyTrash,
                        )
                    }

                    NoteType.NOTE -> {
                        MainTopAppBar(
                            onNavigate = { },
                            scrollBehavior = scrollBehavior,
                            isGrid = isGrid,
                            onToggleGrid = { isGrid = !isGrid },
                        )
                    }

                    NoteType.REMAINDER -> {
                        ArchiveTopAppBar(
                            name = "Remainder",
                            onNavigate = { },
                            scrollBehavior = scrollBehavior,

                        )
                    }

                    NoteType.ARCHIVE -> {
                        ArchiveTopAppBar(
                            onNavigate = { },
                            scrollBehavior = scrollBehavior,
                        )
                    }
                }
            }
        },

    ) { paddingValues ->

        LazyVerticalStaggeredGrid(
            modifier = Modifier
                .padding(paddingValues)
                .padding(8.dp)
                .testTag("main:lazy"),
            columns = StaggeredGridCells.Fixed(if (isGrid) 2 else 1),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalItemSpacing = 8.dp,

        ) {
            if (pinNotePad.first.isNotEmpty()) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(R.string.feature_mainscreen_pin),
                    )
                }
            }
            noteItems(
                modifier = Modifier,
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope,
                items = pinNotePad.first.toImmutableList(),
                onNoteClick = onNoteClick,
                onSelectedCard = onSelectedCard,
            )

            if (pinNotePad.first.isNotEmpty() && pinNotePad.second.isNotEmpty()) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(R.string.feature_mainscreen_other),
                    )
                }
            }
            noteItems(
                modifier = Modifier,
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope,
                items = pinNotePad.second.toImmutableList(),
                onNoteClick = onNoteClick,
                onSelectedCard = onSelectedCard,
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
fun LazyStaggeredGridScope.noteItems(
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedVisibilityScope,
    items: List<NotePad>,
    onNoteClick: (Long) -> Unit,
    onSelectedCard: (Long) -> Unit,
) = items(
    items = items,
    key = { it.id },
    itemContent = { note ->
        val analyticsHelper = LocalAnalyticsHelper.current

        // with(sharedTransitionScope) {
        NoteCard(
            notePad = note,
            onCardClick = onNoteClick,
            onLongClick = onSelectedCard,
        )

        // }
    },
)

@Composable
fun RenameLabelAlertDialog(
    show: Boolean = false,
    label: String = "Label",
    onDismissRequest: () -> Unit = {},
    onChangeName: (String) -> Unit = {},
) {
    var name by remember(label) {
        mutableStateOf(label)
    }

    AnimatedVisibility(visible = show) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text(text = stringResource(id = R.string.feature_mainscreen_rename_label)) },
            text = {
                TextField(value = name, onValueChange = { name = it })
            },
            confirmButton = {
                Button(onClick = {
                    onDismissRequest()
                    onChangeName(name)
                }) {
                    Text(text = stringResource(R.string.feature_mainscreen_rename))
                }
            },
            dismissButton = {
                TextButton(onClick = { onDismissRequest() }) {
                    Text(text = stringResource(R.string.feature_mainscreen_cancel))
                }
            },
        )
    }
}

@Preview
@Composable
fun RenameLabelPreview() {
    RenameLabelAlertDialog(show = true)
}

@Composable
fun DeleteLabelAlertDialog(
    show: Boolean = false,
    onDismissRequest: () -> Unit = {},
    onDelete: () -> Unit = {},
) {
    AnimatedVisibility(visible = show) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text(text = "Rename Label") },
            text = {
                Text(text = " We'll delete the label and remove it from all of from all of your keep notes. Your notes won't be deleted")
            },
            confirmButton = {
                TextButton(onClick = {
                    onDismissRequest()
                    onDelete()
                }) {
                    Text(text = "Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { onDismissRequest() }) {
                    Text(text = "Cancel")
                }
            },
        )
    }
}

@Preview
@Composable
fun DeleteLabelPreview() {
    DeleteLabelAlertDialog(show = true)
}

@Composable
fun Loader(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.feature_mainscreen_note_taking))
    LottieAnimation(
        modifier = modifier,
        composition = composition,
        restartOnPlay = true,
        iterations = 200,
    )
}
