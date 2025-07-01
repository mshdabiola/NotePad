package com.mshdabiola.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mshdabiola.designsystem.icon.NoteIcon
import com.mshdabiola.designsystem.R as Rd

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBack: () -> Unit = {},
    lastUpdate: String = "",
    version: String = "",
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        modifier = Modifier.testTag("about:back"),
                        onClick = onBack,
                    ) {
                        Icon(imageVector = NoteIcon.ArrowBack, contentDescription = "back")
                    }
                },
                title = {
                    Text(text = stringResource(Rd.string.modules_designsystem_about))
                },
            )
        },
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            Column(Modifier.padding(start = 24.dp, end = 24.dp, top = 200.dp)) {
                Text(
                    text = stringResource(Rd.string.modules_designsystem_play_notepad),
                    style = MaterialTheme.typography.headlineMedium,
                )
                Spacer(Modifier.height(8.dp))
                HorizontalDivider(
                    modifier = Modifier.width(64.dp),
                    thickness = 4.dp,
                    color = MaterialTheme.colorScheme.primary,
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = stringResource(Rd.string.modules_designsystem_version),
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall,
                )
                Text(text = version, style = MaterialTheme.typography.bodyLarge)

                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(Rd.string.modules_designsystem_last_update),
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall,
                )
                Text(text = lastUpdate, style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(16.dp))
                Text(text = stringResource(Rd.string.modules_designsystem_about_me))
                Spacer(Modifier.height(16.dp))
                Text(text = stringResource(Rd.string.modules_designsystem_terms_and_condition))
            }
        }
    }
}
