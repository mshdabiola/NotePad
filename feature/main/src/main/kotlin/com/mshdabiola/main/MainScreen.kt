/*
 *abiola 2022
 */

package com.mshdabiola.main

import MainTopBar
import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.mshdabiola.designsystem.component.NoteButton
import com.mshdabiola.designsystem.component.NoteLoadingWheel
import com.mshdabiola.model.NoteDisplayCategory
import com.mshdabiola.model.createFakeNotePads
import com.mshdabiola.ui.NoteCard
import com.mshdabiola.ui.PreviewContainer
import com.mshdabiola.designsystem.R as Rd

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun MainScreen(
    modifier: Modifier = Modifier,
    mainState: MainState,
    navigateToNoteEditor: (Long, Int, Int) -> Unit = { _, _, _ -> },
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
    onDeletedForever: () -> Unit = {},
    onRestore: () -> Unit = {},

    onSearchClick: () -> Unit = {},
    onLabelNameChange: () -> Unit = {},
    onDeleteLabel: () -> Unit = {},

    onDeleteAllTrash: () -> Unit = {},

) {
    val scrollBehavior = if ((mainState as? MainState.Success)?.selectState != null) {
        TopAppBarDefaults.pinnedScrollBehavior()
    } else {
        TopAppBarDefaults.enterAlwaysScrollBehavior()
    }

    val gridState = rememberLazyStaggeredGridState()
//    TrackScrollJank(scrollableState = gridState, stateName = "main:grid_jank_tracker") // More specific jank tracker tag

    when (mainState) {
        is MainState.Loading -> {
            LoadingState(modifier = modifier.testTag("main:loading_state"))
        }

        is MainState.Success -> {
            val onNoteClick: (Long, Int, Int) -> Unit = { id, colorIndex, background ->
                if (mainState.selectState != null) {
                    onNoteSelected(id)
                } else {
                    navigateToNoteEditor(id, colorIndex, background)
                }
            }
            Scaffold(
                modifier = modifier
                    .fillMaxSize()
                    .testTag("main:scaffold_success") // Tag for the success state Scaffold
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    MainTopBar(
                        // Modifier for MainTopBar can be passed if needed,
                        // but test tags within MainTopBar are more granular
                        scrollBehavior = scrollBehavior,
                        noteDisplayCategory = mainState.noteDisplayCategory,
                        isGrid = mainState.isGrid,
                        selectState = mainState.selectState,
                        labelName = mainState.labelName,
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
                        onDeleteForever = onDeletedForever,
                        onRestore = onRestore,
                    )
                },
            ) { paddingValues ->
                LazyVerticalStaggeredGrid(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .testTag("main:notes_grid"), // Tag for the notes list/grid
                    state = gridState,
                    contentPadding = paddingValues,
                    columns = StaggeredGridCells.Fixed(if (mainState.isGrid) 2 else 1),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalItemSpacing = 8.dp,
                ) {
                    if (mainState.unPinNotePads.isEmpty() && mainState.pinNotePads.isEmpty()) {
                        item(span = StaggeredGridItemSpan.FullLine) {
                            EmptyState(
                                modifier = Modifier.testTag("main:empty_state_view"),
                                noteDisplayCategory = mainState.noteDisplayCategory,
                            )
                        }
                    }
                    if (mainState.pinNotePads.isNotEmpty()) {
                        item(span = StaggeredGridItemSpan.FullLine) {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .testTag("main:pinned_section_header"),
                                text = stringResource(Rd.string.modules_designsystem_pin),
                            )
                        }
                    }

                    items(items = mainState.pinNotePads, key = { "pinned_${it.note.id}" }) { notepad ->
                        NoteCard(
                            modifier = Modifier.testTag("main:note_card_pinned_${notepad.note.id}"),
                            notePad = notepad,
                            onCardClick = onNoteClick,
                            onLongClick = onNoteSelected,
                            isSelect = mainState.selectState?.setOfSelected?.contains(notepad.note.id) ?: false,
                        )
                    }

                    if (mainState.pinNotePads.isNotEmpty() && mainState.unPinNotePads.isNotEmpty()) {
                        item(span = StaggeredGridItemSpan.FullLine) {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .testTag("main:others_section_header"),
                                text = stringResource(Rd.string.modules_designsystem_other),
                            )
                        }
                    }
                    items(items = mainState.unPinNotePads, key = { "unpinned_${it.note.id}" }) { notepad ->
                        NoteCard(
                            modifier = Modifier.testTag("main:note_card_unpinned_${notepad.note.id}"),
                            notePad = notepad,
                            onCardClick = onNoteClick,
                            onLongClick = onNoteSelected,
                            isSelect = mainState.selectState?.setOfSelected?.contains(notepad.note.id) ?: false,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier // Test tag is applied from the caller
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        NoteLoadingWheel(
            modifier = Modifier.testTag("main:loading_wheel"),
            contentDesc = "Loading",
        )
    }
}

@SuppressLint("UnusedSharedTransitionModifierParameter")
@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Preview
@Composable
fun MainScreenPreview() {
    val pin = createFakeNotePads(1..2)
    val unPin = createFakeNotePads(3..6)

    PreviewContainer {
        MainScreen(
            mainState = MainState.Success(
                isGrid = true,
                labelName = "Label",
                pinNotePads = pin,
                unPinNotePads = unPin,
                noteDisplayCategory = NoteDisplayCategory(),
                selectState = null,
            ),
        )
    }
}

@Composable
private fun EmptyState(
    modifier: Modifier = Modifier, // Test tag applied from caller
    noteDisplayCategory: NoteDisplayCategory = NoteDisplayCategory(),
) {
    Column(
        modifier = modifier // Test tag applied from the caller
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(Rd.raw.modules_designsystem_note_taking))
        LottieAnimation(
            modifier = Modifier.testTag("main:empty_state_animation"),
            composition = composition,
            restartOnPlay = true,
            iterations = 200,
        )
        Text(
            text = stringResource(Rd.string.modules_designsystem_empty_notes),
            textAlign = TextAlign.Center,
            modifier = Modifier.testTag("main:empty_state_text"),
        )
    }
}
//
// @OptIn(ExperimentalSharedTransitionApi::class)
// fun LazyStaggeredGridScope.noteItems(
//    modifier: Modifier = Modifier,
//    sharedTransitionScope: SharedTransitionScope,
//    animatedContentScope: AnimatedVisibilityScope,
//    items: List<NotePad>,
//    setOfSelected: Set<Long>,
//    onNoteClick: (Long) -> Unit,
//    onSelectedCard: (Long) -> Unit,
//    sharedName: String = "note",
// ) = items(
//    items = items,
//    key = { it.id },
//    itemContent = { note ->
//
//        with(sharedTransitionScope) {
//            NoteCard(
//                modifier = modifier.sharedBounds(
//                    sharedContentState = rememberSharedContentState("${sharedName}_${note.id}"),
//                    animatedVisibilityScope = animatedContentScope,
//
//                ),
//                isSelect = setOfSelected.contains(note.id),
//                notePad = note,
//                onCardClick = onNoteClick,
//                onLongClick = onSelectedCard,
//            )
//        }
//    },
// )

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

@Composable
fun EmptyTrashDialog(
    modifier: Modifier = Modifier,
    show: Boolean = false,
    onDismissRequest: () -> Unit = {},
    onDelete: () -> Unit = {},

) {
    AnimatedVisibility(visible = show) {
        AlertDialog(
            modifier = modifier,
            onDismissRequest = onDismissRequest,
            title = { Text(text = stringResource(Rd.string.modules_designsystem_dialog_empty_trash)) },
            text = {
                Text(text = stringResource(Rd.string.modules_designsystem_dialog_empty_trash_content))
            },
            confirmButton = {
                NoteButton(
                    onClick = {
                        onDismissRequest()
                    },
                ) {
                    Text(text = stringResource(Rd.string.modules_designsystem_close))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    onDelete()
                    onDismissRequest()
                }) {
                    Text(text = stringResource(Rd.string.modules_designsystem_delete))
                }
            },
        )
    }
}

@Composable
fun DeleteForeverDialog(
    modifier: Modifier = Modifier,
    show: Boolean = false,
    onDismissRequest: () -> Unit = {},
    onDelete: () -> Unit = {},

) {
    AnimatedVisibility(visible = show) {
        AlertDialog(
            modifier = modifier,
            onDismissRequest = onDismissRequest,
            title = { Text(text = stringResource(Rd.string.modules_designsystem_dialog_delete_forever)) },
            text = {
                Text(text = stringResource(Rd.string.modules_designsystem_dialog_delete_forever_content))
            },
            confirmButton = {
                NoteButton(
                    onClick = {
                        onDismissRequest()
                    },
                ) {
                    Text(text = stringResource(Rd.string.modules_designsystem_close))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    onDelete()
                    onDismissRequest()
                }) {
                    Text(text = stringResource(Rd.string.modules_designsystem_delete))
                }
            },
        )
    }
}
