package com.mshdabiola.drawing

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mshdabiola.designsystem.icon.NoteIcon
import com.mshdabiola.designsystem.R as Rd

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawingScreen(
    onBackk: () -> Unit = {},
    controller: DrawingController = remember { DrawingController() },
    drawingUiState: DrawingUiState = DrawingUiState(),
    onDeleteImage: () -> Unit = {},
    onCopy: () -> Unit = {},
    onSend: () -> Unit = {},
) {
    var showDropDown by remember {
        mutableStateOf(false)
    }

    Scaffold(

        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBackk) {
                        Icon(
                            imageVector = NoteIcon.ArrowBack,
                            contentDescription = "back",
                        )
                    }
                },
                title = {
                    Text(stringResource(Rd.string.modules_designsystem_drawing))
                },

                actions = {
                    IconButton(
                        enabled = controller.canUndo,
                        onClick = { controller.undo() },
                    ) {
                        Icon(imageVector = NoteIcon.Undo, contentDescription = "redo")
                    }
                    IconButton(
                        enabled = controller.canRedo,
                        onClick = { controller.redo() },
                    ) {
                        Icon(imageVector = NoteIcon.Redo, contentDescription = "redo")
                    }
                    Box {
                        IconButton(
                            onClick = { showDropDown = true },
                            enabled = drawingUiState.filePath != null,
                        ) {
                            Icon(NoteIcon.MoreVert, contentDescription = "more")
                        }
                        DropdownMenu(
                            expanded = showDropDown,
                            onDismissRequest = { showDropDown = false },
                        ) {
//                            DropdownMenuItem(
//                                text = { Text(text = stringResource(Rd.string.modules_designsystem_grab_image_text)) },
//                                onClick = {
//                                    showDropDown = false
//                                    //  onGrabText()
//                                },
//                            )
                            DropdownMenuItem(
                                text = { Text(text = stringResource(Rd.string.modules_designsystem_copy)) },
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
                            DropdownMenuItem(
                                text = { Text(text = stringResource(Rd.string.modules_designsystem_delete)) },
                                onClick = {
                                    showDropDown = false
                                    onDeleteImage()
                                },
                            )
                        }
                    }
                },
            )
        },
        bottomBar = {
            DrawingBar(
                modifier = Modifier
                    .padding(horizontal = 8.dp),
                controller = controller,
            )
        },
    ) { paddingValues: PaddingValues ->
        Box(Modifier.padding(paddingValues)) {
            Board(
                modifier = Modifier.fillMaxSize(),
                controller = controller,
            )
        }
    }
}

@Preview
@Composable
fun DrawingScreenPreview() {
    DrawingScreen()
}
