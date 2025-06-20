import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mshdabiola.designsystem.icon.NoteIcon
import com.mshdabiola.main.SelectState
import com.mshdabiola.model.NoteDisplayCategory
import com.mshdabiola.model.NoteType
import com.mshdabiola.designsystem.R as Rd

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainTopBar(
    modifier: Modifier = Modifier,
    noteDisplayCategory: NoteDisplayCategory,
    isGrid: Boolean = false,
    selectState: SelectState? = null,
    labelName: String? = null,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
    onDisplayModeChange: () -> Unit = {},
    onHamburgerMenuClick: () -> Unit = {},

    onClearSelection: () -> Unit = {},
    onPinNotes: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onSelectColor: () -> Unit = {},
    onLabelNotes: () -> Unit = {},
    onArchive: () -> Unit = {},
    onDeleteNotes: () -> Unit = {},
    onShareNote: () -> Unit = {},
    onCopyNote: () -> Unit = {},
    onDeleteForever: () -> Unit = {},
    onRestore: () -> Unit = {},

    onSearchClick: () -> Unit = {},
    onLabelNameChange: () -> Unit = {},
    onDeleteLabel: () -> Unit = {},

    onDeleteAllTrash: () -> Unit = {},

) {
    val navigationAction: @Composable () -> Unit = {
        if (selectState != null) {
            IconButton(onClick = onClearSelection) {
                Icon(imageVector = NoteIcon.Clear, contentDescription = "clear note")
            }
        } else {
            IconButton(onClick = onHamburgerMenuClick) {
                Icon(imageVector = NoteIcon.Menu, contentDescription = "menu")
            }
        }
    }
    val actions: @Composable RowScope.() -> Unit =
        {
            if (selectState != null) {
                when (noteDisplayCategory.noteType) {
                    NoteType.TRASH -> {
                        var showDropDown by remember {
                            mutableStateOf(false)
                        }

                        IconButton(
                            modifier = Modifier.testTag("main:restore"),
                            onClick = onRestore,
                        ) {
                            Icon(
                                imageVector = NoteIcon.RestoreFromTrash,
                                contentDescription = "restore note",
                            )
                        }
                        Box {
                            IconButton(
                                modifier = Modifier.testTag("main:more"),
                                onClick = { showDropDown = true },
                            ) {
                                Icon(NoteIcon.MoreVert, contentDescription = "more")
                            }
                            DropdownMenu(
                                expanded = showDropDown,
                                onDismissRequest = { showDropDown = false },
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text =
                                            stringResource(Rd.string.modules_designsystem_delete_forever),
                                        )
                                    },
                                    onClick = {
                                        showDropDown = false
                                        onDeleteForever()
                                    },
                                )
                            }
                        }
                    }

                    else -> {
                        var showDropDown by remember {
                            mutableStateOf(false)
                        }

                        IconButton(
                            modifier = Modifier.testTag("main:pin"),
                            onClick = onPinNotes,
                        ) {
                            Icon(
                                imageVector = if (selectState.isAllPin) NoteIcon.PushPinD else NoteIcon.PushPin, // painterResource(id = if (isAllPin) NoteIcon.Pin else NoteIcon.PinFill),
                                contentDescription = "pin note",
                            )
                        }
                        IconButton(
                            modifier = Modifier.testTag("main:notification"),
                            onClick = onNotificationClick,
                        ) {
                            Icon(
                                imageVector = NoteIcon.Notification,
                                contentDescription = "notification",
                            )
                        }
                        IconButton(
                            modifier = Modifier.testTag("main:color"),
                            onClick = onSelectColor,
                        ) {
                            Icon(
                                imageVector = NoteIcon.ColorLens,
                                contentDescription = "color",
                            )
                        }
                        IconButton(
                            modifier = Modifier.testTag("main:label"),
                            onClick = onLabelNotes,
                        ) {
                            Icon(imageVector = NoteIcon.Label, contentDescription = "Label")
                        }
                        Box {
                            IconButton(
                                modifier = Modifier.testTag("main:more"),
                                onClick = { showDropDown = true },
                            ) {
                                Icon(NoteIcon.MoreVert, contentDescription = "more")
                            }
                            DropdownMenu(
                                expanded = showDropDown,
                                onDismissRequest = { showDropDown = false },
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text =
                                            if (noteDisplayCategory.noteType == NoteType.ARCHIVE) {
                                                stringResource(Rd.string.modules_designsystem_unarchive)
                                            } else {
                                                stringResource(Rd.string.modules_designsystem_archive)
                                            },
                                        )
                                    },
                                    onClick = {
                                        showDropDown = false
                                        onArchive()
                                    },
                                )
                                DropdownMenuItem(
                                    text = { Text(text = stringResource(Rd.string.modules_designsystem_delete)) },
                                    onClick = {
                                        showDropDown = false
                                        onDeleteNotes()
                                    },
                                )
                                if (selectState.setOfSelected.size == 1) {
                                    DropdownMenuItem(
                                        text = { Text(text = stringResource(Rd.string.modules_designsystem_make_a_copy)) },
                                        onClick = {
                                            showDropDown = false
                                            onCopyNote()
                                        },
                                    )
                                    DropdownMenuItem(
                                        text = { Text(text = stringResource(Rd.string.modules_designsystem_send)) },
                                        onClick = {
                                            showDropDown = false
                                            onShareNote()
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                when (noteDisplayCategory.noteType) {
                    NoteType.NOTE -> {
                        IconButton(onClick = { onDisplayModeChange() }) {
                            if (!isGrid) {
                                Icon(imageVector = NoteIcon.GridView, contentDescription = "grid")
                            } else {
                                Icon(
                                    imageVector = NoteIcon.ViewAgenda,
                                    contentDescription = "column",
                                )
                            }
                        }
                    }

                    NoteType.REMINDER -> {
                        IconButton(onClick = onSearchClick) {
                            Icon(
                                imageVector = NoteIcon.Search,
                                contentDescription = "search",
                            )
                        }
                        IconButton(onClick = { onDisplayModeChange() }) {
                            if (!isGrid) {
                                Icon(imageVector = NoteIcon.GridView, contentDescription = "grid")
                            } else {
                                Icon(
                                    imageVector = NoteIcon.ViewAgenda,
                                    contentDescription = "column",
                                )
                            }
                        }
                    }

                    NoteType.LABEL -> {
                        var showDropDown by remember {
                            mutableStateOf(false)
                        }

                        IconButton(onClick = onSearchClick) {
                            Icon(
                                imageVector = NoteIcon.Search,
                                contentDescription = "search",
                            )
                        }

                        Box {
                            IconButton(onClick = { showDropDown = true }) {
                                Icon(NoteIcon.MoreVert, contentDescription = "more")
                            }
                            DropdownMenu(
                                expanded = showDropDown,
                                onDismissRequest = { showDropDown = false },
                            ) {
                                DropdownMenuItem(
                                    text = { Text(text = stringResource(Rd.string.modules_designsystem_rename_label)) },
                                    onClick = {
                                        showDropDown = false
                                        onLabelNameChange()
                                    },
                                )
                                DropdownMenuItem(
                                    text = { Text(text = stringResource(Rd.string.modules_designsystem_delete_label)) },
                                    onClick = {
                                        showDropDown = false
                                        onDeleteLabel()
                                    },
                                )
                            }
                        }
                    }

                    NoteType.TRASH -> {
                        var showDropDown by remember {
                            mutableStateOf(false)
                        }
                        Box {
                            IconButton(onClick = { showDropDown = true }) {
                                Icon(NoteIcon.MoreVert, contentDescription = "more")
                            }
                            DropdownMenu(
                                expanded = showDropDown,
                                onDismissRequest = { showDropDown = false },
                            ) {
                                DropdownMenuItem(
                                    text = { Text(text = stringResource(Rd.string.modules_designsystem_empty_trash)) },
                                    onClick = {
                                        showDropDown = false
                                        onDeleteAllTrash()
                                    },
                                )
                            }
                        }
                    }

                    NoteType.ARCHIVE -> {
                        IconButton(onClick = onSearchClick) {
                            Icon(
                                imageVector = NoteIcon.Search,
                                contentDescription = "search",
                            )
                        }
                        IconButton(onClick = { onDisplayModeChange() }) {
                            if (!isGrid) {
                                Icon(imageVector = NoteIcon.GridView, contentDescription = "grid")
                            } else {
                                Icon(
                                    imageVector = NoteIcon.ViewAgenda,
                                    contentDescription = "column",
                                )
                            }
                        }
                    }
                }
            }
        }

    val label = selectState?.setOfSelected?.size?.toString()
        ?: when (noteDisplayCategory.noteType) {
            NoteType.NOTE -> "Note"
            NoteType.REMINDER -> "Reminder"
            NoteType.LABEL -> labelName ?: ""
            NoteType.TRASH -> "Trash"
            NoteType.ARCHIVE -> "Archive"
        }
    val color = if (selectState != null) {
        TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        )
    } else {
        TopAppBarDefaults.topAppBarColors(
            scrolledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
        )
    }

    TopAppBar(
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        navigationIcon = navigationAction,
        title = {
            if (noteDisplayCategory.noteType == NoteType.NOTE) {
                OutlinedCard(
                    onClick = onSearchClick,
                    colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                ) {
                    Box(
                        modifier = Modifier.padding(
                            horizontal = 64.dp,

                            vertical = 4.dp,
                        ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            style = MaterialTheme.typography.labelLarge,

                            text = stringResource(Rd.string.modules_designsystem_search_note),
                        )
                    }
                }
            } else {
                Text(text = label)
            }
        },
        subtitle = {},
        actions = actions,
        colors = color,
        titleHorizontalAlignment = if (noteDisplayCategory.noteType == NoteType.NOTE) {
            Alignment.CenterHorizontally
        } else {
            Alignment.Start
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
fun MainTopBarPreview() {
    MainTopBar(
        noteDisplayCategory = NoteDisplayCategory(
            labelId = 1,
            noteType = NoteType.NOTE,
        ),
        isGrid = false,
        selectState = null,
        labelName = "Label Name",
    )
}
