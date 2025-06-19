/*
 *abiola 2022
 */

package com.mshdabiola.search

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mshdabiola.designsystem.component.NoteLoadingWheel
import com.mshdabiola.designsystem.icon.NoteIcon
import kotlinx.coroutines.launch
import com.mshdabiola.designsystem.R as Rd

// import org.koin.androidx.compose.koinViewModel

@OptIn(
    ExperimentalSharedTransitionApi::class,
    ExperimentalMaterial3Api::class,
)
@Composable
internal fun SearchScreen(
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedVisibilityScope,
    searchQuery: TextFieldState = rememberTextFieldState(),
    searchState: SearchState = SearchState.Loading,
    isGrid: Boolean = false,
    onBack: () -> Unit = {},
    onSetSearch: (SearchSort?) -> Unit = {},
) {
    val searchBarState = rememberSearchBarState(initialValue = SearchBarValue.Collapsed)
    val scope = rememberCoroutineScope()

    val inputField =
        @Composable {
            SearchBarDefaults.InputField(
                modifier = Modifier,
                searchBarState = searchBarState,
                textFieldState = searchQuery,
                onSearch = { scope.launch { searchBarState.animateToCollapsed() } },
                placeholder = { Text(stringResource(Rd.string.modules_designsystem_search_note)) },
                leadingIcon = {
                    IconButton(
                        onClick = onBack,
                    ) {
                        Icon(NoteIcon.ArrowBack, contentDescription = "Back")
                    }
                },
                trailingIcon = {
                    IconButton(
                        onClick = { searchQuery.clearText() },
                    ) {
                        Icon(NoteIcon.Clear, contentDescription = "clear")
                    }
                },
            )
        }
    ExpandedFullScreenSearchBar(
        state = searchBarState,
        inputField = inputField,
    ) {
        when (searchState) {
            is SearchState.Loading -> {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    NoteLoadingWheel("")
                }
            }

            is SearchState.Success -> {
                LazyVerticalStaggeredGrid(
                    modifier = Modifier
                        .padding(16.dp),
                    columns = StaggeredGridCells.Fixed(if (isGrid) 2 else 1),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalItemSpacing = 8.dp,

                ) {
                    if (searchQuery.text.isNotBlank() && searchState.searches.isEmpty()) {
                        item(span = StaggeredGridItemSpan.FullLine) {
                            Column(
                                Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                            ) {
                                Icon(imageVector = NoteIcon.Search, contentDescription = "search")
                                Text(text = stringResource(Rd.string.modules_designsystem_no_result))
                            }
                        }
                    }
                    if (searchState.searches.isEmpty() && searchQuery.text.isBlank()) {
                        if (searchState.types.isNotEmpty()) {
                            item(span = StaggeredGridItemSpan.FullLine) {
                                LabelBox(
                                    title = stringResource(Rd.string.modules_designsystem_types),
                                    searchState.types,
                                    onItemClick = onSetSearch,
                                )
                            }
                        }

                        if (searchState.label.isNotEmpty()) {
                            item(span = StaggeredGridItemSpan.FullLine) {
                                LabelBox(
                                    title = stringResource(Rd.string.modules_designsystem_labels),
                                    searchState.label,
                                    onItemClick = onSetSearch,
                                )
                            }
                        }
                        if (searchState.color.isNotEmpty()) {
                            item(span = StaggeredGridItemSpan.FullLine) {
                                Text(text = stringResource(Rd.string.modules_designsystem_colors))
                            }

                            item(span = StaggeredGridItemSpan.FullLine) {
                                FlowRow(
                                    verticalArrangement = Arrangement.spacedBy(4.dp),

                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    searchState.color.forEach { color ->
                                        Surface(
                                            onClick = {
                                                onSetSearch(color)
                                            },
                                            shape = CircleShape,
                                            color = if (color.colorIndex == -1) Color.White else NoteIcon.noteColors[color.colorIndex],
                                            modifier = Modifier
                                                .width(40.dp)
                                                .aspectRatio(1f),

                                        ) {
                                            if (color.colorIndex == -1) {
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

//                    noteItems(
//                        modifier = Modifier,
//                        sharedTransitionScope = sharedTransitionScope,
//                        animatedContentScope = animatedContentScope,
//                        items = searchState.searches,
//                        onNoteClick = onNoteClick,
//                        onSelectedCard = {},
//                        setOfSelected = emptySet(),
//                        sharedName = "search",
//                    )
                }
            }
        }
    }
}

@Composable
fun LabelBox(
    title: String = "Label",
    list: List<SearchSort> = emptyList(),
    onItemClick: (SearchSort?) -> Unit, // = {},
) {
    var showMore by remember { mutableStateOf(false) }
    FlowRow(
        Modifier.animateContentSize(),
        maxItemsInEachRow = 3,
        maxLines = if (showMore) Int.MAX_VALUE else 2,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(32.dp, Alignment.Start),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(modifier = Modifier.weight(1f), text = title)
            if (list.size > 3) {
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
                val item = when (searchSort) {
                    is SearchSort.Label -> Pair(
                        searchSort.name,
                        NoteIcon.searchIcons[searchSort.iconIndex],

                    )

                    is SearchSort.Type -> Pair(
                        stringArrayResource(Rd.array.modules_designsystem_search_sort)[searchSort.index],
                        NoteIcon.searchIcons[searchSort.index],
                    )

                    is SearchSort.Color -> Pair(
                        "",
                        NoteIcon.searchIcons[0],
                    )
                }
                SearchLabel(
                    modifier = Modifier.clickable { onItemClick(searchSort) },
                    iconId = item.second,
                    name = item.first,
                )
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
