package com.mshdabiola.gallery.navigation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import com.mshdabiola.gallery.GalleryScreen
import com.mshdabiola.gallery.GalleryViewModel
import com.mshdabiola.ui.FirebaseScreenLog
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalSharedTransitionApi::class)
fun EntryProviderBuilder<NavKey>.gallery(
    onBack: () -> Unit,
) {
    entry<GalleryArg> { key ->
        val coroutineScope = rememberCoroutineScope()
        FirebaseScreenLog(screen = "gallery_screen")
        val viewModel = hiltViewModel<GalleryViewModel, GalleryViewModel.Factory>(
            creationCallback = { factory -> factory.create(key) },
        )

        val galleryUiState = viewModel.galleryUiState.collectAsStateWithLifecycle()
        val pagerState = rememberPagerState(galleryUiState.value.initIndex) {
            galleryUiState.value.images.size
        }

//        LaunchedEffect(galleryUiState.value.initIndex) {
//            pagerState.scrollToPage(galleryUiState.value.initIndex)
//        }

        val context = LocalContext.current
        val onSend = {
            val index = pagerState.currentPage
            val image = galleryUiState.value.images[index]

            val file = File(image.path)
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
            val image = galleryUiState.value.images[index]
            val file = File(image.path)
            val uri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)

            val content = context.contentResolver
            val clip = ClipData.newUri(content, "image", uri)
            val c = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            c.setPrimaryClip(clip)
        }
        val delete = {
            val index = pagerState.currentPage
            val image = galleryUiState.value.images[index]
            viewModel.deleteImage(image.id)
        }
        GalleryScreen(
            pagerState = pagerState,
            galleryUiState = galleryUiState.value,
            onBack = onBack,
            onToText = {
                coroutineScope.launch {
                    viewModel.onImage(it)
                    onBack()
                }
            },
            onSend = onSend,
            onCopy = onCopy,
            delete = delete,
        )
    }
}

fun NavBackStack.navigateToGallery(galleryArg: GalleryArg) {
    add(galleryArg)
}
