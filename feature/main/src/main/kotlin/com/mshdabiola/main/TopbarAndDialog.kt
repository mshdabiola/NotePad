
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarScrollBehavior
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopSearchBar
import androidx.compose.material3.rememberSearchBarState
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.mshdabiola.designsystem.component.NoteLoadingWheel
import com.mshdabiola.designsystem.component.NoteTextField
import com.mshdabiola.designsystem.icon.NoteIcon
import com.mshdabiola.main.LabelBox
import com.mshdabiola.main.SearchSort
import com.mshdabiola.main.SearchState
import com.mshdabiola.main.noteItems
import com.mshdabiola.model.NoteCheck
import com.mshdabiola.model.NotePad
import com.mshdabiola.model.NoteVoice
import com.mshdabiola.ui.FlowLayout2
import com.mshdabiola.ui.LabelCard
import com.mshdabiola.ui.ReminderCard
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import com.mshdabiola.designsystem.R as Rd

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopAppBar(
    isGrid: Boolean = false,
    navigateToSearch: () -> Unit = {},
    onNavigate: () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
    onToggleGrid: () -> Unit = {},
) {
    TopAppBar(
        modifier = Modifier,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { navigateToSearch() }
                    .fillMaxWidth()
                    .padding(4.dp)
                    .padding(end = 16.dp)
                    .clip(
                        RoundedCornerShape(
                            topEnd = 50f,
                            topStart = 50f,
                            bottomEnd = 50f,
                            bottomStart = 50f,
                        ),
                    )
                    .background(MaterialTheme.colorScheme.secondaryContainer),
            ) {
                IconButton(onClick = onNavigate) {
                    Icon(
                        imageVector = NoteIcon.Menu,
                        contentDescription = "menu",
                    )
                }
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(Rd.string.modules_designsystem_search_note),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                )
                IconButton(onClick = { onToggleGrid() }) {
                    if (!isGrid) {
                        Icon(imageVector = NoteIcon.GridView, contentDescription = "grid")
                    } else {
                        Icon(imageVector = NoteIcon.ViewAgenda, contentDescription = "column")
                    }
                }
            }
        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent,
        ),
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun NewTopMainAppBar(
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedVisibilityScope,
    searchQuery: TextFieldState = rememberTextFieldState(),
    searchState: SearchState = SearchState.Loading,
    isGrid: Boolean = false,
    scrollBehavior: SearchBarScrollBehavior? = null,
    onNavigateIcon: () -> Unit = {},
    onToggleGrid: () -> Unit = {},
    onSetSearch: (SearchSort?) -> Unit = {},
    onNoteClick: (Long) -> Unit = {},
    onExpandSearch: (Boolean) -> Unit = {},

) {
    val searchBarState = rememberSearchBarState(initialValue = SearchBarValue.Collapsed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = searchBarState.currentValue) {
        onExpandSearch(searchBarState.currentValue == SearchBarValue.Expanded)
    }

    val inputField =
        @Composable {
            SearchBarDefaults.InputField(
                modifier = Modifier,
                searchBarState = searchBarState,
                textFieldState = searchQuery,
                onSearch = { scope.launch { searchBarState.animateToCollapsed() } },
                placeholder = { Text(stringResource(Rd.string.modules_designsystem_search_note)) },
                leadingIcon = {
                    if (searchBarState.currentValue == SearchBarValue.Expanded) {
                        IconButton(
                            onClick = { scope.launch { searchBarState.animateToCollapsed() } },
                        ) {
                            Icon(NoteIcon.ArrowBack, contentDescription = "Back")
                        }
                    } else {
                        IconButton(
                            onClick = onNavigateIcon,
                        ) {
                            Icon(NoteIcon.Menu, contentDescription = "menu")
                        }
                    }
                },
                trailingIcon = {
                    if (searchBarState.currentValue == SearchBarValue.Expanded) {
                        IconButton(
                            onClick = { searchQuery.clearText() },
                        ) {
                            Icon(NoteIcon.Clear, contentDescription = "clear")
                        }
                    } else {
                        IconButton(onClick = { onToggleGrid() }) {
                            if (!isGrid) {
                                Icon(imageVector = NoteIcon.GridView, contentDescription = "grid")
                            } else {
                                Icon(
                                    imageVector = NoteIcon.ViewAgenda,
                                    contentDescription = "column",
                                )
                            }
                        }
                    }
                },
            )
        }

    TopSearchBar(
        modifier = modifier,
        state = searchBarState,
        inputField = inputField,
        scrollBehavior = scrollBehavior,
    )
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
                                    searchState.color.forEach {
                                        Surface(
                                            onClick = {
                                                onSetSearch(it)
                                            },
                                            shape = CircleShape,
                                            color = if (it.colorIndex == -1) Color.White else NoteIcon.noteColors[it.colorIndex],
                                            modifier = Modifier
                                                .width(40.dp)
                                                .aspectRatio(1f),

                                        ) {
                                            if (it.colorIndex == -1) {
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

                    noteItems(
                        modifier = Modifier,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedContentScope = animatedContentScope,
                        items = searchState.searches,
                        onNoteClick = onNoteClick,
                        onSelectedCard = {},
                        setOfSelected = emptySet(),
                    )
                }
            }
        }

//        SearchResults(
//            onResultClick = {
//                result - & gt;
//                textFieldState.setTextAndPlaceCursorAtEnd(result)
//                scope.launch { searchBarState.animateToCollapsed() }
//            }
//        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Preview
@Composable
fun NewTopMainAppBarPreview() {
    SharedTransitionLayout {
        AnimatedVisibility(true) {
            NewTopMainAppBar(
                modifier = Modifier,
                sharedTransitionScope = this@SharedTransitionLayout,
                animatedContentScope = this,

            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
    modifier: Modifier = Modifier,
    state: TextFieldState = rememberTextFieldState(),
    toggleSearch: () -> Unit = {},
) {
    val focusRequester = remember {
        FocusRequester()
    }
    LaunchedEffect(key1 = Unit) {
        focusRequester.requestFocus()
    }

    TopAppBar(
        modifier = modifier,
        navigationIcon = {
            IconButton(
                onClick = {
                    state.clearText()
                    toggleSearch()
                },
            ) {
                Icon(imageVector = NoteIcon.ArrowBack, contentDescription = "back")
            }
        },
        title = {
            NoteTextField(
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .fillMaxWidth(),
                state = state,
                placeholder = "Search",
                textStyle = MaterialTheme.typography.bodyLarge,
                trailingIcon = {
                    if (state.text.isNotBlank()) {
                        IconButton(onClick = { state.clearText() }) {
                            Icon(
                                imageVector = NoteIcon.Clear,
                                contentDescription = stringResource(Rd.string.modules_designsystem_delete),
                            )
                        }
                    }
                },
            )
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectTopBar(
    selectNumber: Int = 0,
    isAllPin: Boolean = false,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
    onClear: () -> Unit = {},
    onPin: () -> Unit = {},
    onNoti: () -> Unit = {},
    onColor: () -> Unit = {},
    onLabel: () -> Unit = {},
    onArchive: () -> Unit = {},
    onDelete: () -> Unit = {},
    onSend: () -> Unit = {},
    onCopy: () -> Unit = {},

) {
    var showDropDown by remember {
        mutableStateOf(false)
    }
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onClear) {
                Icon(imageVector = NoteIcon.Clear, contentDescription = "clear note")
            }
        },
        title = {
            Text(text = "$selectNumber")
        },
        actions = {
            IconButton(
                modifier = Modifier.testTag("main:pin"),
                onClick = onPin,
            ) {
                Icon(
                    imageVector = if (isAllPin) NoteIcon.PushPin else NoteIcon.PushPinD, // painterResource(id = if (isAllPin) NoteIcon.Pin else NoteIcon.PinFill),
                    contentDescription = "pin note",
                )
            }
            IconButton(
                modifier = Modifier.testTag("main:notification"),
                onClick = onNoti,
            ) {
                Icon(
                    imageVector = NoteIcon.Notification,
                    contentDescription = "notification",
                )
            }
            IconButton(
                modifier = Modifier.testTag("main:color"),
                onClick = onColor,
            ) {
                Icon(
                    imageVector = NoteIcon.ColorLens,
                    contentDescription = "color",
                )
            }
            IconButton(
                modifier = Modifier.testTag("main:label"),
                onClick = onLabel,
            ) {
                Icon(imageVector = NoteIcon.Label, contentDescription = "Label")
            }
            Box {
                IconButton(
                    modifier = Modifier.testTag("main:more"),
                    onClick = { showDropDown = true },
                ) {
                    Icon(NoteIcon.MoreVert, contentDescription = "more")
                }
                DropdownMenu(expanded = showDropDown, onDismissRequest = { showDropDown = false }) {
                    DropdownMenuItem(
                        text = { Text(text = stringResource(Rd.string.modules_designsystem_archive)) },
                        onClick = {
                            showDropDown = false
                            onArchive()
                        },
                    )
                    DropdownMenuItem(
                        text = { Text(text = stringResource(Rd.string.modules_designsystem_delete)) },
                        onClick = {
                            showDropDown = false
                            onDelete()
                        },
                    )
                    if (selectNumber == 1) {
                        DropdownMenuItem(
                            text = { Text(text = stringResource(Rd.string.modules_designsystem_make_a_copy)) },
                            onClick = {
                                showDropDown = false
                                onCopy()
                            },
                        )
                        DropdownMenuItem(
                            text = { Text(text = stringResource(Rd.string.modules_designsystem_send)) },
                            onClick = {
                                showDropDown = false
                                onSend()
                            },
                        )
                    }
                }
            }
        },
        scrollBehavior = scrollBehavior,

    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun SelectTopAppBarPreview() {
    SelectTopBar()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabelTopAppBar(
    label: String = "label",
    onNavigate: () -> Unit = {},
    onSearch: () -> Unit = {},
    onRenameLabel: () -> Unit = {},
    onDeleteLabel: () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
) {
    var showDropDown by remember {
        mutableStateOf(false)
    }

    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onNavigate) {
                Icon(imageVector = NoteIcon.Menu, contentDescription = "menu")
            }
        },
        title = { Text(text = label) },
        actions = {
            IconButton(onClick = onSearch) {
                Icon(
                    imageVector = NoteIcon.Search,
                    contentDescription = "search",
                )
            }

            Box {
                IconButton(onClick = { showDropDown = true }) {
                    Icon(NoteIcon.MoreVert, contentDescription = "more")
                }
                DropdownMenu(expanded = showDropDown, onDismissRequest = { showDropDown = false }) {
                    DropdownMenuItem(
                        text = { Text(text = stringResource(Rd.string.modules_designsystem_rename_label)) },
                        onClick = {
                            showDropDown = false
                            onRenameLabel()
                        },
                    )
                    DropdownMenuItem(
                        text = { Text(text = stringResource(Rd.string.modules_designsystem_delete_label)) },
                        onClick = {
                            showDropDown = false
                            onDeleteLabel()
                        },
                    )
                }
            }
        },
        scrollBehavior = scrollBehavior,

    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun LabelTopAppBarPreview() {
    LabelTopAppBar()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchiveTopAppBar(
    name: String = "Archive",
    onNavigate: () -> Unit = {},
    onSearch: () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
) {
//    var showDropDown by remember {
//        mutableStateOf(false)
//    }

    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onNavigate) {
                Icon(imageVector = NoteIcon.Menu, contentDescription = "menu")
            }
        },
        title = { Text(text = name) },
        actions = {
            IconButton(onClick = onSearch) {
                Icon(
                    imageVector = NoteIcon.Search,
                    contentDescription = "search",
                )
            }
        },
        scrollBehavior = scrollBehavior,

    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun ArchiveTopAppBarPreview() {
    ArchiveTopAppBar()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrashTopAppBar(
    onNavigate: () -> Unit = {},
    onEmptyTrash: () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
) {
    var showDropDown by remember {
        mutableStateOf(false)
    }

    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onNavigate) {
                Icon(imageVector = NoteIcon.Menu, contentDescription = "menu")
            }
        },
        title = { Text(text = stringResource(Rd.string.modules_designsystem_trash)) },
        actions = {
            Box {
                IconButton(onClick = { showDropDown = true }) {
                    Icon(NoteIcon.MoreVert, contentDescription = "more")
                }
                DropdownMenu(expanded = showDropDown, onDismissRequest = { showDropDown = false }) {
                    DropdownMenuItem(
                        text = { Text(text = stringResource(Rd.string.modules_designsystem_empty_trash)) },
                        onClick = {
                            showDropDown = false
                            onEmptyTrash()
                        },
                    )
                }
            }
        },
        scrollBehavior = scrollBehavior,

    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun TrashTopAppBarPreview() {
    TrashTopAppBar()
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun MainTopAppBarPreview() {
    MainTopAppBar()
}

@OptIn(ExperimentalMaterial3Api::class)
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
            title = { Text(text = stringResource(Rd.string.modules_designsystem_rename_label)) },
            text = {
                Text(text = stringResource(Rd.string.modules_designsystem_rename_details))
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

@Preview
@Composable
fun DeleteLabelPreview() {
    DeleteLabelAlertDialog(show = true)
}

@Composable
fun NoteBottomBar(modifier: Modifier = Modifier) {
    BottomAppBar(
        actions = {
            IconButton(
                modifier = Modifier.testTag("main:check"),
                onClick = { },
            ) {
                Icon(
                    imageVector = NoteIcon.CheckBox,
                    contentDescription = "add note check",
                )
            }

            IconButton(
                modifier = Modifier.testTag("main:draw"),
                onClick = {
                },
            ) {
                Icon(
                    imageVector = NoteIcon.Brush,
                    contentDescription = "add note drawing",
                )
            }

            IconButton(
                modifier = Modifier.testTag("main:voice"),
                onClick = {
                },
            ) {
                Icon(
                    imageVector = NoteIcon.KeyboardVoice,
                    contentDescription = "add note voice",
                )
            }

            IconButton(
                modifier = Modifier.testTag("main:image"),
                onClick = {
                },
            ) {
                Icon(
                    imageVector = NoteIcon.Image,
                    contentDescription = "add note image",
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.testTag("main:float"),
                onClick = { },
                containerColor = MaterialTheme.colorScheme.primary,
                elevation = FloatingActionButtonDefaults.elevation(),
            ) {
                Icon(imageVector = NoteIcon.Add, contentDescription = "add note")
            }
        },
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteCard(
    modifier: Modifier = Modifier,
    notePad: NotePad,
    isSelect: Boolean = false,
    onCardClick: (Long) -> Unit = {},
    onLongClick: (Long) -> Unit = {},
) {
    val unCheckNote by remember(notePad.checks) {
        derivedStateOf { notePad.checks.filter { !it.isCheck } }
    }
    val numberOfChecked by remember(key1 = notePad.checks) {
        derivedStateOf { notePad.checks.count { it.isCheck } }
    }
    val haveVoice by remember(notePad.voices) {
        derivedStateOf { notePad.voices.isNotEmpty() }
    }
    val bg = if (notePad.background != -1) {
        Color.Transparent
    } else {
        if (notePad.color != -1) {
            NoteIcon.noteColors[notePad.color]
        } else {
            MaterialTheme.colorScheme.background
        }
    }

    val sColor = if (notePad.background != -1) {
        NoteIcon.background[notePad.background].fgColor
    } else {
        MaterialTheme.colorScheme.secondaryContainer
    }

    var size by remember {
        mutableStateOf(IntSize.Zero)
    }
    val images = remember(notePad.images) {
        notePad.images.reversed().chunked(3)
    }

    val de = LocalDensity.current

    OutlinedCard(
        modifier = modifier.combinedClickable(
            onClick = { notePad.id.let { onCardClick(it) } },
            onLongClick = { notePad.id.let { onLongClick(it) } },
        ),
        border = if (isSelect) {
            BorderStroke(3.dp, Color.Blue)
        } else {
            BorderStroke(
                1.dp,
                sColor,
            )
        },
        colors = CardDefaults.outlinedCardColors(containerColor = bg),
    ) {
        Box {
            if (notePad.background != -1) {
                Image(
                    painter = painterResource(id = NoteIcon.background[notePad.background].bg),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(
                        with(de) { size.width.toDp() },
                        with(de) { size.height.toDp() },
                    ),
                )
            }

            Column(
                Modifier
                    .onSizeChanged {
                        size = it
                    },
            ) {
                if (notePad.images.isNotEmpty()) {
                    images.forEach { imageList ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                        ) {
                            imageList.forEach {
                                AsyncImage(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(100.dp),
                                    model = it.path,
                                    contentDescription = "",
                                    contentScale = ContentScale.Crop,
                                )
                            }
                        }
                    }
                }

                if (!notePad.isImageOnly()) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                    ) {
                        Text(
                            text = notePad.title.ifEmpty { notePad.detail },
                            style = if (notePad.title.isNotEmpty()) {
                                MaterialTheme.typography.titleMedium
                            } else {
                                MaterialTheme.typography.bodyMedium
                            },
                            maxLines = 10,
                        )
                        if (!notePad.isCheck) {
                            if (notePad.title.isNotEmpty()) {
                                if (notePad.detail.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = notePad.detail,
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 10,

                                    )
                                }
                            }
                        } else {
                            unCheckNote.take(10).forEach {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        modifier = Modifier.size(16.dp),
                                        imageVector = NoteIcon.CheckBoxOutlineBlank,
                                        contentDescription = "",
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        it.content,
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 1,
                                    )
                                }
                            }
                            if (unCheckNote.size > 10) {
                                Text(text = "....")
                            }
                            if (numberOfChecked > 0) {
                                Text(
                                    text = stringResource(
                                        Rd.string.modules_designsystem_checked_items_value,
                                        numberOfChecked,
                                    ),
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        FlowLayout2(
                            verticalSpacing = 4.dp,
                        ) {
                            if (haveVoice) {
                                Icon(
                                    imageVector = NoteIcon.PlayCircle,
                                    contentDescription = "play",
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            if (notePad.reminder > 0) {
                                ReminderCard(
                                    date = notePad.reminderString,
                                    interval = notePad.interval,
                                    color = sColor,
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            notePad.labels.forEach {
                                LabelCard(name = it.label, color = sColor)
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NoteCardPreview() {
    NoteCard(
        notePad = NotePad(
            id = 1,
            title = "Mandy abiola",
            detail = "Lamia moshood",
            editDate = 314L,
            isCheck = true,
            reminder = Clock.System.now().toEpochMilliseconds(),
            color = 2,
            isPin = false,
            background = 3,
//            selected = true,

//            labels = listOf(
//                "ade",
//                "food",
//                "abiola",
//                "kdlskdflsjfslf",
//                "klslssljsl",
//                "alskfk",
//            ).toImmutableList(),
            checks = listOf(
                NoteCheck(
                    id = 2418L,
                    noteId = 6429L,
                    content = "Maegan",
                    isCheck = false,
                    focus = false,
                ),
                NoteCheck(
                    id = 2418L,
                    noteId = 6429L,
                    content = "Book",
                    isCheck = false,
                    focus = false,
                ),
            ).toImmutableList(),
            voices = listOf(
                NoteVoice(
                    id = 500L,
                    noteId = 8001L,
                    voiceName = "Danniel",
                    length = 1940L,
                    currentProgress = 179,
                    isPlaying = false,

                ),
            ).toImmutableList(),
        ),
    )
}
