package com.mshdabiola.gallery


import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import java.io.File


@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun GalleryScreen(
    viewModel: GalleryViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    navigateToEditScreen: (Long, String, Long) -> Unit
) {
    val galleryUiState = viewModel.galleryUiState.collectAsStateWithLifecycle()
    GalleryScreen(
        galleryUiState = galleryUiState.value,
        onBack = onBack,
        onDelete = viewModel::deleteImage,
        onToText = {
            val id = galleryUiState.value.images[0].noteId
            navigateToEditScreen(id, "extract", it.toLong())
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun GalleryScreen(
    galleryUiState: GalleryUiState,
    onBack: () -> Unit = {},
    onDelete: (Long) -> Unit = {},
    onToText: (Int) -> Unit = {}
) {

    val pagerState = rememberPagerState()
//    var currIndex = remember(pagerState.currentPage) {
//        pa
//    }
    LaunchedEffect(key1 = galleryUiState.currentIndex, block = {
        if (galleryUiState.images.isNotEmpty()) {
            delay(1000)
            pagerState.animateScrollToPage(page = galleryUiState.currentIndex)
        }
    })
    val context = LocalContext.current
    val onSend = {
        val index = pagerState.currentPage
        val image = galleryUiState.images[index]

        val file = File(image.imageName)
        val uri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)
        val intent = ShareCompat.IntentBuilder(context)
            .setType("image/*")
            .setStream(uri)
            .setChooserTitle("NotePad")
            .createChooserIntent()

        context.startActivity(intent)
    }
    val onCopy = {
        val index = pagerState.currentPage
        val image = galleryUiState.images[index]
        val file = File(image.imageName)
        val uri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)

        val content = context.contentResolver
        val clip = ClipData.newUri(content, "image", uri)
        val c = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        c.setPrimaryClip(clip)
    }
    val delete = {
        val index = pagerState.currentPage
        val image = galleryUiState.images[index]
        onDelete(image.id)
    }

    Scaffold(
        topBar = {
            GalleryTopAppBar(
                onBack = onBack,
                onDelete = delete,
                onGrabText = { onToText(pagerState.currentPage) },
                name = "${pagerState.currentPage + 1} of ${galleryUiState.images.size}",
                onSend = onSend,
                onCopy = onCopy
            )
        }
    ) { paddingValues ->

        HorizontalPager(
            modifier = Modifier.padding(paddingValues),
            pageCount = galleryUiState.images.size,
            state = pagerState
        ) {
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
                val image = galleryUiState.images[it]
                /// currIndex=it
                AsyncImage(model = image.imageName, contentDescription = "")

            }
        }
    }
}

@Preview
@Composable
fun GalleryScreenPreview() {
    GalleryScreen(galleryUiState = GalleryUiState(), onDelete = {})

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryTopAppBar(
    name: String = "label",
    onBack: () -> Unit = {},
    onDelete: () -> Unit = {},
    onGrabText: () -> Unit = {},
    onSend: () -> Unit = {},
    onCopy: () -> Unit = {}

) {
    var showDropDown by remember {
        mutableStateOf(false)
    }



    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "back")
            }
        },
        title = { Text(text = name) },
        actions = {


            Box {
                IconButton(onClick = { showDropDown = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "more")

                }
                DropdownMenu(expanded = showDropDown, onDismissRequest = { showDropDown = false }) {
                    DropdownMenuItem(
                        text = { Text(text = "Grab Image Text") },
                        onClick = {
                            showDropDown = false
                            onGrabText()
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
                            onDelete()

                        })

                }

            }
        }

    )

}