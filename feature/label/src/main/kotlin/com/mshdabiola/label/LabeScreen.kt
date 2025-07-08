package com.mshdabiola.label

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.mshdabiola.designsystem.icon.NoteIcon
import com.mshdabiola.labelscreen.LabelState
import com.mshdabiola.labelscreen.LabelUiState
import com.mshdabiola.designsystem.R as Rd

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabelScreen(
    labelUiState: LabelUiState,
    onBack: () -> Unit = {},
    onDelete: (Long) -> Unit = {},
    onAdd: (Int) -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("label:back_button"),
                    ) {
                        Icon(imageVector = NoteIcon.ArrowBack, contentDescription = "back")
                    }
                },
                title = {
                    Text(
                        text = stringResource(Rd.string.modules_designsystem_edit_label),
                        modifier = Modifier.testTag("label:title"),
                    )
                },
            )
        },
    ) { paddingValues ->

        LazyColumn(
            Modifier
                .padding(paddingValues)
                .testTag("label:list"),
        ) {
            item {
                EditLabelTextField(
                    labelState = labelUiState.newLabel,
                    isEditMode = labelUiState.isEditMode,
                    onAdd = { onAdd(-1) },
                )
            }

            itemsIndexed(labelUiState.labels, key = { index, item -> item.id }) { index, item ->
                LabelTextField(
                    labelState = item,
                    onAdd = { onAdd(index) },
                    onDelete = { onDelete(item.id) },

                )
            }
        }
    }
}

@Preview
@Composable
fun LabelScreenPreview() {
    val labelUiState = LabelUiState(
        labels = listOf(
            LabelState(1, TextFieldState("Java")),
            LabelState(2, TextFieldState("Kotlin")),
            LabelState(3, TextFieldState("Python")),
            LabelState(4, TextFieldState("C sharper")),
            LabelState(5, TextFieldState("JavaScript")),

        ),
        newLabel = LabelState(-1, TextFieldState("new")),
        isEditMode = false,
    )
    LabelScreen(labelUiState = labelUiState, onBack = {}, onDelete = {}, onAdd = {})
}

@Composable
fun EditLabelTextField(
    labelState: LabelState,
    isEditMode: Boolean = false,
    onAdd: () -> Unit = { },
) {
    val focusRequester by remember {
        mutableStateOf(FocusRequester())
    }
    var isFocus by remember {
        mutableStateOf(false)
    }
    var isFirstTime by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(
        key1 = isEditMode,
        block = {
            if (isEditMode && !isFirstTime) {
                println("focus")
                focusRequester.requestFocus()
                isFirstTime = true
            }
        },
    )
    TextField(
        modifier =
        Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .onFocusChanged { isFocus = it.isFocused }
            .testTag("label:new_label_input"),
        state = labelState.label,
        placeholder = { Text(stringResource(Rd.string.modules_designsystem_create_new_label)) },
//        supportingText = if (errorOccur) stringResource(Rd.string.modules_designsystem_label_already_exists) else "",
//        isError = errorOccur,
        leadingIcon = {
            if (isFocus) {
                IconButton(
                    onClick = {
                        labelState.label.clearText()
                        focusRequester.freeFocus()
                    },
                    modifier = Modifier.testTag("label:new_label_clear_button"),
                ) {
                    Icon(imageVector = NoteIcon.Clear, contentDescription = "Clear")
                }
            } else {
                Icon(
                    imageVector = NoteIcon.Add,
                    contentDescription = "add",
                    modifier = Modifier.testTag("label:new_label_add_icon_indicator"),
                )
            }
        },
        trailingIcon = {
            if (labelState.label.text.isNotBlank()) {
                IconButton(
                    onClick = { onAdd() },
                    modifier = Modifier.testTag("label:new_label_done_button"),
                ) {
                    Icon(imageVector = NoteIcon.Done, contentDescription = "add")
                }
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        onKeyboardAction = {
            onAdd()
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
    )
}

@Composable
fun LabelTextField(
    labelState: LabelState,
    onAdd: () -> Unit = { },
    onDelete: (Long) -> Unit = {},
) {
    val focusRequester by remember {
        mutableStateOf(FocusRequester())
    }
    var isFocus by remember {
        mutableStateOf(false)
    }
    val focusManager = LocalFocusManager.current
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .onFocusChanged { focusState -> isFocus = focusState.isFocused }
            .testTag("label:item_label_input_${labelState.id}"),
        state = labelState.label,
        leadingIcon = {
            if (isFocus) {
                IconButton(
                    onClick = { onDelete(labelState.id) },
                    modifier = Modifier.testTag("label:item_delete_button_${labelState.id}"),
                ) {
                    Icon(imageVector = NoteIcon.Delete, contentDescription = "delete")
                }
            } else {
                Icon(
                    imageVector = NoteIcon.Label,
                    contentDescription = "label",
                    modifier = Modifier.testTag("label:item_label_icon_indicator_${labelState.id}"),
                )
            }
        },
        trailingIcon = {
            if (isFocus) {
                if (labelState.label.text.isNotBlank()) {
                    IconButton(
                        onClick = {
                            focusManager.clearFocus()
                            onAdd()
                        },
                        modifier = Modifier.testTag("label:item_done_button_${labelState.id}"),
                    ) {
                        Icon(imageVector = NoteIcon.Done, contentDescription = "add")
                    }
                }
            } else {
                IconButton(
                    onClick = { focusRequester.requestFocus() },
                    modifier = Modifier.testTag("label:item_edit_button_${labelState.id}"),
                ) {
                    Icon(imageVector = NoteIcon.Edit, contentDescription = "edit")
                }
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        onKeyboardAction = {
            focusManager.clearFocus()
            onAdd()
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
    )
}
