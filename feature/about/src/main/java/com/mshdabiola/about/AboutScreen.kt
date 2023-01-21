package com.mshdabiola.about


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    var lastUpdate by remember {
        mutableStateOf("")
    }
    var version by remember {
        mutableStateOf("")
    }
    LaunchedEffect(key1 = Unit, block = {
        val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        val datetime = Instant.fromEpochMilliseconds(pInfo.lastUpdateTime)
            .toLocalDateTime(TimeZone.currentSystemDefault()).date
        lastUpdate = "${datetime.dayOfMonth} ${
            datetime.month.name.lowercase().replaceFirstChar { it.uppercaseChar() }
        } ${datetime.year}"
        version = pInfo.versionName
    })


    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "back")
                    }
                },
                title = {
                    Text(text = "About")
                }
            )
        }
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)) {
            Column(Modifier.padding(start = 24.dp, end = 24.dp, top = 200.dp)) {
                Text(
                    text = "Play NotePad",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(Modifier.height(8.dp))
                Divider(
                    modifier = Modifier.width(64.dp),
                    thickness = 4.dp,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Version",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(text = version, style = MaterialTheme.typography.bodyLarge)

                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Last Update",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(text = lastUpdate, style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(16.dp))
                Text(text = "This is app is an open source project. develop by Mshdabiola")
                Spacer(Modifier.height(16.dp))
                Text(text = "Terms and Condition")
            }

        }
    }
}

@Preview
@Composable
fun AboutScreenPreview() {
    AboutScreen()

}