package com.mshdabiola.editscreen.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.mshdabiola.bottomsheet.ModalBottomSheet
import com.mshdabiola.bottomsheet.ModalState
import com.mshdabiola.designsystem.icon.NoteIcon
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteOptionBottomSheet(
    modalState: ModalState,
    currentColor: Int,
    currentImage: Int,
    onDelete: () -> Unit = {},
    onCopy: () -> Unit = {},
    onSendNote: () -> Unit = {},
    onLabel: () -> Unit = {},
) {

    val coroutineScope = rememberCoroutineScope()
    val background = if (currentImage != -1) {
        NoteIcon.background[currentImage].fgColor
    } else {
        if (currentColor != -1) {
            NoteIcon.noteColors[currentColor]
        } else {
            MaterialTheme.colorScheme.surface
        }
    }

    ModalBottomSheet(modalState = modalState) {
        Surface(
            color = background
        ) {
            Column(modifier = Modifier.padding(bottom = 36.dp)) {
                NavigationDrawerItem(icon = {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = ""
                    )
                }, label = { Text(text = "Delete") },
                    selected = false, onClick = {
                        onDelete()
                        coroutineScope.launch { modalState.hide() }
                    },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = background)
                )

                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = NoteIcon.Copy),
                            contentDescription = ""
                        )
                    },
                    label = { Text(text = "Make a copy") },
                    selected = false,
                    onClick = {
                        onCopy()
                        coroutineScope.launch { modalState.hide() }

                    },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = background)
                )
                NavigationDrawerItem(icon = {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = ""
                    )
                }, label = { Text(text = "Send") },
                    selected = false,
                    onClick = {
                        onSendNote()
                        coroutineScope.launch { modalState.hide() }
                    },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = background)
                )
                NavigationDrawerItem(icon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = NoteIcon.Label),
                        contentDescription = ""
                    )
                }, label = { Text(text = "Labels") },
                    selected = false, onClick = {
                        onLabel()
                        coroutineScope.launch { modalState.hide() }

                    },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = background)
                )

//                NavigationDrawerItem(icon = {
//                    Icon(
//                        imageVector = ImageVector.vectorResource(id = NoteIcon.Save),
//                        contentDescription = ""
//                    )
//                }, label = { Text(text = "Save as txt") },
//                    selected = false, onClick = {
//                        onSaveText()
//                        coroutineScope.launch { modalState.hide() }
//
//                    })


            }
        }

    }
}