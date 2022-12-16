package com.mshdabiola.editscreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.mshdabiola.editscreen.state.NotePadUiState
import com.mshdabiola.editscreen.state.NoteUiState
import com.mshdabiola.editscreen.state.toNotePadUiState
import com.mshdabiola.model.NotePad


@Composable
fun EditScreen(
    editViewModel: EditViewModel = hiltViewModel()
) {

    LaunchedEffect(key1 = editViewModel.notePadUiState, block = {
        editViewModel.insertNote(editViewModel.notePadUiState.note)
    })
    EditScreen(
        notepad = editViewModel.notePadUiState,
        onTitleChange = editViewModel::onTitleChange,
        onSubjectChange = editViewModel::onDetailChange
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    notepad: NotePadUiState,
    onTitleChange: (String) -> Unit = {},
    onSubjectChange: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    onDeleteNote: () -> Unit = {},
    onSave: () -> Unit = {}
) {
    var expand by remember {
        mutableStateOf(false)
    }
    val subjectFocus = remember {
        FocusRequester()
    }



    Scaffold(topBar = {
        TopAppBar(
            title = { Text(text = "Edit Screen") },
            navigationIcon = {
                IconButton(onClick = { onBackClick() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "back button")
                }
            },
            actions = {
                IconButton(onClick = { onDeleteNote() }) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "delete note")
                }
                Box {
                    IconButton(onClick = { expand = true }) {

                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "")
                    }
                    DropdownMenu(expanded = expand, onDismissRequest = { expand = false }) {
                        DropdownMenuItem(
                            text = { Text(text = "Save to file") },
                            onClick = { onSave() },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "save"
                                )
                            }

                        )

                    }
                }
            }
        )
    }) {
        Column(
            modifier = Modifier

                .padding(it)

        ) {

            TextField(
                value = notepad.note.title,
                onValueChange = onTitleChange,
                placeholder = { Text(text = "Title") },
                textStyle = MaterialTheme.typography.titleMedium,
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    containerColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    autoCorrect = true,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .fillMaxWidth()

            )
            TextField(
                value = notepad.note.detail,
                onValueChange = onSubjectChange,
                textStyle = MaterialTheme.typography.bodySmall,
                placeholder = { Text(text = "Subject") },
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    containerColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    autoCorrect = true,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { subjectFocus.freeFocus() }),
                modifier = Modifier
                    .fillMaxSize()
                    .focusRequester(subjectFocus)


            )

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreenCheck(
    note: NoteUiState = NoteUiState(),
    onTitleChange: (String) -> Unit = {},
    onSubjectChange: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    onDeleteNote: () -> Unit = {},
    onSave: () -> Unit = {}
) {
    var expand by remember {
        mutableStateOf(false)
    }
    val subjectFocus = remember {
        FocusRequester()
    }



    Scaffold(topBar = {
        TopAppBar(
            title = { Text(text = "Edit Screen") },
            navigationIcon = {
                IconButton(onClick = { onBackClick() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "back button")
                }
            },
            actions = {
                IconButton(onClick = { onDeleteNote() }) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "delete note")
                }
                Box {
                    IconButton(onClick = { expand = true }) {

                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "")
                    }
                    DropdownMenu(expanded = expand, onDismissRequest = { expand = false }) {
                        DropdownMenuItem(
                            text = { Text(text = "Save to file") },
                            onClick = { onSave() },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "save"
                                )
                            }

                        )

                    }
                }
            }
        )
    }) {
        Column(
            modifier = Modifier

                .padding(it)

        ) {

            TextField(
                value = note.title,
                onValueChange = onTitleChange,
                placeholder = { Text(text = "Title") },
                textStyle = MaterialTheme.typography.titleMedium,
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    containerColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    autoCorrect = true,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .fillMaxWidth()

            )

            LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(10) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = false, onCheckedChange = {})
                        Text(modifier = Modifier.weight(1f), text = "Text")
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "")
                        }
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = note.title,
                    onValueChange = onTitleChange,
                    placeholder = { Text(text = "Title") },
                    textStyle = MaterialTheme.typography.titleMedium,
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        containerColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        autoCorrect = true,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier
                        .weight(1f)


                )
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Send")
                }
            }


        }
    }
}

@Preview
@Composable
fun EditScreenPreview() {
    EditScreen(notepad = NotePad().toNotePadUiState())
}

@Preview
@Composable
fun EditScreenCheckPreview() {
    EditScreenCheck(note = NoteUiState())
}

