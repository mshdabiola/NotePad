package com.mshdabiola.gallery


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import kotlinx.coroutines.delay


@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun GalleryScreen(viewModel: GalleryViewModel = hiltViewModel(), onBack: () -> Unit = {}) {
    val galleryUiState = viewModel.galleryUiState.collectAsStateWithLifecycle()
    GalleryScreen(
        galleryUiState = galleryUiState.value,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun GalleryScreen(
    galleryUiState: GalleryUiState,
    onBack: () -> Unit = {}
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

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "back")
                    }
                },
                title = {
                    Text(text = "${pagerState.currentPage + 1} of ${galleryUiState.images.size}")
                }
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
    GalleryScreen()

}