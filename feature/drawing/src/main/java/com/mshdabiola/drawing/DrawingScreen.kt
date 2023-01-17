package com.mshdabiola.drawing


import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File


@Composable
fun DrawingScreen(
    viewModel: DrawingViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    DrawingScreen(
        onBackk = onBack,
        filePath = viewModel.drawingUiState.filePath,
        paths = viewModel.drawingUiState.paths,
        saveImage = viewModel::saveImage,
        onDeleteImage = {
            viewModel.deleteImage()
            onBack()
        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawingScreen(
    onBackk: () -> Unit = {},
    paths: ImmutableMap<PathData, List<Offset>> = emptyMap<PathData, List<Offset>>()
        .toImmutableMap(),
    saveImage: (Bitmap, Map<PathData, List<Offset>>) -> Unit = { _, _ -> },
    filePath: String = "",
    onDeleteImage: () -> Unit = {}
) {
    val controller = rememberDrawingController()
    var showDropDown by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    val onSend = {


        val file = File(filePath)
        val uri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)
        val intent = ShareCompat.IntentBuilder(context)
            .setType("image/*")
            .setStream(uri)
            .setChooserTitle("NotePad")
            .createChooserIntent()

        context.startActivity(intent)
    }
    val onCopy = {

        val file = File(filePath)
        val uri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)

        val content = context.contentResolver
        val clip = ClipData.newUri(content, "image", uri)
        val c = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        c.setPrimaryClip(clip)
    }

    LaunchedEffect(key1 = paths, block = {
        controller.setPathData(paths)
    })

    LaunchedEffect(key1 = controller.listOfPathData, block = {
        withContext(Dispatchers.IO) {
            saveImage(controller.getBitMap(), controller.listOfPathData.paths2)
        }

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
                    Box {
                        IconButton(onClick = { showDropDown = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "more")

                        }
                        DropdownMenu(
                            expanded = showDropDown,
                            onDismissRequest = { showDropDown = false }) {
                            DropdownMenuItem(
                                text = { Text(text = "Grab Image Text") },
                                onClick = {
                                    showDropDown = false
                                    //  onGrabText()
                                })
                            DropdownMenuItem(
                                text = { Text(text = "Copy") },
                                onClick = {
                                    showDropDown = false
                                    onCopy()

                                })
                            DropdownMenuItem(
                                text = { Text(text = "Send") },
                                onClick = {
                                    showDropDown = false
                                    onSend()

                                })
                            DropdownMenuItem(
                                text = { Text(text = "Delete") },
                                onClick = {
                                    showDropDown = false
                                    onDeleteImage()

                                })

                        }

                    }
                }
            )
        },
        bottomBar = {
            DrawingBar(
                modifier = Modifier
//                    .navigationBarsPadding()
//                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 8.dp),
                controller = controller
            )
        }
    ) { paddingValues: PaddingValues ->
        Box(Modifier.padding(paddingValues)) {
            Board(
                modifier = Modifier.fillMaxSize(),
                drawingController = controller
            )
//            DrawingBar(
//                modifier = Modifier
//                    .align(Alignment.BottomCenter)
//                    .padding(horizontal = 8.dp),
//                controller = controller
//            )
        }
    }

}

@Preview
@Composable
fun DrawingScreenPreview() {
    DrawingScreen()

}