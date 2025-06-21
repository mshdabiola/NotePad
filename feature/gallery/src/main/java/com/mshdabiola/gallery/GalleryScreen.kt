package com.mshdabiola.gallery

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.mshdabiola.designsystem.icon.NoteIcon
import com.mshdabiola.model.NoteImage
import me.saket.telephoto.zoomable.coil.ZoomableAsyncImage
import com.mshdabiola.designsystem.R as Rd

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.GalleryScreen(
    galleryUiState: GalleryUiState,
    pagerState: PagerState = rememberPagerState() { 2 },
    animatedContentScope: AnimatedVisibilityScope,
    onBack: () -> Unit = {},
    onToText: (String) -> Unit = {},
    onSend: () -> Unit = {},
    onCopy: () -> Unit = {},
    delete: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            GalleryTopAppBar(
                onBack = onBack,
                onDelete = delete,
                onGrabText = { onToText(galleryUiState.images[pagerState.currentPage].path) },
                name = "${pagerState.currentPage + 1} of ${galleryUiState.images.size}",
                onSend = onSend,
                onCopy = onCopy,
            )
        },
    ) { paddingValues ->

        HorizontalPager(
            modifier = Modifier.padding(paddingValues),
            state = pagerState,
        ) {
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
                val image = galleryUiState.images.getOrNull(it)
                // / currIndex=it
                if (image != null) {
                    ZoomableAsyncImage(
                        modifier = Modifier
                            .sharedElement(
                                sharedContentState = rememberSharedContentState("image_${image.id}"),
                                animatedVisibilityScope = animatedContentScope,
                            )
                            .fillMaxSize(),
                        model = image.path,
                        contentDescription = "",
                        alignment = Alignment.Center,

                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@SuppressLint("UnusedSharedTransitionModifierParameter")
@Preview
@Composable
fun GalleryScreenPreview() {
    SharedTransitionScope {
        AnimatedVisibility(true) {
            GalleryScreen(
                animatedContentScope = this,
                galleryUiState = GalleryUiState(
                    images = listOf(
                        NoteImage(id = 1),
                        NoteImage(id = 1),
                    ),

                ),
                pagerState = rememberPagerState(1) { 2 },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryTopAppBar(
    name: String = "label",
    onBack: () -> Unit = {},
    onDelete: () -> Unit = {},
    onGrabText: () -> Unit = {},
    onSend: () -> Unit = {},
    onCopy: () -> Unit = {},

) {
    var showDropDown by remember {
        mutableStateOf(false)
    }

    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(imageVector = NoteIcon.ArrowBack, contentDescription = "back")
            }
        },
        title = { Text(text = name) },
        actions = {
            Box {
                IconButton(onClick = { showDropDown = true }) {
                    Icon(NoteIcon.MoreVert, contentDescription = "more")
                }
                DropdownMenu(expanded = showDropDown, onDismissRequest = { showDropDown = false }) {
                    DropdownMenuItem(
                        text = { Text(text = stringResource(Rd.string.modules_designsystem_grab_image_text)) },
                        onClick = {
                            showDropDown = false
                            onGrabText()
                        },
                    )
                    DropdownMenuItem(
                        text = { Text(text = stringResource(Rd.string.modules_designsystem_copy)) },
                        onClick = {
                            showDropDown = false
                            onCopy()
                        },
                    )
                    DropdownMenuItem(
                        text = { Text(text = stringResource(Rd.string.modules_designsystem_send)) },
                        onClick = {
                            showDropDown = false
                            onSend()
                        },
                    )
                    DropdownMenuItem(
                        text = { Text(text = stringResource(Rd.string.modules_designsystem_delete)) },
                        onClick = {
                            showDropDown = false
                            onDelete()
                        },
                    )
                }
            }
        },

    )
}
