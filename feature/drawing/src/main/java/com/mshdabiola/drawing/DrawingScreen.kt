package com.mshdabiola.drawing


import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel


@Composable
fun DrawingScreen(
    viewModel: DrawingViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    DrawingScreen(
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawingScreen(
    onBack: () -> Unit = {}
) {

    Scaffold(

        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "back"
                        )
                    }
                },
                title = {
                    Text("Drawing")
                },

                actions = {}
            )
        }
    ) { paddingValues: PaddingValues ->
        DrawingApp(paddingValues)
    }

}

@Preview
@Composable
fun DrawingScreenPreview() {
    DrawingScreen()

}