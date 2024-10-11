package com.mshdabiola.detail

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mshdabiola.model.Label
import com.mshdabiola.model.NotePad
import com.mshdabiola.ui.LabelCard

@Composable
fun ActionEditScreen(
    id: Long,
) {
    var showLabel by remember {
        mutableStateOf(false)
    }
    ActionEditScreen(
        //  onSaveNote = shareViewModel::saveNote,
        // labels = shareViewModel.selectLabels,
        // showLabel = shareViewModel.showLabel,
        showLabelDialog = { showLabel = true },
    )

    EditLabels(
        show = showLabel,
        //  labels = shareViewModel.labels,
        onDismissRequest = { showLabel = false },
        //  onToggleLabel = shareViewModel::toggleLabel,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ActionEditScreen(
    note: NotePad = NotePad(),
    showLabel: Boolean = true,
    labels: List<Label> = emptyList<Label>(),
    onSaveNote: (String, String, List<Uri>) -> Unit = { _, _, _ -> },
    showLabelDialog: () -> Unit = {},
    onFinish: () -> Unit = {},
) {
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                modifier = Modifier.imePadding(),
                text = { Text(text = "Add note") },
                icon = { Icon(imageVector = Icons.Default.Add, contentDescription = "Add") },
                onClick = {
//                    onSaveNote(title, subject, images.flatten())
//                    (context as Activity).finish()
                },
            )
        },
        topBar = {
            TopAppBar(
                title = { },
                actions = {
                    IconButton(onClick = onFinish) {
                        Icon(imageVector = Icons.Outlined.Cancel, contentDescription = "Cancel")
                    }
                },
            )
        },
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (note.images.isNotEmpty()) {
                item {
                    note.images.reversed().chunked(3).forEach { imageList ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                        ) {
                            imageList.forEach {
                                AsyncImage(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(200.dp),
                                    model = it,
                                    contentDescription = "",
                                    contentScale = ContentScale.Crop,
                                )
                            }
                        }
                    }
                }
            }
            item {
                TextField(
                    value = note.title,
                    onValueChange = { },
                    placeholder = { Text(text = "Title") },
                    textStyle = MaterialTheme.typography.titleLarge,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        autoCorrectEnabled = true,
                        imeAction = ImeAction.Next,
                    ),
                    modifier = Modifier
                        .fillMaxWidth(),

                )
            }

            item {
                TextField(
                    value = note.detail,
                    onValueChange = { },
                    textStyle = MaterialTheme.typography.bodyMedium,
                    placeholder = { Text(text = "Subject") },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
//        containerColor = Color.Transparent,
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        autoCorrectEnabled = true,
                        keyboardType = KeyboardType.Text,
                        // imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { // Todo(Save)
                        },
                    ),
                    modifier = Modifier
                        .fillMaxWidth(),

                )
            }
            if (labels.isNotEmpty()) {
                item {
                    FlowRow(
                        modifier = Modifier.padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        labels.forEach {
                            LabelCard(
                                name = it.label,
                                color = MaterialTheme.colorScheme.secondary,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
            }
            if (showLabel) {
                item {
                    TextButton(onClick = { showLabelDialog() }) {
                        Text("Add Labels")
                    }
                }
            }
        }
    }
}

@Composable
fun EditLabels(
    show: Boolean = false,
    onDismissRequest: () -> Unit = {},
    labels: List<Label> = emptyList<Label>(),
    checks: List<Long> = emptyList<Long>(),
    onToggleLabel: (Long) -> Unit = {},
) {
    AnimatedVisibility(visible = show) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text(text = "Add Labels") },
            confirmButton = {
                TextButton(onClick = { onDismissRequest() }) {
                    Text("Close")
                }
            },
            text = {
                LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                    items(labels, key = { it.id }) { label ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,

                        ) {
                            Text(text = label.label, modifier = Modifier.weight(1f))
                            Checkbox(
                                checked = checks.contains(label.id),
                                onCheckedChange = { onToggleLabel(label.id) },
                            )
                        }
                    }
                }
            },
        )
    }
}

@Preview
@Composable
fun ActionEditScreenPreview() {
    ActionEditScreen(
        labels = listOf(
            Label(
                id = 759L,
                label = "Emanuel",
            ),
            Label(
                id = 79L,
                label = "Emanuel",
            ),
            Label(
                id = 59L,
                label = "Emanuel",
            ),
            Label(
                id = 7529L,
                label = "Emanuel",
            ),
            Label(
                id = 7519L,
                label = "Emanuel",
            ),
            Label(
                id = 79L,
                label = "Emanuel",
            ),
            Label(
                id = 59L,
                label = "Emanuel",
            ),
            Label(
                id = 7529L,
                label = "Emanuel",
            ),
            Label(
                id = 7519L,
                label = "Emanuel",
            ),

        ),
    )
}

@Preview
@Composable
fun DialogPreview() {
    EditLabels(
        show = true,
        labels = listOf(
            Label(
                id = 759L,
                label = "Emanuel",
            ),
            Label(
                id = 79L,
                label = "Emanuel",
            ),
            Label(
                id = 59L,
                label = "Emanuel",
            ),
            Label(
                id = 7529L,
                label = "Emanuel",
            ),
            Label(
                id = 7519L,
                label = "Emanuel",
            ),

        ),
    )
}
