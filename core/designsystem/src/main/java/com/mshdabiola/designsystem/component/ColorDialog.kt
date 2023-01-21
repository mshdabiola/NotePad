package com.mshdabiola.designsystem.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.FormatColorReset
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mshdabiola.designsystem.icon.NoteIcon


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorDialog(
    show: Boolean = false,
    currentColor: Int = -1,
    onDismissRequest: () -> Unit = {},
    onColorClick: (Int) -> Unit = {}
) {
    AnimatedVisibility(visible = show) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(text = "Note Color")
            },
            text = {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(40.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Surface(
                            onClick = {
                                onDismissRequest()
                                onColorClick(-1)
                            },
                            shape = CircleShape,
                            color = Color.White,
                            modifier = Modifier
                                .width(40.dp)
                                .aspectRatio(1f),
                            border = BorderStroke(
                                1.dp,
                                if (-1 == currentColor) Color.Blue else Color.Gray
                            )
                        ) {
                            if (-1 == currentColor) {
                                Icon(
                                    imageVector = Icons.Default.Done,
                                    contentDescription = "done",
                                    tint = Color.Blue,
                                    modifier = Modifier.padding(4.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Outlined.FormatColorReset,
                                    contentDescription = "done",
                                    tint = Color.Gray,
                                    modifier = Modifier.padding(4.dp)
                                )
                            }

                        }
                    }

                    itemsIndexed(NoteIcon.noteColors) { index, color ->
                        Surface(
                            onClick = {
                                onDismissRequest()
                                onColorClick(index)
                            },
                            shape = CircleShape,
                            color = color,
                            modifier = Modifier
                                .width(40.dp)
                                .aspectRatio(1f),
                            border = BorderStroke(
                                1.dp,
                                if (index == currentColor) Color.Blue else Color.Gray
                            )
                        ) {
                            if (index == currentColor) {
                                Icon(
                                    imageVector = Icons.Default.Done,
                                    contentDescription = "done",
                                    tint = Color.Blue,
                                    modifier = Modifier.padding(4.dp)
                                )
                            }

                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

}

@Preview
@Composable
fun ColorDialogPreview() {
    ColorDialog(
        show = true
    )
}

