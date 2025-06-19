/*
 *abiola 2022
 */

package com.mshdabiola.main.navigation

import android.content.Intent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ShareCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.mshdabiola.main.DeleteLabelAlertDialog
import com.mshdabiola.main.MainScreen
import com.mshdabiola.main.MainState
import com.mshdabiola.main.MainViewModel
import com.mshdabiola.main.RenameLabelAlertDialog
import com.mshdabiola.ui.ColorDialog
import com.mshdabiola.ui.NotificationDialogNew
import kotlinx.coroutines.delay

fun NavController.navigateToMain(
    navOptions: NavOptions = androidx.navigation.navOptions { },
) = navigate(route = MainRoute, navOptions)

const val MainRoute = "main"
const val FullMainRoute = MainRoute

@OptIn(ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.mainScreen(
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope,
    onShowSnack: suspend (String, String?) -> Boolean,
    navigateToDetail: (Long) -> Unit,
    navigateToSelectLevel: (Set<Long>) -> Unit,
    onOpenDrawer: () -> Unit,
    navigateToSearch: () -> Unit,
) {
    composable(
        route = FullMainRoute,
    ) {
        val mainViewModel: MainViewModel = hiltViewModel()
        val mainState = mainViewModel.mainState.collectAsStateWithLifecycle()

        LaunchedEffect(
            key1 = Unit,
            block = {
                delay(2000)
                mainViewModel.deleteEmptyNote()
            },
        )

        var showDialog by remember {
            mutableStateOf(false)
        }
        var showColor by remember {
            mutableStateOf(false)
        }
        var showRenameLabel by remember {
            mutableStateOf(false)
        }
        var showDeleteLabel by remember {
            mutableStateOf(false)
        }
        val context = LocalContext.current

        MainScreen(
            modifier = modifier,
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = this,
            mainState = mainState.value,
            navigateToNoteEditor = navigateToDetail,
            onNoteSelected = mainViewModel::handleCardSelection,
            onClearSelection = mainViewModel::onClearSelection,
            onPinNotes = mainViewModel::pinOrUnpinNotes,
            onNotificationClick = { showDialog = true },
            onSelectColor = { showColor = true },
            onLabelNotes = {
                (mainState.value as MainState.Success).selectState?.setOfSelected?.let {
                    navigateToSelectLevel(it)
                }
            },
            onCopyNote = mainViewModel::onCopyNote,
            onDeleteNotes = mainViewModel::onDeleteNote,
            onArchive = mainViewModel::onArchiveNote,
            onShareNote = {
                val notePads = mainViewModel.onSendNote()
                val intent = ShareCompat.IntentBuilder(context)
                    .setText(notePads.toString())
                    .setType("text/*")
                    .setChooserTitle("From Notepad")
                    .createChooserIntent()
                context.startActivity(Intent(intent))
            },
            onLabelNameChange = { showRenameLabel = true },
            onDeleteLabel = { showDeleteLabel = true },
            onDeleteAllTrash = mainViewModel::onDeleteAllTrash,
            onHamburgerMenuClick = onOpenDrawer,
            onSearchClick = navigateToSearch,
            onDisplayModeChange = mainViewModel::onDisplayModeChange,
        )

        NotificationDialogNew(
            showDialog = showDialog,
            onDismissRequest = { showDialog = false },
            isEdit = false,
            initState = (mainState.value as? MainState.Success)?.selectState?.notificationUiState,
            onSetAlarm = mainViewModel::setAlarm,
            onDeleteAlarm = mainViewModel::onDeleteAlarm,
        )

        ColorDialog(
            show = showColor,
            onDismissRequest = { showColor = false },
            onColorClick = mainViewModel::setAllColor,
            currentColor = (mainState.value as? MainState.Success)?.selectState?.colorIndex ?: -1,
        )

        RenameLabelAlertDialog(
            show = showRenameLabel,
            label = (mainState.value as? MainState.Success)?.labelName ?: "",
            onDismissRequest = { showRenameLabel = false },
            onChangeName = mainViewModel::renameLabel,
        )

        DeleteLabelAlertDialog(
            show = showDeleteLabel,
            onDismissRequest = { showDeleteLabel = false },
            onDelete = mainViewModel::deleteLabel,
        )
    }
}
