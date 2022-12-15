package com.mshdabiola.mainscreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mshdabiola.mainscreen.component.NoteCard
import com.mshdabiola.mainscreen.state.NoteUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun MainScreen(mainViewModel: MainViewModel = hiltViewModel()) {

    val mainState = mainViewModel.mainState.collectAsStateWithLifecycle()
    MainScreen(listOfNote = mainState.value.listOfNote)

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MainScreen(listOfNote: ImmutableList<NoteUiState>) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    ModalNavigationDrawer(
        drawerContent = { },
        drawerState = drawerState,
        gesturesEnabled = true
    ) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    title = { Text(text = "Note Pad") },
                    navigationIcon = {
                        Icon(imageVector = Icons.Default.Menu, contentDescription = "menu")
                    },
                    actions = {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "")
                    },
                    scrollBehavior = scrollBehavior
                )
            },
            bottomBar = {
                BottomAppBar(
                    actions = {
                        Spacer(modifier = Modifier.width(16.dp))
                        Icon(
                            imageVector = ImageVector
                                .vectorResource(id = R.drawable.outline_check_box_24),
                            contentDescription = "note check"
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Icon(
                            imageVector = ImageVector
                                .vectorResource(id = R.drawable.baseline_brush_24),
                            contentDescription = "note check"
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Icon(
                            imageVector = ImageVector
                                .vectorResource(id = R.drawable.outline_keyboard_voice_24),
                            contentDescription = "note check"
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Icon(
                            imageVector = ImageVector
                                .vectorResource(id = R.drawable.outline_image_24),
                            contentDescription = "note check"
                        )
                    },
                    floatingActionButton = {
                        FloatingActionButton(onClick = { /*TODO*/ }) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "add note")
                        }
                    }
                )
            }
        ) {
            LazyVerticalStaggeredGrid(
                modifier = Modifier
                    .padding(it)
                    .padding(8.dp),
                columns = StaggeredGridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)

            ) {
                items(listOfNote) {
                    NoteCard(noteUiState = it)
                }

            }
        }
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    MainScreen(
        listOfNote =
        listOf(
            NoteUiState(),
            NoteUiState(
                detail = "akdlskdfjsl" +
                        "aslsfsfjslfjskfdjkdfkjdjkfdk" +
                        "dkdfdkdjf" +
                        "sfksjfdj"
            ),
            NoteUiState(),
            NoteUiState(),
            NoteUiState(),
            NoteUiState(),
            NoteUiState(),

            )
            .toImmutableList()
    )
}