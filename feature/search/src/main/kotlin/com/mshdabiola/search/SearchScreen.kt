/*
 *abiola 2022
 */

package com.mshdabiola.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mshdabiola.designsystem.icon.NoteIcon
import com.mshdabiola.ui.NoteCard
import com.mshdabiola.ui.TrackScrollJank
import com.mshdabiola.designsystem.R as Rd

// import org.koin.androidx.compose.koinViewModel

@OptIn(
    ExperimentalSharedTransitionApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
internal fun SharedTransitionScope.SearchScreen(
    modifier: Modifier = Modifier,
    animatedContentScope: AnimatedVisibilityScope,
    searchQuery: TextFieldState = rememberTextFieldState(),
    searchState: SearchState = SearchState.Select(),
    onBack: () -> Unit = {},
    onSetSearch: (SearchSort?) -> Unit = {},
    onNoteClick: (Long, Int, Int) -> Unit = { _, _, _ -> },
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val gridState = rememberLazyStaggeredGridState()
    TrackScrollJank(scrollableState = gridState, stateName = "main:grid:screen")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TextField(
                        state = searchQuery,
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                        ),
                        placeholder = { Text(stringResource(Rd.string.modules_designsystem_search_note)) },

                        trailingIcon = {
                            if (searchQuery.text.isNotBlank()) {
                                IconButton(
                                    onClick = {
                                        onSetSearch(null)

                                        searchQuery.clearText()
                                    },
                                ) {
                                    Icon(NoteIcon.Clear, contentDescription = "clear")
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Search,
                            showKeyboardOnFocus = true,
                        ),
                        modifier = Modifier.fillMaxWidth(),

                    )
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                ),
                subtitle = {},
                titleHorizontalAlignment = Alignment.CenterHorizontally,
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(NoteIcon.ArrowBack, contentDescription = "Back")
                    }
                },

            )
        },

    ) { paddingValues ->

        when (searchState) {
            is SearchState.Success -> {
                if (searchQuery.text.isNotBlank() && searchState.searches.isEmpty()) {
                    Column(
                        Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Icon(imageVector = NoteIcon.Search, contentDescription = "search")
                        Text(text = stringResource(Rd.string.modules_designsystem_no_result))
                    }
                } else {
                    LazyVerticalStaggeredGrid(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        state = gridState,
                        contentPadding = paddingValues,
                        columns = StaggeredGridCells.Fixed(if (searchState.isGrid) 2 else 1),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalItemSpacing = 8.dp,
                    ) {
                        items(items = searchState.searches, key = { it.note.id }) { notepad ->
                            NoteCard(
                                modifier = Modifier,
                                animatedVisibilityScope = animatedContentScope,
                                notePad = notepad,
                                onCardClick = onNoteClick,
                                onLongClick = {},
                                isSelect = false,
                            )
                        }
                    }
                }
            }

            is SearchState.Select -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    if (searchState.types.isNotEmpty()) {
                        LabelBox(
                            title = stringResource(Rd.string.modules_designsystem_types),
                            space = 32.dp,
                            numPerRow = 3,
                            searchState.types,
                            onItemClick = onSetSearch,
                        )
                    }

                    if (searchState.label.isNotEmpty()) {
                        LabelBox(
                            title = stringResource(Rd.string.modules_designsystem_labels),
                            space = 32.dp,
                            numPerRow = 3,
                            searchState.label,
                            onItemClick = onSetSearch,
                        )
                    }
                    if (searchState.color.isNotEmpty()) {
                        LabelBox(
                            title = stringResource(Rd.string.modules_designsystem_colors),
                            space = 8.dp,
                            numPerRow = 6,
                            searchState.color,
                            onItemClick = onSetSearch,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Preview
@Composable
internal fun NewSearchScreenPreview() {
    SharedTransitionLayout {
        AnimatedVisibility(visible = true) {
            SearchScreen(
                animatedContentScope = this,
                searchState = SearchState.Select(
                    types = listOf(
                        SearchSort.Type(0),
                        SearchSort.Type(1),
                        SearchSort.Type(2),
                        SearchSort.Type(3),
                        SearchSort.Type(4),
                        SearchSort.Type(5),
                    ),
                    label = listOf(
                        SearchSort.Label("Java", iconIndex = 6, 3),
                        SearchSort.Label("Python", iconIndex = 6, 3),
                        SearchSort.Label("C Sharp", iconIndex = 6, 3),
                        SearchSort.Label("JavaScript", iconIndex = 6, 3),
                        SearchSort.Label("Java", iconIndex = 6, 3),
                        SearchSort.Label("Java", iconIndex = 6, 3),

                    ),
                    color = listOf(
                        SearchSort.Color(-1),
                        SearchSort.Color(0),
                        SearchSort.Color(1),
                        SearchSort.Color(2),
                        SearchSort.Color(3),
                        SearchSort.Color(4),
                        SearchSort.Color(5),
                    ),
                ),
            )
        }
    }
}

@Composable
fun LabelBox(
    title: String = "Label",
    space: Dp = 16.dp,
    numPerRow: Int = 3,
    list: List<SearchSort> = emptyList(),
    onItemClick: (SearchSort?) -> Unit, // = {},
) {
    var showMore by remember { mutableStateOf(false) }
    FlowRow(
        Modifier.animateContentSize(),
        maxItemsInEachRow = numPerRow,
        maxLines = if (showMore) Int.MAX_VALUE else 2,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(space, Alignment.CenterHorizontally),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(modifier = Modifier.weight(1f), text = title)
            if (list.size > numPerRow) {
                TextButton(onClick = { showMore = !showMore }) {
                    Text(
                        text = if (!showMore) {
                            stringResource(id = Rd.string.modules_designsystem_more)
                        } else {
                            stringResource(
                                id = Rd.string.modules_designsystem_less,
                            )
                        },
                    )
                }
            }
        }
        list
            // .take()
            .forEach { searchSort ->
                when (searchSort) {
                    is SearchSort.Label -> {
                        SearchLabel(
                            modifier = Modifier.clickable { onItemClick(searchSort) },
                            iconId = NoteIcon.searchIcons[searchSort.iconIndex],
                            name = searchSort.name,
                        )
                    }

                    is SearchSort.Type -> {
                        SearchLabel(
                            modifier = Modifier.clickable { onItemClick(searchSort) },
                            iconId = NoteIcon.searchIcons[searchSort.index],
                            name = stringArrayResource(Rd.array.modules_designsystem_search_sort)[searchSort.index],
                        )
                    }

                    is SearchSort.Color -> {
                        Surface(
                            onClick = {
                                onItemClick(searchSort)
                            },
                            shape = CircleShape,
                            color = if (searchSort.colorIndex == -1) Color.White else NoteIcon.noteColors[searchSort.colorIndex],
                            modifier = Modifier
                                .width(40.dp)
                                .aspectRatio(1f),

                        ) {
                            if (searchSort.colorIndex == -1) {
                                Icon(
                                    imageVector = NoteIcon.FormatColorReset,
                                    contentDescription = "done",
                                    tint = Color.Gray,
                                    modifier = Modifier.padding(4.dp),
                                )
                            }
                        }
                    }
                }
            }
    }
}

@Composable
fun SearchLabel(
    modifier: Modifier = Modifier,
    iconId: ImageVector = NoteIcon.Label,
    name: String = "Label",
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier
                .width(72.dp)
                .aspectRatio(1f),
        ) {
            Icon(
                imageVector = iconId,
                contentDescription = "label icon",
                modifier = Modifier.padding(16.dp),
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = name)
    }
}
