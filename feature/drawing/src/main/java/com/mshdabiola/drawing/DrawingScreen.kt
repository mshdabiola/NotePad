package com.mshdabiola.drawing

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.mshdabiola.firebase.FirebaseScreenLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun DrawingScreen(
    viewModel: DrawingViewModel = hiltViewModel(),
    onBack: () -> Unit,
) {
    FirebaseScreenLog(screen = "drawing_screen")
    val context = LocalContext.current
    val lifecycleObserver= LocalLifecycleOwner.current
    val defaultLifecycleObserver= object : DefaultLifecycleObserver {
        override fun onPause(owner: LifecycleOwner) {
            super.onPause(owner)
            viewModel.saveData()
        }
    }

    DisposableEffect(key1 = Unit){
        lifecycleObserver.lifecycle.addObserver(defaultLifecycleObserver)
        onDispose {  lifecycleObserver.lifecycle.removeObserver(defaultLifecycleObserver)}
    }

//    LaunchedEffect(key1=viewModel.controller.completePathData.value, block = {
//        withContext(Dispatchers.IO){
//            viewModel.saveDrawing(viewModel.controller.completePathData.value)
//        }
//    })
    val res = context.resources.displayMetrics
//    LaunchedEffect(key1 = viewModel.controller.completePathData.value, block = {
//       withContext(Dispatchers.IO){
//           viewModel.saveImage(
//               viewModel.controller.getBitMap(
//                   res.widthPixels,
//                   res.heightPixels,
//                   res.density,
//               ),
//           )
//       }
//    })
    DrawingScreen(
        onBackk = onBack,
        filePath = viewModel.drawingUiState.filePath,
        controller = viewModel.controller,
        onDeleteImage = {
            viewModel.deleteImage()
            onBack()
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawingScreen(
    onBackk: () -> Unit = {},
    controller: DrawingController = rememberDrawingController(),
    filePath: String = "",
    onDeleteImage: () -> Unit = {},
) {
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

    Scaffold(

        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBackk) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "back",
                        )
                    }
                },
                title = {
                    Text("Drawing")
                },

                actions = {
                    IconButton(
                        enabled = controller.canUndo.value,
                        onClick = { controller.undo() },
                    ) {
                        Icon(imageVector = Icons.Default.Undo, contentDescription = "redo")
                    }
                    IconButton(
                        enabled = controller.canRedo.value,
                        onClick = { controller.redo() },
                    ) {
                        Icon(imageVector = Icons.Default.Redo, contentDescription = "redo")
                    }
                    Box {
                        IconButton(onClick = { showDropDown = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "more")
                        }
                        DropdownMenu(
                            expanded = showDropDown,
                            onDismissRequest = { showDropDown = false },
                        ) {
                            DropdownMenuItem(
                                text = { Text(text = "Grab Image Text") },
                                onClick = {
                                    showDropDown = false
                                    //  onGrabText()
                                },
                            )
                            DropdownMenuItem(
                                text = { Text(text = "Copy") },
                                onClick = {
                                    showDropDown = false
                                    onCopy()
                                },
                            )
                            DropdownMenuItem(
                                text = { Text(text = "Send") },
                                onClick = {
                                    showDropDown = false
                                    onSend()
                                },
                            )
                            DropdownMenuItem(
                                text = { Text(text = "Delete") },
                                onClick = {
                                    showDropDown = false
                                    onDeleteImage()
                                },
                            )
                        }
                    }
                },
            )
        },
        bottomBar = {
            DrawingBar(
                modifier = Modifier
                    .padding(horizontal = 8.dp),
                controller = controller,
            )
        },
    ) { paddingValues: PaddingValues ->
        Box(Modifier.padding(paddingValues)) {
            Board(
                modifier = Modifier.fillMaxSize(),
                drawingController = controller,
            )
        }
    }
}

@Preview
@Composable
fun DrawingScreenPreview() {
    DrawingScreen()
}
