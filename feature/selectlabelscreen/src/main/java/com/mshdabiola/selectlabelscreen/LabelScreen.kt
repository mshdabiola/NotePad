package com.mshdabiola.selectlabelscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mshdabiola.designsystem.icon.NoteIcon
import com.mshdabiola.ui.FirebaseScreenLog
import com.mshdabiola.ui.NoteTextField
import kotlinx.collections.immutable.toImmutableList
import com.mshdabiola.designsystem.R as Rd

@Composable
fun LabelScreen(onBack: () -> Unit, viewModel: LabelViewModel = hiltViewModel()) {
    FirebaseScreenLog(screen = "select_label_screen")
    LabelScreen(
        labelScreenUiState = viewModel.labelScreenUiState,
        onBack = onBack,
        onCheckClick = viewModel::onCheckClick,
        onSearchText = viewModel::onSearchChange,
        onCreateLabel = viewModel::onCreateLabel,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabelScreen(
    labelScreenUiState: LabelScreenUiState,
    onBack: () -> Unit = {},
    onCheckClick: (Long) -> Unit = {},
    onSearchText: (String) -> Unit = {},
    onCreateLabel: () -> Unit = {},
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
                    NoteTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = labelScreenUiState.editText,
                        placeholder = { Text(text = stringResource(Rd.string.modules_designsystem_enter_text)) },
                        onValueChange = onSearchText,
                    )
                },
            )
        },
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            if (labelScreenUiState.showAddLabel) {
                TextButton(onClick = { onCreateLabel() }) {
                    Icon(imageVector = NoteIcon.Add, contentDescription = "add")
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = "${stringResource(id = Rd.string.modules_designsystem_create)} \"${labelScreenUiState.editText}\"")
                }
            }
            LazyColumn {
                items(
                    items = labelScreenUiState.labels,
                ) {
                    LabelText(
                        labelUiState = it,
                        onCheckClick = onCheckClick,
                    )
                }
            }
        }
    }
}

@Composable
fun LabelText(
    labelUiState: LabelUiState,
    onCheckClick: (Long) -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,

    ) {
        Icon(imageVector = NoteIcon.Label, contentDescription = "")
        Spacer(modifier = Modifier.width(8.dp))
        Text(modifier = Modifier.weight(1f), text = labelUiState.label)
        TriStateCheckbox(
            state = labelUiState.toggleableState,
            onClick = { onCheckClick(labelUiState.id) },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NoteTextPreview() {
    LabelText(
        labelUiState = LabelUiState(
            id = 8382L,
            label = "Derron",
            toggleableState = ToggleableState.Off,
        ),
    )
}

@Preview
@Composable
fun LabelScreenPreview() {
    LabelScreen(
        labelScreenUiState = LabelScreenUiState(
            labels = listOf(

                LabelUiState(
                    id = 8382L,
                    label = "Derron",
                    toggleableState = ToggleableState.Off,
                ),
                LabelUiState(
                    id = 8983L,
                    label = "Nakeshia",
                    toggleableState = ToggleableState.Indeterminate,
                ),
                LabelUiState(
                    id = 8983L,
                    label = "Nakeshia",
                    toggleableState = ToggleableState.Indeterminate,
                ),
                LabelUiState(id = 8983L, label = "Nakeshia", toggleableState = ToggleableState.On),

            ).toImmutableList(),
        ),
    )
}
