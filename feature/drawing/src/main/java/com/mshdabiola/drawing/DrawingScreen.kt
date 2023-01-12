package com.mshdabiola.drawing


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.collections.immutable.ImmutableList


@Composable
fun DrawingScreen(
    viewModel: DrawingViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    DrawingScreen(
        onBack = onBack,
        paths = viewModel.drawingUiState.paths,
        onpathChange = viewModel::onPointChange
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawingScreen(
    onBack: () -> Unit = {},
    paths: ImmutableList<PathData>,
    onpathChange: (Offset, MODE) -> Unit = { _, _ -> }
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
        val controller = rememberDrawingController()
        Column(Modifier.padding(paddingValues)) {
            Board(
                modifier = Modifier.fillMaxSize(),
                paths = paths,
                drawingController = controller,
                onPointChange = onpathChange
            )
        }
    }

}

@Preview
@Composable
fun DrawingScreenPreview() {
    // DrawingScreen()

}