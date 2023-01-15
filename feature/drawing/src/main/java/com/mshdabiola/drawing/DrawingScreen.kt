package com.mshdabiola.drawing


import android.graphics.Bitmap
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableMap


@Composable
fun DrawingScreen(
    viewModel: DrawingViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    DrawingScreen(
        onBackk = onBack,
        saveImage = viewModel::saveImage
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawingScreen(
    onBackk: () -> Unit = {},
    paths: ImmutableMap<PathData, List<Offset>> = emptyMap<PathData, List<Offset>>().toImmutableMap(),
    saveImage: (Bitmap) -> Unit = {}
) {
    val controller = rememberDrawingController()

    LaunchedEffect(key1 = paths, block = {
        controller.setPathData(paths)
    })

    LaunchedEffect(key1 = controller.listOfPathData, block = {
        saveImage(controller.getBitMap())
    })

    Scaffold(

        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBackk) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "back"
                        )
                    }
                },
                title = {
                    Text("Drawing")
                },

                actions = {
                    IconButton(
                        enabled = controller.canUndo.value,
                        onClick = { controller.undo() }) {
                        Icon(imageVector = Icons.Default.Undo, contentDescription = "redo")
                    }
                    IconButton(
                        enabled = controller.canRedo.value,
                        onClick = { controller.redo() }) {
                        Icon(imageVector = Icons.Default.Redo, contentDescription = "redo")
                    }
                }
            )
        }
    ) { paddingValues: PaddingValues ->
        Box(Modifier.padding(paddingValues)) {
            Board(
                modifier = Modifier.fillMaxSize(),
                drawingController = controller
            )
            DrawingBar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 8.dp),
                controller = controller
            )
        }
    }

}

@Preview
@Composable
fun DrawingScreenPreview() {
    DrawingScreen()

}