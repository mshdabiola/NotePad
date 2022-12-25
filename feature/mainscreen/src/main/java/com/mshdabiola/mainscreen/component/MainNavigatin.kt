package com.mshdabiola.mainscreen.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mshdabiola.designsystem.icon.NoteIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation() {
    Surface(modifier = Modifier.fillMaxWidth(4 / 5f)) {

        Column(
            modifier = Modifier
                .verticalScroll(state = rememberScrollState())
                .padding(start = 8.dp)
                .safeDrawingPadding()
        )
        {

            Text(text = "Note Pad", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))
            NavigationDrawerItem(
                icon = {
                    Icon(painter = painterResource(id = NoteIcon.Archive), contentDescription = "")
                },
                label = { Text(text = "Notes") },
                selected = false,
                onClick = { })
            NavigationDrawerItem(
                icon = {
                    Icon(painter = painterResource(id = NoteIcon.Archive), contentDescription = "")
                },
                label = { Text(text = "Reminders") },
                selected = false,
                onClick = { })
            Divider(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)) {
                Text(modifier = Modifier.weight(1f), text = "Labels")
                Text(text = "Edit", color = MaterialTheme.colorScheme.primary)
            }

            repeat(5) {
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = NoteIcon.Label),
                            contentDescription = ""
                        )
                    },
                    label = { Text(text = "Labels") },
                    selected = false,
                    onClick = { })
            }
            NavigationDrawerItem(
                icon = {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "")
                },
                label = { Text(text = "Create new label") },
                selected = false,
                onClick = { })
            Divider(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            NavigationDrawerItem(
                icon = {
                    Icon(painterResource(id = NoteIcon.Archive), contentDescription = "")
                },
                label = { Text(text = "Archive") },
                selected = false,
                onClick = { })

            NavigationDrawerItem(
                icon = {
                    Icon(Icons.Outlined.Delete, contentDescription = "")
                },
                label = { Text(text = "Trash") },
                selected = false,
                onClick = { })
            NavigationDrawerItem(
                icon = {
                    Icon(Icons.Outlined.Settings, contentDescription = "")
                },
                label = { Text(text = "Setting") },
                selected = false,
                onClick = { })
            NavigationDrawerItem(
                icon = {
                    Icon(Icons.Outlined.Info, contentDescription = "")
                },
                label = { Text(text = "Help & feedback") },
                selected = false,
                onClick = { })


        }
    }

}

@Preview
@Composable
fun MainNavigationPreview() {

    Column(Modifier.fillMaxSize()) {
        MainNavigation()
    }

}