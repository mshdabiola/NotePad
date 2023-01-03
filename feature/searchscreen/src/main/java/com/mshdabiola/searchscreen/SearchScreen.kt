package com.mshdabiola.searchscreen


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mshdabiola.designsystem.component.NoteCard
import com.mshdabiola.designsystem.component.NoteTextField
import com.mshdabiola.designsystem.component.state.NotePadUiState
import com.mshdabiola.designsystem.component.state.NoteUiState
import com.mshdabiola.designsystem.theme.NotePadAppTheme
import kotlinx.collections.immutable.toImmutableList


@Composable
fun SearchScreen(
    onBack: () -> Unit = {},
    navigateToEdit: (Long, String, Long) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    SearchScreen(
        onBack = onBack,
        navigateToEdit = navigateToEdit,
        searchUiState = viewModel.searchUiState,
        onSearchTextChange = viewModel::onSearchTextChange
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SearchScreen(
    searchUiState: SearchUiState,
    onBack: () -> Unit,
    navigateToEdit: (Long, String, Long) -> Unit = { _, _, _ -> },
    onSearchTextChange: (String) -> Unit = {}

) {
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
                        modifier = Modifier.fillMaxWidth(),
                        value = searchUiState.search,
                        placeholder = { Text(text = "Search") },
                        onValueChange = onSearchTextChange,
                        textStyle = MaterialTheme.typography.bodyLarge
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            Modifier
                .padding(paddingValues)
                .padding(8.dp)
        ) {
            LazyVerticalStaggeredGrid(

                columns = StaggeredGridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)

            ) {

                items(searchUiState.notes) { notePadUiState ->
                    NoteCard(
                        notePad = notePadUiState,
                        onCardClick = { navigateToEdit(it, "", 0) })
                }

            }
        }
    }
}

@Preview
@Composable
fun SearchScreenPreview() {
    NotePadAppTheme {
        SearchScreen(
            onBack = {},
            searchUiState = SearchUiState(
                search = "Search Text",
                notes = listOf(
                    NotePadUiState(
                        note = NoteUiState(
                            id = null,
                            title = "Ashwin",
                            detail = "Kaya",
                            editDate = 6901L,
                            isCheck = false,
                            isPin = false,
                        )
                    ),
                    NotePadUiState(
                        note = NoteUiState(
                            id = null,
                            title = "Ashwin",
                            detail = "Kaya",
                            editDate = 6901L,
                            isCheck = false,
                            isPin = false,
                        )
                    )
                ).toImmutableList()
            )
        )
    }


}