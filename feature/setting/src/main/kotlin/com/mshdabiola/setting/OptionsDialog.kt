package com.mshdabiola.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import com.mshdabiola.designsystem.R as Rd

@Composable
fun OptionsDialog(
    modifier: Modifier = Modifier,
    current: Int,
    options: List<String>,
    onSelect: (Int) -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Rd.string.modules_designsystem_close))
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                options.fastForEachIndexed { i, s ->
                    Row(modifier = Modifier.clickable { onSelect(i) }, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        RadioButton(selected = i == current, onClick = { onSelect(i) })
                        Text(modifier = Modifier.weight(1f), text = s)
                    }
                }
            }
        },
    )
}
