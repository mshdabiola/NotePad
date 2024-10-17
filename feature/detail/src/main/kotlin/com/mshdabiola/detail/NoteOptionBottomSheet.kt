package com.mshdabiola.detail

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import com.mshdabiola.designsystem.icon.NoteIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteOptionBottomSheet(
    show: Boolean,
    currentColor: Int,
    currentImage: Int,
    onDelete: () -> Unit = {},
    onCopy: () -> Unit = {},
    onSendNote: () -> Unit = {},
    onLabel: () -> Unit = {},
    onDismissRequest: () -> Unit = {},
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

    if (show) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            containerColor = background,
        ) {
            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "",
                    )
                },
                label = { Text(text = stringResource(R.string.feature_detail_delete)) },
                selected = false,
                onClick = {
                    onDelete()
                    onDismissRequest()
                },
                colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = background),
            )

            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.ContentCopy,
                        contentDescription = "",
                    )
                },
                label = { Text(text = stringResource(R.string.feature_detail_make_a_copy)) },
                selected = false,
                onClick = {
                    onCopy()
                    onDismissRequest()
                },
                colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = background),
            )
            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "",
                    )
                },
                label = { Text(text = stringResource(R.string.feature_detail_send)) },
                selected = false,
                onClick = {
                    onSendNote()
                    onDismissRequest()
                },
                colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = background),
            )
            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Label,
                        contentDescription = "",
                    )
                },
                label = { Text(text = stringResource(R.string.feature_detail_labels)) },
                selected = false,
                onClick = {
                    onLabel()
                    onDismissRequest()
                },
                colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = background),
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
