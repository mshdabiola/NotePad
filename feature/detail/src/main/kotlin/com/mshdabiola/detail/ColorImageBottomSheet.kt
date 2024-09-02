package com.mshdabiola.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.FormatColorReset
import androidx.compose.material.icons.outlined.ImageNotSupported
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mshdabiola.designsystem.icon.NoteIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorAndImageBottomSheet(
    currentColor: Int,
    currentImage: Int,
    onColorClick: (Int) -> Unit = {},
    onImageClick: (Int) -> Unit = {},
    show: Boolean,
    onDismissRequest: () -> Unit = {},
) {
    rememberCoroutineScope()
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
            Column(Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)) {
                Text(
                    text = stringResource(R.string.feature_detail_color),
                    style = MaterialTheme.typography.titleSmall,
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        Surface(
                            onClick = { onColorClick(-1) },
                            shape = CircleShape,
                            color = Color.White,
                            modifier = Modifier.size(40.dp),
                            border = BorderStroke(
                                1.dp,
                                if (-1 == currentColor) Color.Blue else Color.Gray,
                            ),
                        ) {
                            if (-1 == currentColor) {
                                Icon(
                                    imageVector = Icons.Default.Done,
                                    contentDescription = "done",
                                    tint = Color.Blue,
                                    modifier = Modifier.padding(4.dp),
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Outlined.FormatColorReset,
                                    contentDescription = "done",
                                    tint = Color.Gray,
                                    modifier = Modifier.padding(4.dp),
                                )
                            }
                        }
                    }
                    itemsIndexed(NoteIcon.noteColors) { index, color ->
                        Surface(
                            onClick = { onColorClick(index) },
                            shape = CircleShape,
                            color = color,
                            modifier = Modifier.size(40.dp),
                            border = BorderStroke(
                                1.dp,
                                if (index == currentColor) Color.Blue else Color.Gray,
                            ),
                        ) {
                            if (index == currentColor) {
                                Icon(
                                    imageVector = Icons.Default.Done,
                                    contentDescription = "done",
                                    tint = Color.Blue,
                                    modifier = Modifier.padding(4.dp),
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.feature_detail_background),
                    style = MaterialTheme.typography.titleSmall,
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        Box(Modifier.clickable { onImageClick(-1) }) {
                            Icon(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .border(
                                        1.dp,
                                        if (-1 == currentImage) Color.Blue else Color.Gray,
                                        CircleShape,
                                    )
                                    .size(56.dp)
                                    .padding(8.dp),
                                imageVector = Icons.Outlined.ImageNotSupported,
                                contentDescription = "",
                            )
                            if (-1 == currentImage) {
                                Icon(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(Color.Blue)
                                        .size(16.dp)
                                        .align(Alignment.TopEnd),
                                    imageVector = Icons.Default.Done,
                                    contentDescription = "",
                                    tint = Color.White,

                                )
                            }
                        }
                    }
                    itemsIndexed(NoteIcon.background) { index, noteBg ->

                        Box(Modifier.clickable { onImageClick(index) }) {
                            Image(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .border(
                                        1.dp,
                                        if (index == currentImage) Color.Blue else Color.Gray,
                                        CircleShape,
                                    )
                                    .size(56.dp),
                                painter = painterResource(id = noteBg.bg),
                                contentDescription = "",
                                contentScale = ContentScale.Crop,
                            )
                            if (index == currentImage) {
                                Icon(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(Color.Blue)
                                        .size(16.dp)
                                        .align(Alignment.TopEnd),
                                    imageVector = Icons.Default.Done,
                                    contentDescription = "",
                                    tint = Color.White,

                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
