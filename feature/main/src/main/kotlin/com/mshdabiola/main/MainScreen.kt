/*
 *abiola 2022
 */

package com.mshdabiola.main

import MainTopBar
import NoteCard
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.mshdabiola.analytics.LocalAnalyticsHelper
import com.mshdabiola.designsystem.component.NoteLoadingWheel
import com.mshdabiola.model.NoteDisplayCategory
import com.mshdabiola.model.NotePad
import com.mshdabiola.ui.TrackScrollJank
import kotlinx.collections.immutable.toImmutableList
import com.mshdabiola.designsystem.R as Rd

// import org.koin.androidx.compose.koinViewModel
@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun MainScreen(
    modifier: Modifier,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedVisibilityScope,
    mainState: MainState,
    navigateToNoteEditor: (Long) -> Unit = {},
    onNoteSelected: (Long) -> Unit = {},

    onDisplayModeChange: () -> Unit = {},
    onHamburgerMenuClick: () -> Unit = {},

    onClearSelection: () -> Unit = {},
    onPinNotes: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onSelectColor: () -> Unit = {},
    onLabelNotes: () -> Unit = {},
    onArchive: () -> Unit = {},
    onDeleteNotes: () -> Unit = {},
    onShareNote: () -> Unit = {},
    onCopyNote: () -> Unit = {},

    onSearchClick: () -> Unit = {},
    onLabelNameChange: () -> Unit = {},
    onDeleteLabel: () -> Unit = {},

    onDeleteAllTrash: () -> Unit = {},

    ) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val state = rememberLazyListState()
    TrackScrollJank(scrollableState = state, stateName = "topic:screen")

    when (mainState) {
        is MainState.Loading -> {
            LoadingState()
        }

        is MainState.Success -> {
            val onNoteClick: (Long) -> Unit = {
                if (mainState.selectState != null) {
                    onNoteSelected(it)
                } else {
                    navigateToNoteEditor(it)
                }
            }
            Scaffold(
                modifier = modifier
                    .testTag("main:list")
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    MainTopBar(
                        noteDisplayCategory = mainState.noteDisplayCategory,
                        isGrid = mainState.isGrid,
                        selectState = mainState.selectState,
                        onDisplayModeChange = onDisplayModeChange,
                        onHamburgerMenuClick = onHamburgerMenuClick,
                        onClearSelection = onClearSelection,
                        onPinNotes = onPinNotes,
                        onNotificationClick = onNotificationClick,
                        onSelectColor = onSelectColor,
                        onLabelNotes = onLabelNotes,
                        onArchive = onArchive,
                        onDeleteNotes = onDeleteNotes,
                        onShareNote = onShareNote,
                        onCopyNote = onCopyNote,
                        onSearchClick = onSearchClick,
                        onLabelNameChange = onLabelNameChange,
                        onDeleteLabel = onDeleteLabel,
                        onDeleteAllTrash = onDeleteAllTrash,

                        )
                },

                ) { paddingValues ->

                LazyVerticalStaggeredGrid(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(16.dp),
                    columns = StaggeredGridCells.Fixed(if (mainState.isGrid) 2 else 1),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalItemSpacing = 8.dp,

                    ) {
//
                    if (mainState.unPinNotePads.isEmpty() && mainState.pinNotePads.isEmpty()) {
                        item(span = StaggeredGridItemSpan.FullLine) {
                            EmptyState(noteDisplayCategory = mainState.noteDisplayCategory)
                        }
                    }
                    if (mainState.pinNotePads.isNotEmpty()) {
                        item(span = StaggeredGridItemSpan.FullLine) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = stringResource(Rd.string.modules_designsystem_pin),
                            )
                        }
                    }
                    noteItems(
                        modifier = Modifier,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedContentScope = animatedContentScope,
                        items = mainState.pinNotePads.toImmutableList(),
                        onNoteClick = onNoteClick,
                        onSelectedCard = onNoteSelected,
                        setOfSelected = mainState.selectState?.setOfSelected ?: emptySet(),
                    )

                    if (mainState.pinNotePads.isNotEmpty() && mainState.unPinNotePads.isNotEmpty()) {
                        item(span = StaggeredGridItemSpan.FullLine) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = stringResource(Rd.string.modules_designsystem_other),
                            )
                        }
                    }
                    noteItems(
                        modifier = Modifier,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedContentScope = animatedContentScope,
                        items = mainState.unPinNotePads.toImmutableList(),
                        onNoteClick = onNoteClick,
                        onSelectedCard = onNoteSelected,
                        setOfSelected = mainState.selectState?.setOfSelected ?: emptySet(),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Preview
@Composable
internal fun MainScreenPreview() {
    val mainState = MainState.Success(
        isGrid = true,
        labelName = "Sample Label",
        pinNotePads = listOf(
            NotePad(id = 1, title = "Pinned Note 1", detail = "Content 1", isPin = true),
            NotePad(id = 2, title = "Pinned Note 2", detail = "Content 2", isPin = true),
        ),
        unPinNotePads = listOf(
            NotePad(id = 3, title = "Unpinned Note 1", detail = "Content 3"),
            NotePad(id = 4, title = "Unpinned Note 2", detail = "Content 4"),
            NotePad(id = 5, title = "Unpinned Note 1", detail = "Content 3"),
            NotePad(id = 6, title = "Unpinned Note 2", detail = "Content 4"),
            NotePad(
                id = 7,
                title = "Unpinned Note 1", detail = "Content 3",
            ),
            NotePad(id = 8, title = "Unpinned Note 2", detail = "Content 4"),
            NotePad(
                id = 9,
                title = "Unpinned Note 1", detail = "Content 3",
            ),
            NotePad(id = 10, title = "Unpinned Note 2", detail = "Content 4"),
            NotePad(id = 11, title = "Unpinned Note 1", detail = "Content 3"),
            NotePad(id = 12, title = "Unpinned Note 2", detail = "Content 4"),
        ),
        noteDisplayCategory = NoteDisplayCategory(),
        selectState = SelectState(colorIndex = 0, isAllPin = true, setOfSelected = setOf(1L)),
    )
    SharedTransitionScope {
        AnimatedVisibility(visible = true) {
            MainScreen(
                modifier = Modifier.fillMaxSize(),
                sharedTransitionScope = this@SharedTransitionScope,
                animatedContentScope = this,
                mainState = mainState,
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
        NoteLoadingWheel(
            contentDesc = "Loading",
        )
    }
}

@Composable
private fun EmptyState(
    modifier: Modifier = Modifier,
    noteDisplayCategory: NoteDisplayCategory = NoteDisplayCategory(),
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
            .testTag("main:empty"),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(Rd.raw.modules_designsystem_note_taking))
        LottieAnimation(
            modifier = modifier,
            composition = composition,
            restartOnPlay = true,
            iterations = 200,
        )
        Text(
            text = stringResource(Rd.string.modules_designsystem_empty_notes),
            textAlign = TextAlign.Center,
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
fun LazyStaggeredGridScope.noteItems(
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedVisibilityScope,
    items: List<NotePad>,
    setOfSelected: Set<Long>,
    onNoteClick: (Long) -> Unit,
    onSelectedCard: (Long) -> Unit,
    sharedName: String = "note",
) = items(
    items = items,
    key = { it.id },
    itemContent = { note ->
        val analyticsHelper = LocalAnalyticsHelper.current

        with(sharedTransitionScope) {
            NoteCard(
                modifier = modifier.sharedBounds(
                    sharedContentState = rememberSharedContentState("${sharedName}_${note.id}"),
                    animatedVisibilityScope = animatedContentScope,

                    ),
                isSelect = setOfSelected.contains(note.id),
                notePad = note,
                onCardClick = onNoteClick,
                onLongClick = onSelectedCard,
            )
        }
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
            title = { Text(text = stringResource(id = Rd.string.modules_designsystem_rename_label)) },
            text = {
                TextField(value = name, onValueChange = { name = it })
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDismissRequest()
                        onChangeName(name)
                    },
                ) {
                    Text(text = stringResource(Rd.string.modules_designsystem_rename))
                }
            },
            dismissButton = {
                TextButton(onClick = { onDismissRequest() }) {
                    Text(text = stringResource(Rd.string.modules_designsystem_cancel))
                }
            },
        )
    }
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
            title = { Text(text = stringResource(Rd.string.modules_designsystem_rename_label)) },
            text = {
                Text(text = stringResource(Rd.string.modules_designsystem_rename_label_detail))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDismissRequest()
                        onDelete()
                    },
                ) {
                    Text(text = stringResource(Rd.string.modules_designsystem_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { onDismissRequest() }) {
                    Text(text = stringResource(Rd.string.modules_designsystem_cancel))
                }
            },
        )
    }
}
