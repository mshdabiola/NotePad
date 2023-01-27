package com.mshdabiola.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mshdabiola.designsystem.component.state.NoteCheckUiState
import com.mshdabiola.designsystem.component.state.NotePadUiState
import com.mshdabiola.designsystem.component.state.NoteUiState
import com.mshdabiola.designsystem.component.state.NoteVoiceUiState
import com.mshdabiola.designsystem.icon.NoteIcon
import com.mshdabiola.searchscreen.FlowLayout2
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.Clock

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteCard(
    notePad: NotePadUiState,
    onCardClick: (Long) -> Unit = {},
    onLongClick: (Long) -> Unit = {},
) {
    val unCheckNote by remember(notePad.checks) {
        derivedStateOf { notePad.checks.filter { !it.isCheck } }
    }
    val numberOfChecked by remember(key1 = notePad.checks) {
        derivedStateOf { notePad.checks.count { it.isCheck } }
    }
    val haveVoice by remember(notePad.voices) {
        derivedStateOf { notePad.voices.isNotEmpty() }
    }
    val bg = if (notePad.note.background != -1) {
        Color.Transparent
    } else {
        if (notePad.note.color != -1) {
            NoteIcon.noteColors[notePad.note.color]
        } else {
            MaterialTheme.colorScheme.background
        }
    }

    val sColor = if (notePad.note.background != -1) {
        NoteIcon.background[notePad.note.background].fgColor
    } else {
        MaterialTheme.colorScheme.secondaryContainer
    }

    var size by remember {
        mutableStateOf(IntSize.Zero)
    }
    val images = remember(notePad.images) {
        notePad.images.reversed().chunked(3)
    }

    val de = LocalDensity.current

    OutlinedCard(
        modifier = Modifier.combinedClickable(
            onClick = { notePad.note.id.let { onCardClick(it) } },
            onLongClick = { notePad.note.id.let { onLongClick(it) } },
        ),
        border = if (notePad.note.selected) {
            BorderStroke(3.dp, Color.Blue)
        } else {
            BorderStroke(
                1.dp,
                sColor,
            )
        },
        colors = CardDefaults.outlinedCardColors(containerColor = bg),
    ) {
        Box {
            if (notePad.note.background != -1) {
                Image(
                    painter = painterResource(id = NoteIcon.background[notePad.note.background].bg),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(
                        with(de) { size.width.toDp() },
                        with(de) { size.height.toDp() },
                    ),
                )
            }

            Column(
                Modifier
                    .onSizeChanged {
                        size = it
                    },
            ) {
                if (notePad.images.isNotEmpty()) {
                    images.forEach { imageList ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                        ) {
                            imageList.forEach {
                                AsyncImage(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(100.dp),
                                    model = it.imageName,
                                    contentDescription = "",
                                    contentScale = ContentScale.Crop,
                                )
                            }
                        }
                    }
                }

                if (!notePad.isImageOnly()) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                    ) {
                        Text(
                            text = notePad.note.title.ifEmpty { notePad.note.detail },
                            style = if (notePad.note.title.isNotEmpty()) {
                                MaterialTheme.typography.titleMedium
                            } else {
                                MaterialTheme.typography.bodyMedium
                            },
                            maxLines = 10,
                        )
                        if (!notePad.note.isCheck) {
                            if (notePad.note.title.isNotEmpty()) {
                                if (notePad.note.detail.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = notePad.note.detail,
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 10,

                                    )
                                }
                            }
                        } else {
                            unCheckNote.take(10).forEach {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        modifier = Modifier.size(16.dp),
                                        imageVector = Icons.Default.CheckBoxOutlineBlank,
                                        contentDescription = "",
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        it.content,
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 1,
                                    )
                                }
                            }
                            if (unCheckNote.size > 10) {
                                Text(text = "....")
                            }
                            if (numberOfChecked > 0) {
                                Text(text = "+ $numberOfChecked checked items")
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        FlowLayout2(
                            verticalSpacing = 4.dp,
                        ) {
                            if (haveVoice) {
                                Icon(
                                    imageVector = Icons.Default.PlayCircle,
                                    contentDescription = "play",
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            if (notePad.note.reminder > 0) {
                                ReminderCard(
                                    remainder = notePad.note.reminder,
                                    interval = notePad.note.interval,
                                    color = sColor,
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            notePad.labels.forEach {
                                LabelCard(name = it, color = sColor)
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NoteCardPreview() {
    NoteCard(
        notePad = NotePadUiState(
            note = NoteUiState(
                id = 1,
                title = "Mandy abiola",
                detail = "Lamia moshood",
                editDate = 314L,
                isCheck = true,
                reminder = Clock.System.now().toEpochMilliseconds(),
                color = 2,
                isPin = false,
                background = 3,
                selected = true,
            ),
            labels = listOf(
                "ade",
                "food",
                "abiola",
                "kdlskdflsjfslf",
                "klslssljsl",
                "alskfk",
            ).toImmutableList(),
            checks = listOf(
                NoteCheckUiState(
                    id = 2418L,
                    noteId = 6429L,
                    content = "Maegan",
                    isCheck = false,
                    focus = false,
                ),
                NoteCheckUiState(
                    id = 2418L,
                    noteId = 6429L,
                    content = "Book",
                    isCheck = false,
                    focus = false,
                ),
            ).toImmutableList(),
            voices = listOf(
                NoteVoiceUiState(
                    id = 500L,
                    noteId = 8001L,
                    voiceName = "Danniel",
                    length = 1940L,
                    currentProgress = .179f,
                    isPlaying = false,

                ),
            ).toImmutableList(),
        ),
    )
}
