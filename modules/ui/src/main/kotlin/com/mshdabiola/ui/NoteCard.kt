package com.mshdabiola.ui

import androidx.compose.animation.ExperimentalSharedTransitionApi
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import coil3.compose.AsyncImage
import com.mshdabiola.designsystem.icon.NoteIcon
import com.mshdabiola.model.NoteDrawing
import com.mshdabiola.model.NoteImage
import com.mshdabiola.model.NotePad
import kotlin.collections.chunked
import kotlin.text.ifEmpty
import com.mshdabiola.designsystem.R as Rd

@OptIn(ExperimentalFoundationApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun NoteCard(
    modifier: Modifier = Modifier,
    notePad: NotePad,
    isSelect: Boolean = false,
    onCardClick: (Long, Int, Int) -> Unit = { _, _, _ -> },
    onLongClick: (Long) -> Unit = {},
    type: String = "note",
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
    val images = remember(notePad.images, notePad.drawings) {
        notePad.getVisuals().reversed().chunked(3)
    }

    val de = LocalDensity.current
    val sharedTransitionScope = LocalSharedStScope.current
    val animatedVisibilityScope = LocalNavAnimatedContentScope.current

    with(sharedTransitionScope) {
        OutlinedCard(
            modifier = modifier
                .sharedBounds(
                    sharedContentState = rememberSharedContentState("${type}_${notePad.note.id}"),
                    animatedVisibilityScope = animatedVisibilityScope,
                )
                .combinedClickable(
                    onClick = {
                        onCardClick(
                            notePad.note.id,
                            notePad.note.color,
                            notePad.note.background,
                        )
                    },
                    onLongClick = { onLongClick(notePad.note.id) },
                ),
            border = if (isSelect) {
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
                    if (images.isNotEmpty()) {
                        images.forEach { imageList ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp),
                            ) {
                                imageList.forEach {
                                    when (it) {
                                        is NoteImage -> {
                                            AsyncImage(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(100.dp),
                                                model = it.path,
                                                contentDescription = "",
                                                contentScale = ContentScale.Crop,
                                            )
                                        }

                                        is NoteDrawing -> {
                                            BoardViewer(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(100.dp),
                                                drawingPaths = it.drawingPaths,
                                            )
                                        }
                                    }
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
                                            imageVector = NoteIcon.CheckBoxOutlineBlank,
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
                                    Text(
                                        text = stringResource(
                                            Rd.string.modules_designsystem_checked_items_value,
                                            numberOfChecked,
                                        ),
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            FlowLayout2(
                                verticalSpacing = 4.dp,
                            ) {
                                if (haveVoice) {
                                    Icon(
                                        imageVector = NoteIcon.PlayCircle,
                                        contentDescription = "play",
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                notePad.notification?.let {
                                    ReminderCard(
                                        notification = it,
                                        color = sColor,
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                notePad.labels.forEach {
                                    LabelCard(name = it.name, color = sColor)
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
