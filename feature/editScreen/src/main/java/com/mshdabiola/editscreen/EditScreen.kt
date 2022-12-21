package com.mshdabiola.editscreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import com.mshdabiola.editscreen.state.NoteCheckUiState
import com.mshdabiola.editscreen.state.NotePadUiState
import com.mshdabiola.editscreen.state.toNotePadUiState
import com.mshdabiola.model.Note
import com.mshdabiola.model.NoteCheck
import com.mshdabiola.model.NotePad


@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun EditScreen(
    editViewModel: EditViewModel = hiltViewModel(),
    onBack: () -> Unit
) {

    EditScreen(
        notepad = editViewModel.notePadUiState,
        onTitleChange = editViewModel::onTitleChange,
        onSubjectChange = editViewModel::onDetailChange,
        addItem = editViewModel::addCheck,
        onCheckChange = editViewModel::onCheckChange,
        onCheck = editViewModel::onCheck,
        onCheckDelete = editViewModel::onCheckDelete,
        onBackClick = onBack
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
    onSave: () -> Unit = {},
    onCheckChange: (String, Long) -> Unit = { _, _ -> },
    onCheckDelete: (Long) -> Unit = {},
    onCheck: (Boolean, Long) -> Unit = { _, _ -> },
    addItem: () -> Unit = {}
) {
    var expand by remember {
        mutableStateOf(false)
    }

    val subjectFocus = remember {
        FocusRequester()
    }

    val checkNote by remember(notepad.checks) {
        derivedStateOf { notepad.checks.filter { it.isCheck } }
    }
    val notCheckNote by remember(notepad.checks) {
        derivedStateOf { notepad.checks.filter { !it.isCheck } }
    }
    var showCheckNote by remember {
        mutableStateOf(true)
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
            modifier = Modifier.padding(it)

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
            if (notepad.note.isCheck) {

                Column(
                    Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    if (notCheckNote.isNotEmpty()) {

                        notCheckNote.forEach { noteCheckUiState ->
                            //  key(keys = arrayOf( noteCheckUiState.id)) {
                            CheckItem(
                                noteCheckUiState = noteCheckUiState,
                                onCheckChange,
                                onCheckDelete,
                                onCheck
                            )
                            //  }
                        }
                    }
                    Row(
                        modifier = Modifier.clickable { addItem() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "")

                        Text(text = "Add list item")
                    }

                    if (checkNote.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { showCheckNote = !showCheckNote }) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(id = if (showCheckNote) R.drawable.baseline_expand_more_24 else R.drawable.baseline_expand_less_24),
                                    contentDescription = ""
                                )
                            }
                            Text(
                                text = "${checkNote.size} Checked Items",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        if (showCheckNote) {
                            checkNote.forEach { noteCheckUiState ->
                                //  key(keys = arrayOf( noteCheckUiState.id)) {
                                CheckItem(
                                    noteCheckUiState = noteCheckUiState,
                                    onCheckChange,
                                    onCheckDelete,
                                    onCheck,
                                    strickText = true
                                )
                                //  }
                            }
                        }

                    }


                }
            } else {
                //Todo("fix text alignment in subject")
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
                        .imePadding()
                        .focusRequester(subjectFocus)


                )
            }
        }
    }
}

@Preview
@Composable
fun EditScreenPreview() {
    EditScreen(
        notepad = NotePad(
            note = Note(isCheck = true),
            checks = listOf(
                NoteCheck(1, 2, "Food", true),
                NoteCheck(1, 2, "Food", true),
                NoteCheck(1, 2, "Food", false),
                NoteCheck(1, 2, "Food", false),
                NoteCheck(1, 2, "Food", true),
                NoteCheck(1, 2, "Food", true),


                )
        ).toNotePadUiState()
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckItem(
    noteCheckUiState: NoteCheckUiState,
    onCheckChange: (String, Long) -> Unit = { _, _ -> },
    onCheckDelete: (Long) -> Unit = {},
    onCheck: (Boolean, Long) -> Unit = { _, _ -> },
    strickText: Boolean = false
) {

    val mutableInteractionSource = remember {
        MutableInteractionSource()
    }
    LaunchedEffect(key1 = Unit, block = {
        if (noteCheckUiState.id == 1L) {
            mutableInteractionSource.emit(FocusInteraction.Focus())
        }
    })
    val focused by mutableInteractionSource.collectIsFocusedAsState()
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = noteCheckUiState.isCheck,
            onCheckedChange = { onCheck(it, noteCheckUiState.id) })
        TextField(
            modifier = Modifier.weight(1f),
            value = noteCheckUiState.content,
            onValueChange = { onCheckChange(it, noteCheckUiState.id) },
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                containerColor = Color.Transparent
            ),
            textStyle = if (strickText) TextStyle.Default.copy(textDecoration = TextDecoration.LineThrough) else TextStyle.Default,
            interactionSource = mutableInteractionSource,
            trailingIcon = {
                if (focused) {
                    IconButton(onClick = {
                        onCheckDelete(noteCheckUiState.id)
                    }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "")
                    }
                }
            }

        )

    }
}




