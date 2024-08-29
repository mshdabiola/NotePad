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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Divider
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
import com.mshdabiola.designsystem.R
import com.mshdabiola.main.navigation.Main
import com.mshdabiola.model.NoteType
import com.mshdabiola.ui.state.LabelUiState
import com.mshdabiola.ui.state.NoteTypeUi
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
fun MainNavigation(

    onNavigation: (NoteTypeUi) -> Unit = {},
    currentMainArg: Main = Main(-1L),
    navigateToLevel: (Boolean) -> Unit = {},
    labels: ImmutableList<LabelUiState>,
    navigateToAbout: () -> Unit = {},

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
                        imageVector = ImageVector.vectorResource(id = R.drawable.modules_designsystem_app_icon),
                        contentDescription = "note",
                    )
                },
                label = { Text(text = stringResource(com.mshdabiola.mainscreen.R.string.feature_mainscreen_notes)) },
                selected = currentMainArg.id == -1L,
                onClick = { onNavigation(NoteTypeUi()) },
            )
            NavigationDrawerItem(
                icon = {
                    Icon(imageVector = Icons.Outlined.Notifications, contentDescription = "")
                },
                label = { Text(text = stringResource(com.mshdabiola.mainscreen.R.string.feature_mainscreen_reminders)) },
                selected = currentMainArg.id == -2L,
                onClick = { onNavigation(NoteTypeUi(type = NoteType.REMAINDER)) },
            )
            Divider(
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
                    text = stringResource(com.mshdabiola.mainscreen.R.string.feature_mainscreen_labels),
                )
                TextButton(onClick = { navigateToLevel(false) }) {
                    Text(text = stringResource(com.mshdabiola.mainscreen.R.string.feature_mainscreen_edit))
                }
            }

            labels.forEach {
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Label,
                            contentDescription = "",
                        )
                    },
                    label = { Text(text = it.label) },
                    selected = currentMainArg.id == it.id,
                    onClick = { onNavigation(NoteTypeUi(NoteType.LABEL, it.id)) },
                )
            }
            NavigationDrawerItem(
                icon = {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "")
                },
                label = { Text(text = stringResource(com.mshdabiola.mainscreen.R.string.feature_mainscreen_create_new_label)) },
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
                    Icon(imageVector = Icons.Outlined.Archive, contentDescription = "Archive")
                },
                label = { Text(text = stringResource(com.mshdabiola.mainscreen.R.string.feature_mainscreen_archive)) },
                selected = currentMainArg.id == -3L,
                onClick = { onNavigation(NoteTypeUi(NoteType.ARCHIVE)) },
            )

            NavigationDrawerItem(
                icon = {
                    Icon(Icons.Outlined.Delete, contentDescription = "")
                },
                label = { Text(text = stringResource(com.mshdabiola.mainscreen.R.string.feature_mainscreen_trash)) },
                selected = currentMainArg.id == -4L,
                onClick = { onNavigation(NoteTypeUi(NoteType.TRASH)) },
            )
//            NavigationDrawerItem(
//                icon = {
//                    Icon(Icons.Outlined.Settings, contentDescription = "")
//                },
//                label = { Text(text = "Setting") },
//                selected = false,
//                onClick = { })
            NavigationDrawerItem(
                icon = {
                    Icon(Icons.Outlined.Info, contentDescription = "")
                },
                label = { Text(text = stringResource(com.mshdabiola.mainscreen.R.string.feature_mainscreen_about)) },
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
                LabelUiState(id = 7955L, label = "Gillian"),
                LabelUiState(id = 126L, label = "Laneisha"),
                LabelUiState(id = 7955L, label = "Gillian"),
//                LabelUiState(id = 126L, label = "Laneisha"),
//                LabelUiState(id = 7955L, label = "Gillian"),
//                LabelUiState(id = 126L, label = "Laneisha"),
//                LabelUiState(id = 7955L, label = "Gillian"),
//                LabelUiState(id = 126L, label = "Laneisha"),
            ).toImmutableList(),
        )
    }
}
