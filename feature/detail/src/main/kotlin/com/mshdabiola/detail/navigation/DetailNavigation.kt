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
import androidx.compose.animation.SharedTransitionScope
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
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.mshdabiola.detail.AddBottomSheet2
import com.mshdabiola.detail.ColorAndImageBottomSheet
import com.mshdabiola.detail.DetailViewModel
import com.mshdabiola.detail.EditScreen
import com.mshdabiola.detail.NoteOptionBottomSheet
import com.mshdabiola.detail.NotificationBottomSheet
import com.mshdabiola.model.NoteVisual
import com.mshdabiola.ui.FirebaseScreenLog
import com.mshdabiola.ui.NotificationDialogNew
import com.mshdabiola.ui.supportVoice
import java.io.File

fun NavController.navigateToDetail(
    detailArg: DetailArg,

    navOptions: NavOptions = androidx.navigation.navOptions { },
) = navigate(detailArg, navOptions)

@OptIn(ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.detailScreen(
    onBack: () -> Unit,
    navigateToGallery: (Long, Int, Int, String) -> Unit,
    navigateToDrawing: (Long, Long) -> Unit,
    navigateToSelectLevel: (Set<Long>) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    modifier: Modifier.Companion,
) {
    composable<DetailArg> {
        val id = it.savedStateHandle.toRoute<DetailArg>().id

        val editViewModel: DetailViewModel = hiltViewModel()
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

        sharedTransitionScope.EditScreen(
            modifier = modifier,
            id = id,
            notepad = detailState.notePad,
            title = detailState.title,
            content = detailState.detail,
            onBackClick = onBack,
            onCheckChange = editViewModel::onCheckChange,
            onCheckDelete = editViewModel::onCheckDelete,
            onCheck = editViewModel::onCheck,
            addItem = editViewModel::addCheck,
            playVoice = editViewModel::playMusic,
            pauseVoice = editViewModel::pause,
            moreOptions = {
                showModalState = true
            },
            noteOption = { noteModalState = true },
            unCheckAllItems = editViewModel::unCheckAllItems,
            deleteCheckItems = editViewModel::deleteCheckedItems,
            hideCheckBoxes = editViewModel::hideCheckBoxes,
            pinNote = editViewModel::pinNote,
            onLabel = {
                navigateToSelectLevel(
                    setOf(
                        detailState.notePad.id,
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
            navigateToDrawing = { navigateToDrawing(detailState.notePad.id, it) },
            animatedContentScope = this,

        )
        AddBottomSheet2(
            show = showModalState,
            currentColor = detailState.notePad.color,
            currentImage = detailState.notePad.background,
            isNoteCheck = detailState.notePad.isCheck,
            saveImage = editViewModel::saveImage,
            saveVoice = editViewModel::saveVoice,
            getPhotoUri = editViewModel::getPhotoUri,
            changeToCheckBoxes = editViewModel::changeToCheckBoxes,
            onDrawing = {
                val id = editViewModel.insertNewDrawing()
                navigateToDrawing(detailState.notePad.id, id)
            },
            onDismiss = { showModalState = false },
            isVoiceSupport = supportVoice(),
        )
//
        val images = detailState.notePad.visuals
            .filterIsInstance<NoteVisual.NoteImage>().map {
                val file = File(it.path)
                val uri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)
                uri
            }

        val send = {
            val intent = ShareCompat.IntentBuilder(context)
                .setText(detailState.notePad.title)
                .setSubject(detailState.notePad.detail)
                .setChooserTitle("From Notepad")

            if (images.isNotEmpty()) intent.setType("image/*") else intent.setType("text/*")
            images.forEach {
                intent.setStream(it)
            }

            context.startActivity(Intent(intent.createChooserIntent()))
        }
        NoteOptionBottomSheet(
            show = noteModalState,
            currentColor = detailState.notePad.color,
            currentImage = detailState.notePad.background,
            onLabel = {
                navigateToSelectLevel(
                    setOf(
                        detailState.notePad.id,
                    ),
                )
            },
            onDelete = editViewModel::onDelete,
            onCopy = editViewModel::copyNote,
            onSendNote = send,
            onDismissRequest = { noteModalState = false },
        )
        ColorAndImageBottomSheet(
            show = colorModalState,
            currentColor = detailState.notePad.color,
            currentImage = detailState.notePad.background,
            onColorClick = editViewModel::onColorChange,
            onImageClick = editViewModel::onImageChange,
            onDismissRequest = { colorModalState = false },
        )
//
        NotificationBottomSheet(
            show = noteficationModalState,
            onAlarm = editViewModel::setAlarm,
            showDialog = { showDialog = true },
            currentColor = detailState.notePad.color,
            currentImage = detailState.notePad.background,

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
