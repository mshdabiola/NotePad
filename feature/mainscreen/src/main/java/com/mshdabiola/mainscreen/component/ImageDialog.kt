package com.mshdabiola.mainscreen.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mshdabiola.designsystem.icon.NoteIcon

@Composable
fun ImageDialog(
    show: Boolean = false,
    onDismissRequest: () -> Unit = {},
    onChooseImage: () -> Unit = {},
    onSnapImage: () -> Unit = {},

    ) {
    AnimatedVisibility(visible = show) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text(text = "Add Image") },
            text = {
                Column {
                    Row(
                        modifier = Modifier
                            .clickable { onSnapImage() }
                            .fillMaxWidth()
                            .padding(16.dp),

                        verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector =
                            ImageVector.vectorResource(id = NoteIcon.Photo),
                            contentDescription = "take image"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Take Image")
                    }
                    Row(
                        modifier = Modifier
                            .clickable { onChooseImage() }
                            .fillMaxWidth()
                            .padding(16.dp),

                        verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector =
                            ImageVector.vectorResource(id = NoteIcon.Image),
                            contentDescription = "take phone"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Choose Image")
                    }


                }
            },
            confirmButton = {}
        )
    }

}

@Preview
@Composable
fun ImageDialogPreview() {
    ImageDialog(true)
}