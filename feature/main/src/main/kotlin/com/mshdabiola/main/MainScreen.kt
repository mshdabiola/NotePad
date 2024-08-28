/*
 *abiola 2022
 */

package com.mshdabiola.main

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.DraggableScrollbar
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.rememberDraggableScroller
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.scrollbarState
import com.mshdabiola.analytics.LocalAnalyticsHelper
import com.mshdabiola.common.result.Result
import com.mshdabiola.model.Note
import com.mshdabiola.ui.FirebaseScreenLog
import com.mshdabiola.ui.TrackScrollJank
import com.mshdabiola.ui.logNoteOpened

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
        modifier = modifier.fillMaxSize().testTag("main:loading"),
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
