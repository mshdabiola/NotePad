/*
 *abiola 2022
 */

package com.mshdabiola.detail.navigation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import com.mshdabiola.detail.AddBottomSheet2
import com.mshdabiola.detail.ColorAndImageBottomSheet
import com.mshdabiola.detail.DetailViewModel
import com.mshdabiola.detail.EditScreen
import com.mshdabiola.detail.NoteOptionBottomSheet
import com.mshdabiola.detail.NotificationBottomSheet
import com.mshdabiola.ui.FirebaseScreenLog
import com.mshdabiola.ui.NotificationDialogNew
import com.mshdabiola.ui.supportVoice
import java.io.File

fun NavBackStack.navigateToDetail(detailArg: DetailArg) {
    add(detailArg)
}

@OptIn(ExperimentalSharedTransitionApi::class)
fun EntryProviderBuilder<NavKey>.detailScreen(
    onBack: () -> Unit,
    navigateToGallery: (Long, Int, Int, String) -> Unit,
    navigateToDrawing: (Long, Long?) -> Unit,
    navigateToSelectLevel: (Set<Long>) -> Unit,
    modifier: Modifier,
) {
    entry<DetailArg> { key ->
        val editViewModel = hiltViewModel<DetailViewModel, DetailViewModel.Factory>(
            creationCallback = { factory -> factory.create(key) },
        )
        val detailState by editViewModel.detailState.collectAsStateWithLifecycle()
        var showModalState by remember {
            mutableStateOf(false)
        }
        var noteModalState by remember {
            mutableStateOf(false)
        }
        var noteficationModalState by remember {
            mutableStateOf(false)
        }
        var colorModalState by remember {
            mutableStateOf(false)
        }

        var showDialog by remember {
            mutableStateOf(false)
        }
        val notificationPermission = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = {
                if (it) {
                    noteficationModalState = true
                }
            },
        )
        val context = LocalContext.current

        FirebaseScreenLog(screen = "edit_screen")

        EditScreen(
            modifier = modifier,
            state = detailState,
            onBackClick = onBack,
            onCheckDelete = editViewModel::onCheckDelete,
//            onCheck = editViewModel::onCheck,
            addItem = editViewModel::addCheck,
            playVoice = editViewModel::playMusic,
            pauseVoice = editViewModel::pause,
            moreOptions = {
                showModalState = true
            },
            noteOption = { noteModalState = true },
//            unCheckAllItems = editViewModel::unCheckAllItems,
            deleteCheckItems = editViewModel::deleteCheckedItems,
            hideCheckBoxes = editViewModel::hideCheckBoxes,
            pinNote = editViewModel::pinNote,
            onLabel = {
                navigateToSelectLevel(
                    setOf(
                        detailState.notePad.note.id,
                    ),
                )
            },
            onColorClick = { colorModalState = true },
            onNotification = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED
                ) {
                    notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    noteficationModalState = true
                }
            },
            showNotificationDialog = {
                showDialog = true
            },
            onArchive = editViewModel::onArchive,
            deleteVoiceNote = editViewModel::deleteVoiceNote,
            navigateToGallery = navigateToGallery,
            navigateToDrawing = { navigateToDrawing(detailState.notePad.note.id, it) },

        )
        AddBottomSheet2(
            show = showModalState,
            currentColor = detailState.notePad.note.color,
            currentImage = detailState.notePad.note.background,
            isNoteCheck = detailState.notePad.note.isCheck,
            saveImage = editViewModel::saveImage,
            saveVoice = editViewModel::saveVoice,
            getPhotoUri = editViewModel::getPhotoUri,
            changeToCheckBoxes = editViewModel::changeToCheckBoxes,
            onDrawing = {
                navigateToDrawing(detailState.notePad.note.id, null)
            },
            onDismiss = { showModalState = false },
            isVoiceSupport = supportVoice(),
        )
//
        val images = detailState.notePad.images
            .map {
                val file = File(it.path)
                val uri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)
                uri
            }

        val send = {
            val intent = ShareCompat.IntentBuilder(context)
                .setText(detailState.notePad.note.title)
                .setSubject(detailState.notePad.note.detail)
                .setChooserTitle("From Notepad")

            if (images.isNotEmpty()) intent.setType("image/*") else intent.setType("text/*")
            images.forEach {
                intent.setStream(it)
            }

            context.startActivity(Intent(intent.createChooserIntent()))
        }
        NoteOptionBottomSheet(
            show = noteModalState,
            currentColor = detailState.notePad.note.color,
            currentImage = detailState.notePad.note.background,
            onLabel = {
                navigateToSelectLevel(
                    setOf(
                        detailState.notePad.note.id,
                    ),
                )
            },
            onDelete = editViewModel::onTrash,
            onCopy = editViewModel::copyNote,
            onSendNote = send,
            onDismissRequest = { noteModalState = false },
        )
        ColorAndImageBottomSheet(
            show = colorModalState,
            currentColor = detailState.notePad.note.color,
            currentImage = detailState.notePad.note.background,
            onColorClick = editViewModel::onColorChange,
            onImageClick = editViewModel::onImageChange,
            onDismissRequest = { colorModalState = false },
        )
//
        NotificationBottomSheet(
            show = noteficationModalState,
            onAlarm = editViewModel::setAlarm,
            showDialog = { showDialog = true },
            currentColor = detailState.notePad.note.color,
            currentImage = detailState.notePad.note.background,

        ) { noteficationModalState = false }
//
        NotificationDialogNew(
            initState = editViewModel.notificationUiState,
            showDialog = showDialog,
            onDismissRequest = { showDialog = false },
            isEdit = false,
            onSetAlarm = {},
            onDeleteAlarm = { },
        )
    }
}
