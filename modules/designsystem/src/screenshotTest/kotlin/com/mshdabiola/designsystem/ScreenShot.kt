package com.mshdabiola.designsystem

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.mshdabiola.designsystem.component.SkTopAppBar
import com.mshdabiola.designsystem.icon.SkIcons

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun TopAppBarScreenShot() {
    SkTopAppBar(
        titleRes = android.R.string.untitled,
        navigationIcon = SkIcons.Search,
        navigationIconContentDescription = "Navigation icon",
        actionIcon = SkIcons.MoreVert,
        actionIconContentDescription = "Action icon",
    )
}