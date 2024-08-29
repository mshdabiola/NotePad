/*
 *abiola 2022
 */

package com.mshdabiola.main

import ArchiveTopAppBar
import LabelTopAppBar
import MainTopAppBar
import SelectTopBar
import TrashTopAppBar
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Brush
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.KeyboardVoice
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.DraggableScrollbar
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.rememberDraggableScroller
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.scrollbarState
import com.mshdabiola.analytics.LocalAnalyticsHelper
import com.mshdabiola.common.result.Result
import com.mshdabiola.model.Note
import com.mshdabiola.model.NoteType
import com.mshdabiola.ui.FirebaseScreenLog
import com.mshdabiola.ui.NotifySnacker
import com.mshdabiola.ui.TrackScrollJank
import com.mshdabiola.ui.logNoteOpened
import com.mshdabiola.ui.state.LabelUiState
import com.mshdabiola.ui.state.NotePadUiState
import com.mshdabiola.ui.state.NoteTypeUi
import com.mshdabiola.ui.state.Notify
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

// import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun MainRoute(
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedVisibilityScope,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    navigateToDetail: (Long) -> Unit,
) {
    val mainViewModel: MainViewModel = hiltViewModel()

    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            it?.let {
//                showImageDialog = false
//                val time = System.currentTimeMillis()
//                saveImage(it, time)
//                navigateToEdit(-3, "image text", time)
            }
        },
    )

    val snapPictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = {
            if (it) {
                // navigateToEdit(-3, "image text", photoId)
            }
        },
    )

    val voiceLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
//            it.data?.let { intent ->
//                val strArr = intent.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
//                val audiouri = intent.data
//
//                if (audiouri != null) {
//                    val time = System.currentTimeMillis()
//                    saveVoice(audiouri, time)
//
//                    navigateToEdit(-4, strArr?.joinToString() ?: "", time)
//                }
//            }
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

    FirebaseScreenLog(screen = "main_screen")
    val mainState = mainViewModel.mainState.collectAsStateWithLifecycle()

    MainScreen(
        sharedTransitionScope = sharedTransitionScope,
        animatedContentScope = animatedContentScope,
        modifier = modifier,
        mainState = feedNote.value,
        navigateToDetail = navigateToDetail,
        //   items = timeline,

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
    mainState: Result<List<Note>>,
    navigateToDetail: (Long) -> Unit = {},
) {
    val state = rememberLazyListState()
    TrackScrollJank(scrollableState = state, stateName = "topic:screen")

    with(sharedTransitionScope) {
        Box(
            modifier = modifier
                .testTag("main:screen")
                .sharedBounds(
                    sharedContentState = rememberSharedContentState("container"),
                    animatedVisibilityScope = animatedContentScope,
                ),
        ) {
            LazyColumn(
                state = state,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .testTag("main:list"),
            ) {
                item {
                    // Spacer(Modifier.windowInsetsTopHeight(WindowInsets.safeDrawing))
                }
                when (mainState) {
                    is Result.Loading -> item {
                        LoadingState()
                    }

                    is Result.Error -> TODO()
                    is Result.Success -> {
                        if (mainState.data.isEmpty()) {
                            item {
                                EmptyState()
                            }
                        } else {
                            noteItems(
                                modifier = Modifier,
                                sharedTransitionScope = sharedTransitionScope,
                                animatedContentScope = animatedContentScope,
                                items = mainState.data,
                                onNoteClick = { navigateToDetail(it) },
                            )
                        }
                    }
                }
                item {
                    Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.safeDrawing))
                }
            }
            val itemsAvailable = noteUiStateItemsSize(mainState)
            val scrollbarState = state.scrollbarState(
                itemsAvailable = itemsAvailable,
            )
            state.DraggableScrollbar(
                modifier = Modifier
                    .fillMaxHeight()
                    .windowInsetsPadding(WindowInsets.systemBars)
                    .padding(horizontal = 2.dp)
                    .align(Alignment.CenterEnd),
                state = scrollbarState,
                orientation = Orientation.Vertical,
                onThumbMoved = state.rememberDraggableScroller(
                    itemsAvailable = itemsAvailable,
                ),
            )
        }
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("main:loading"),
        contentAlignment = Alignment.Center,
    ) {
//        SkLoadingWheel(
//            contentDesc = stringResource(Res.string.features_main_loading),
//        )
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
    }
}

