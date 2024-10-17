package com.mshdabiola.playnotepad.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mshdabiola.designsystem.icon.NoteIcon
import com.mshdabiola.model.Label
import com.mshdabiola.model.NoteType
import kotlinx.collections.immutable.toImmutableList
import com.mshdabiola.designsystem.R as Rd

@Composable
fun MainNavigation(

    onNavigation: (Long) -> Unit = {},
    currentMainArg: Long = NoteType.NOTE.index,
    navigateToLevel: (Boolean) -> Unit = {},
    labels: List<Label>,
    navigateToAbout: () -> Unit = {},
    navigateToSetting: () -> Unit = {},
) {
    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .width(300.dp),
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(state = rememberScrollState())
                .padding(horizontal = 8.dp)
                .safeDrawingPadding(),
        ) {
            Text(
                text = "Play NotePad",
                style = MaterialTheme.typography.headlineSmall
                    .copy(
                        brush = Brush.horizontalGradient(
                            0.2f to MaterialTheme.colorScheme.primary,
                            1f to MaterialTheme.colorScheme.secondary,
                        ),
                        shadow = Shadow(
                            color = Color.LightGray,
                            offset = Offset(4f, 2f),
                            blurRadius = 1f,
                        ),
                    ),

            )
            Spacer(
                modifier = Modifier.height(16.dp),

            )
            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = Rd.drawable.modules_designsystem_app_icon),
                        contentDescription = "note",
                    )
                },
                label = { Text(text = stringResource(Rd.string.modules_designsystem_notes)) },
                selected = currentMainArg == NoteType.NOTE.index,
                onClick = { onNavigation(NoteType.NOTE.index) },
            )
            NavigationDrawerItem(
                icon = {
                    Icon(imageVector = NoteIcon.Notification, contentDescription = "")
                },
                label = { Text(text = stringResource(Rd.string.modules_designsystem_reminders)) },
                selected = currentMainArg == NoteType.REMAINDER.index,
                onClick = { onNavigation(NoteType.REMAINDER.index) },
            )
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(Rd.string.modules_designsystem_labels),
                )
                TextButton(onClick = { navigateToLevel(false) }) {
                    Text(text = stringResource(Rd.string.modules_designsystem_edit))
                }
            }

            labels.forEach {
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = NoteIcon.Label,
                            contentDescription = "",
                        )
                    },
                    label = { Text(text = it.label) },
                    selected = currentMainArg == it.id,
                    onClick = { onNavigation(it.id) },
                )
            }
            NavigationDrawerItem(
                icon = {
                    Icon(imageVector = NoteIcon.Add, contentDescription = "")
                },
                label = { Text(text = stringResource(Rd.string.modules_designsystem_create_new_label)) },
                selected = false,
                onClick = { navigateToLevel(true) },
            )
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
            )

            NavigationDrawerItem(
                icon = {
                    Icon(imageVector = NoteIcon.Archive, contentDescription = "Archive")
                },
                label = { Text(text = stringResource(Rd.string.modules_designsystem_archive)) },
                selected = currentMainArg == NoteType.ARCHIVE.index,
                onClick = { onNavigation(NoteType.ARCHIVE.index) },
            )

            NavigationDrawerItem(
                icon = {
                    Icon(NoteIcon.Delete, contentDescription = "")
                },
                label = { Text(text = stringResource(Rd.string.modules_designsystem_trash)) },
                selected = currentMainArg == NoteType.TRASH.index,
                onClick = { onNavigation(NoteType.TRASH.index) },
            )
            NavigationDrawerItem(
                icon = {
                    Icon(NoteIcon.Settings, contentDescription = "")
                },
                label = { Text(text = stringResource(Rd.string.setting)) },
                selected = false,
                onClick = navigateToSetting,
            )
            NavigationDrawerItem(
                icon = {
                    Icon(NoteIcon.Info, contentDescription = "")
                },
                label = { Text(text = stringResource(Rd.string.modules_designsystem_about)) },
                selected = false,
                onClick = {
                    navigateToAbout()
                },
            )
        }
    }
}

@Preview
@Composable
fun MainNavigationPreview() {
    Column(Modifier.fillMaxSize()) {
        MainNavigation(
            labels = listOf(
                Label(id = 7955L, label = "Gillian"),
//                LabelUiState(id = 126L, label = "Laneisha"),
//                LabelUiState(id = 7955L, label = "Gillian"),
//                LabelUiState(id = 126L, label = "Laneisha"),
//                LabelUiState(id = 7955L, label = "Gillian"),
//                LabelUiState(id = 126L, label = "Laneisha"),
//                LabelUiState(id = 7955L, label = "Gillian"),
//                LabelUiState(id = 126L, label = "Laneisha"),
            ).toImmutableList(),
        )
    }
}
