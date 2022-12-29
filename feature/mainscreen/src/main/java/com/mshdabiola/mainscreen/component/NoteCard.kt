package com.mshdabiola.mainscreen.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.mshdabiola.designsystem.icon.NoteIcon
import com.mshdabiola.mainscreen.state.NotePadUiState
import com.mshdabiola.mainscreen.state.NoteUiState
import kotlinx.collections.immutable.toImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteCard(notePad: NotePadUiState, onCardClick: (Long) -> Unit = {}) {
    val unCheckNote by remember(notePad.checks) {
        derivedStateOf { notePad.checks.filter { !it.isCheck } }
    }
    val numberOfChecked by remember(key1 = notePad.checks) {
        derivedStateOf { notePad.checks.count { it.isCheck } }
    }
    val bg = if (notePad.note.background != -1) {
        Color.Transparent
    } else {
        if (notePad.note.color != -1)
            NoteIcon.noteColors[notePad.note.color]
        else
            MaterialTheme.colorScheme.background
    }

    val sColor = if (notePad.note.background != -1)
        NoteIcon.background[notePad.note.background].fgColor
    else
        MaterialTheme.colorScheme.secondaryContainer


    val painter = if (notePad.note.background != -1)
        rememberVectorPainter(image = ImageVector.vectorResource(id = NoteIcon.background[notePad.note.background].bg))
    else
        null

    var size by remember {
        mutableStateOf(IntSize.Zero)
    }

    val de = LocalDensity.current

    OutlinedCard(
        modifier = Modifier,
        border = BorderStroke(1.dp, sColor),
        colors = CardDefaults.outlinedCardColors(containerColor = bg),
        onClick = { notePad.note.id?.let { onCardClick(it) } }
    ) {
        Box {
            if (notePad.note.background != -1) {
                Image(
                    painter = painterResource(id = NoteIcon.background[notePad.note.background].bg),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(
                        with(de) { size.width.toDp() },
                        with(de) { size.height.toDp() })
                )
            }

            Column(
                Modifier
                    .onSizeChanged {
                        size = it
                    }
                    .padding(16.dp)
            ) {
                Text(
                    text = notePad.note.title.ifEmpty { notePad.note.detail },
                    style = MaterialTheme.typography.titleMedium
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
                            Checkbox(checked = it.isCheck, onCheckedChange = {}, enabled = false)
                            Text(it.content, style = MaterialTheme.typography.bodyMedium)

                        }
                    }
                    if (unCheckNote.size > 10) {
                        Text(text = "....")
                    }
                    if (numberOfChecked > 0) {
                        Text(text = "+ $numberOfChecked checked items")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState(), enabled = false)
                ) {
                    notePad.labels.forEach {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = sColor,
                            border = BorderStroke(1.dp, Color.Gray)
                        ) {
                            Text(text = it, modifier = Modifier.padding(8.dp))
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                }
            }
        }


    }
}

@Preview
@Composable
fun NoteCardPreview() {
    NoteCard(
        notePad = NotePadUiState(
            note = NoteUiState(
                id = null,
                title = "Mandy",
                detail = "Lamia ",
                date = 314L,
                isCheck = false,
                color = 2,
                isPin = false,
                background = 3
            ),
            labels = listOf(
                "ade",
                "food",
                "abiola",
                "kdlskdflsjfslf",
                "klslssljsl",
                "alskfk"
            ).toImmutableList()
        )
    )
}