private fun noteUiStateItemsSize(
    topicUiState: Result<List<Note>>,
) = when (topicUiState) {
    is Result.Error -> 0 // Nothing
    is Result.Loading -> 1 // Loading bar
    is Result.Success -> topicUiState.data.size + 2
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(
    modifier: Modifier = Modifier,
    notePads: ImmutableList<NotePadUiState>,
    labels: ImmutableList<LabelUiState>,
    isGrid: Boolean = true,
    navigateToEdit: (Long, String, Long) -> Unit = { _, _, _ -> },
    currentNoteType: NoteTypeUi = NoteTypeUi(),
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
    onToggleGrid: () -> Unit = {},
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val pinScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val pinNotePad by remember(notePads) {
        derivedStateOf {
            notePads.partition { it.note.isPin }
        }
    }

    val noOfSelected = remember(notePads) {
        notePads.count { it.note.selected }
    }
    val isAllPin = remember(notePads) {
        notePads.filter { it.note.selected }
            .all { it.note.isPin }
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
                when (currentNoteType.type) {
                    NoteType.LABEL -> {
                        LabelTopAppBar(
                            label = labels.single { it.id == currentNoteType.id }.label,
                            onSearch = navigateToSearch,
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
                            navigateToSearch = navigateToSearch,
                            onNavigate = { },
                            scrollBehavior = scrollBehavior,
                            isGrid = isGrid,
                            onToggleGrid = onToggleGrid,
                        )
                    }

                    NoteType.REMAINDER -> {
                        ArchiveTopAppBar(
                            name = "Remainder",
                            onSearch = navigateToSearch,
                            onNavigate = { },
                            scrollBehavior = scrollBehavior,

                            )
                    }

                    NoteType.ARCHIVE -> {
                        ArchiveTopAppBar(
                            onSearch = navigateToSearch,
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
            items(pinNotePad.first) { notePadUiState ->
                com.mshdabiola.ui.NoteCard(
                    notePad = notePadUiState,
                    onCardClick = {
                        if (noOfSelected > 0) {
                            onSelectedCard(it)
                        } else {
                            navigateToEdit(it, "", 0)
                        }
                    },
                    onLongClick = onSelectedCard,
                )
            }

            if (pinNotePad.first.isNotEmpty() && pinNotePad.second.isNotEmpty()) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(R.string.feature_mainscreen_other),
                    )
                }
            }
            items(pinNotePad.second) { notePadUiState ->
                com.mshdabiola.ui.NoteCard(
                    notePad = notePadUiState,
                    onCardClick = {
                        if (noOfSelected > 0) {
                            onSelectedCard(it)
                        } else {
                            navigateToEdit(it, "", 0)
                        }
                    },
                    onLongClick = onSelectedCard,
                )
            }
        }
    }

}

@OptIn(ExperimentalSharedTransitionApi::class)
fun LazyListScope.noteItems(
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedVisibilityScope,
    items: List<Note>,
    onNoteClick: (Long) -> Unit,
) = items(
    items = items,
    key = { 1 },
    itemContent = { note ->
        val analyticsHelper = LocalAnalyticsHelper.current

        // with(sharedTransitionScope) {
        NoteCard(
            modifier = modifier,
//                    .sharedBounds(
//                        sharedContentState = rememberSharedContentState("item"),
//                        animatedVisibilityScope = animatedContentScope,
//                    ),
            noteUiState = note,
            onClick = {
                analyticsHelper.logNoteOpened(note.id.toString())
//                onNoteClick(note.id)
            },
        )
        // }
    },
)

@Composable
fun NoteCard(
    modifier: Modifier,
    noteUiState: Note,
    onClick: () -> Unit,
) {
}
