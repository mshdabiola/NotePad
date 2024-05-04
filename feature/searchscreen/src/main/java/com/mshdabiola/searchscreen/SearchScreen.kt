package com.mshdabiola.searchscreen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardVoice
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.outlined.Brush
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.KeyboardVoice
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mshdabiola.designsystem.theme.SkTheme
import com.mshdabiola.ui.FirebaseScreenLog
import com.mshdabiola.ui.NoteCard
import com.mshdabiola.ui.NoteTextField
import com.mshdabiola.ui.state.NotePadUiState
import com.mshdabiola.ui.state.NoteUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
fun SearchScreen(
    onBack: () -> Unit = {},
    navigateToEdit: (Long, String, Long) -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    FirebaseScreenLog(screen = "search_screen")
    SearchScreen(
        onBack = onBack,
        navigateToEdit = navigateToEdit,
        searchUiState = viewModel.searchUiState,
        onSearchTextChange = viewModel::onSearchTextChange,
        onClearSearchText = viewModel::onClearSearchText,
        onItemLabelClick = viewModel::onItemLabelClick,
        onItemTypeClick = viewModel::onItemTypeClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SearchScreen(
    searchUiState: SearchUiState,
    onBack: () -> Unit,
    navigateToEdit: (Long, String, Long) -> Unit = { _, _, _ -> },
    onSearchTextChange: (String) -> Unit = {},
    onClearSearchText: () -> Unit = {},
    onItemLabelClick: (Int) -> Unit = {},
    onItemTypeClick: (Int) -> Unit = {},

    ) {
    val focusRequester = remember {
        FocusRequester()
    }
    LaunchedEffect(key1 = Unit) {
        focusRequester.requestFocus()
    }
    LaunchedEffect(key1 = searchUiState, block = {
    })
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "back")
                    }
                },
                title = {
                    NoteTextField(
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .fillMaxWidth(),
                        value = searchUiState.search,
                        placeholder = { Text(text = searchUiState.placeholder) },
                        onValueChange = onSearchTextChange,
                        textStyle = MaterialTheme.typography.bodyLarge,
                        trailingIcon = {
                            IconButton(onClick = { onClearSearchText() }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = stringResource(R.string.delete),
                                )
                            }
                        },
                    )
                },
            )
        },
    ) { paddingValues ->
        Column(
            Modifier
                .padding(paddingValues)
                .padding(8.dp),
        ) {
            if (searchUiState.notes.isNotEmpty()) {
                LazyVerticalStaggeredGrid(

                    columns = StaggeredGridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalItemSpacing = 8.dp,

                    ) {
                    items(searchUiState.notes) { notePadUiState ->
                        NoteCard(
                            notePad = notePadUiState,
                            onCardClick = { navigateToEdit(it, "", 0) },
                        )
                    }
                }
            } else {
                EmptySearchScreen(
                    labels = searchUiState.labels,
                    onItemLabelClick = onItemLabelClick,
                    onItemTypeClick = onItemTypeClick,
                )
            }
        }
    }
}

@Preview
@Composable
fun SearchScreenPreview() {
    SkTheme {
        SearchScreen(
            onBack = {},
            searchUiState = SearchUiState(
                search = "Search Text",
                notes = listOf(
                    NotePadUiState(
                        note = NoteUiState(
                            id = 1,
                            title = "Ashwin",
                            detail = "Kaya",
                            editDate = 6901L,
                            isCheck = false,
                            isPin = false,
                        ),
                    ),
                    NotePadUiState(
                        note = NoteUiState(
                            id = 2,
                            title = "Ashwin",
                            detail = "Kaya",
                            editDate = 6901L,
                            isCheck = false,
                            isPin = false,
                        ),
                    ),
                ).toImmutableList(),
            ),
        )
    }
}

@Composable
fun EmptySearchScreen(
    labels: ImmutableList<String> = emptyList<String>().toImmutableList(),
    onItemLabelClick: (Int) -> Unit = {},
    onItemTypeClick: (Int) -> Unit = {},
) {
    val labelPair = remember(labels) {
        labels.map { Pair(it, Icons.Outlined.Label) }
    }
    LabelBox(
        title = stringResource(R.string.types),
        labelIcon = listOf(
            Pair(stringResource(R.string.reminders), Icons.Outlined.Notifications),
            Pair(stringResource(R.string.lists), Icons.Outlined.CheckBox),
            Pair(stringResource(R.string.images), Icons.Outlined.Image),
            Pair(stringResource(R.string.voice), Icons.Outlined.KeyboardVoice),
            Pair(stringResource(R.string.drawings), Icons.Outlined.Brush),

            ),
        onItemClick = onItemTypeClick,
    )
    LabelBox(
        title = stringResource(R.string.labels),
        labelIcon = labelPair,
        onItemClick = onItemLabelClick,
    )
}

@Preview(device = "spec:parent=pixel_5,orientation=portrait")
@Composable
fun EmptyScreenPreview() {
    Column {
        EmptySearchScreen(
            labels = listOf("Voice", "Voice", "Voice", "Voice", "Voice").toImmutableList(),
        )
    }
}

@Composable
fun LabelBox(
    title: String = "Label",
    labelIcon: List<Pair<String, ImageVector>> = emptyList(),
    onItemClick: (Int) -> Unit = {},
) {
    val configuration = LocalConfiguration.current

    val number = remember {
        ((configuration.screenWidthDp.dp) / 72.dp).toInt() - 1
    }
    var h by remember {
        mutableStateOf(number)
    }

    val datas = remember(key1 = h, key2 = labelIcon) {
        labelIcon.take(h).chunked(number)
    }
    if (labelIcon.isNotEmpty()) {
        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth(),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(modifier = Modifier.weight(1f), text = title)
                if (labelIcon.size > number) {
                    TextButton(onClick = { h = if (h == number) labelIcon.size else number }) {
                        Text(
                            text = if (h == number) stringResource(id = R.string.more) else stringResource(
                                id = R.string.less
                            )
                        )
                    }
                }
            }
            Column(Modifier.animateContentSize()) {
                datas.forEachIndexed { index1, pairList ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        pairList.forEachIndexed { index, pair ->

                            SearchLabel(
                                modifier = Modifier.clickable { onItemClick(index + (index1 * number)) },
                                iconId = pair.second,
                                name = pair.first,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Preview(device = "spec:parent=pixel_5")
@Composable
fun LabelBoxPreview() {
    LabelBox(
        labelIcon = listOf(
            Pair("Voice", Icons.Default.KeyboardVoice),
            Pair("Voice", Icons.Default.KeyboardVoice),
            Pair("Voice", Icons.Default.KeyboardVoice),
            Pair("Voice", Icons.Default.KeyboardVoice),
            Pair("Voice", Icons.Default.KeyboardVoice),
            Pair("Voice", Icons.Default.KeyboardVoice),
            Pair("Voice", Icons.Default.KeyboardVoice),

            ),
    )
}

@Composable
fun SearchLabel(
    modifier: Modifier = Modifier,
    iconId: ImageVector = Icons.Default.Label,
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

@Preview
@Composable
fun SearchLabelPreview() {
    SearchLabel()
}
