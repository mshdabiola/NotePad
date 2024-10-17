package com.mshdabiola.labelscreen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.mshdabiola.designsystem.icon.NoteIcon
import com.mshdabiola.ui.FirebaseScreenLog
import com.mshdabiola.ui.NoteTextField
import kotlinx.collections.immutable.toImmutableList
import com.mshdabiola.designsystem.R as Rd

@Composable
fun LabelScreen(onBack: () -> Unit, labelViewModel: LabelViewModel = hiltViewModel()) {
    FirebaseScreenLog(screen = "label_screen")
    LabelScreen(
        labelScreenUiState = labelViewModel.labelScreenUiState,
        onBack = onBack,
        onLabelChange = labelViewModel::onLabelChange,
        onDelete = labelViewModel::onDelete,
        onAddLabelChange = labelViewModel::onAddLabelChange,
        onAddLabelDone = labelViewModel::onAddLabelDone,
        onAddDelete = labelViewModel::onAddDeleteValue,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabelScreen(
    labelScreenUiState: LabelScreenUiState,
    onBack: () -> Unit = {},
    onLabelChange: (String, Long) -> Unit = { _, _ -> },
    onDelete: (Long) -> Unit = {},
    onAddLabelChange: (String) -> Unit = {},
    onAddLabelDone: () -> Unit = {},
    onAddDelete: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = NoteIcon.ArrowBack, contentDescription = "back")
                    }
                },
                title = {
                    Text(text = stringResource(Rd.string.modules_designsystem_edit_label))
                },
            )
        },
    ) { paddingValues ->

        LazyColumn(Modifier.padding(paddingValues)) {
            item {
                EditLabelTextField(
                    value = labelScreenUiState.editText,
                    isEditMode = labelScreenUiState.isEditMode,
                    errorOccur = labelScreenUiState.errorOccur,
                    onValueChange = onAddLabelChange,
                    onAddLabelDone = onAddLabelDone,
                    onAddDelete = onAddDelete,
                )
            }

            items(labelScreenUiState.labels, key = { it.id }) {
                LabelTextField(it, onLabelChange, onDelete)
            }
        }
    }
}

@Composable
fun EditLabelTextField(
    value: String,
    isEditMode: Boolean,
    errorOccur: Boolean,
    onValueChange: (String) -> Unit,
    onAddLabelDone: () -> Unit,
    onAddDelete: () -> Unit,
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

    LaunchedEffect(key1 = isEditMode, block = {
        if (isEditMode && !isFirstTime) {
            focusRequester.requestFocus()
            isFirstTime = true
        }
    })
    NoteTextField(
        modifier =
        Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .onFocusChanged { isFocus = it.isFocused },
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = stringResource(Rd.string.modules_designsystem_create_new_label),
                color = TextStyle.Default.color.copy(alpha = 0.5f),
            )
        },
        supportingText = { Text(text = if (errorOccur) stringResource(Rd.string.modules_designsystem_label_already_exists) else "") },
        isError = errorOccur,
        leadingIcon = {
            if (isFocus) {
                IconButton(onClick = {
                    onAddDelete()
                    focusRequester.freeFocus()
                }) {
                    Icon(imageVector = NoteIcon.Clear, contentDescription = "Clear")
                }
            } else {
                Icon(imageVector = NoteIcon.Add, contentDescription = "add")
            }
        },
        trailingIcon = {
            IconButton(onClick = { onAddLabelDone() }) {
                Icon(imageVector = NoteIcon.Done, contentDescription = "add")
            }
        },
        keyboardActions = KeyboardActions { onAddLabelDone() },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
    )
}

@Composable
fun LabelTextField(
    labelUiState: LabelUiState,
    onValue: (String, Long) -> Unit = { _, _ -> },
    onDelete: (Long) -> Unit = {},
) {
    val focusRequester by remember {
        mutableStateOf(FocusRequester())
    }
    var isFocus by remember {
        mutableStateOf(false)
    }
    val focusManager = LocalFocusManager.current
    NoteTextField(
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .onFocusChanged { focusState -> isFocus = focusState.isFocused },
        value = labelUiState.label,
        onValueChange = { onValue(it, labelUiState.id) },
        leadingIcon = {
            if (isFocus) {
                IconButton(onClick = { onDelete(labelUiState.id) }) {
                    Icon(imageVector = NoteIcon.Delete, contentDescription = "add")
                }
            } else {
                Icon(imageVector = NoteIcon.Label, contentDescription = "add")
            }
        },
        trailingIcon = {
            if (isFocus) {
                IconButton(onClick = { focusManager.clearFocus() }) {
                    Icon(imageVector = NoteIcon.Done, contentDescription = "add")
                }
            } else {
                IconButton(onClick = { focusRequester.requestFocus() }) {
                    Icon(imageVector = NoteIcon.Edit, contentDescription = "add")
                }
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions { focusManager.clearFocus() },
    )
}

@Preview
@Composable
fun LabelScreenPreview() {
    LabelScreen(
        labelScreenUiState =
        LabelScreenUiState(
            labels = listOf(
                LabelUiState(id = 124L, label = "Tabatha"),
                LabelUiState(id = 3724L, label = "Vicent"),
                LabelUiState(id = 1958L, label = "Isabelle"),
            ).toImmutableList(),
        ),
    )
}
