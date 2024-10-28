package com.mshdabiola.designsystem

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.mshdabiola.designsystem.component.NoteTopAppBar
import com.mshdabiola.designsystem.icon.NoteIcon

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun TopAppBarScreenShot() {
    NoteTopAppBar(
        titleRes = stringResource(android.R.string.untitled),
        navigationIcon = NoteIcon.Search,
        navigationIconContentDescription = "Navigation icon",
        actionIcon = NoteIcon.MoreVert,
        actionIconContentDescription = "Action icon",
    )
}
