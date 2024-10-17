package com.mshdabiola.playnotepad

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.compose.AsyncImage
import com.mshdabiola.designsystem.component.SkTextField
import com.mshdabiola.designsystem.icon.NoteIcon
import com.mshdabiola.designsystem.theme.SkTheme
import com.mshdabiola.model.DarkThemeConfig
import com.mshdabiola.model.Label
import com.mshdabiola.model.ThemeBrand
import com.mshdabiola.ui.LabelCard
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShareActivity : ComponentActivity() {

    val viewModel: SharedActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val splashScreen = installSplashScreen()
        installSplashScreen()
        var uiState: SharedActivityUiState by mutableStateOf(SharedActivityUiState.Loading)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state
                    .onEach { uiState = it }
                    .collect()
            }
        }
        enableEdgeToEdge()
        splashScreen.setKeepOnScreenCondition {
            when (uiState) {
                SharedActivityUiState.Loading -> true
                is SharedActivityUiState.Success -> false
            }
        }

        val title = intent.getStringExtra(Intent.EXTRA_TITLE) ?: intent.getStringExtra(Intent.EXTRA_TEXT) ?: ""
        val subject = intent.getStringExtra(Intent.EXTRA_SUBJECT) ?: intent.getStringExtra(Intent.EXTRA_TEXT) ?: ""

        val size = intent.clipData?.itemCount ?: 0
        val list = (0 until size)
            .mapNotNull { intent.clipData?.getItemAt(it)?.uri?.toString() }

        println(list.joinToString())

        viewModel.newSharePost(title, subject, list)

        setContent {
            val darkTheme = shouldUseDarkTheme(uiState)

            // Update the edge to edge configuration to match the theme
            // This is the same parameters as the default enableEdgeToEdge call, but we manually
            // resolve whether or not to show dark theme using uiState, since it can be different
            // than the configuration's dark theme value based on the user preference.
            DisposableEffect(darkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        Color.TRANSPARENT,
                        Color.TRANSPARENT,
                    ) { darkTheme },
                    navigationBarStyle = SystemBarStyle.auto(
                        Color.TRANSPARENT,
                        Color.TRANSPARENT,
                    ) { darkTheme },
                )
                onDispose {}
            }

            SkTheme(
                darkTheme = darkTheme,
                disableDynamicTheming = shouldDisableDynamicTheming(uiState),
            ) {
                if (uiState is SharedActivityUiState.Success) {
                    var showLabel by remember {
                        mutableStateOf(false)
                    }
//    val shareViewModel= hiltViewModel<DetailViewModel>()
                    ActionEditScreen(
                        title = viewModel.title,
                        content = viewModel.content,
                        success = uiState as SharedActivityUiState.Success,
                        //  onSaveNote = shareViewModel::saveNote,
                        // showLabel = shareViewModel.showLabel,
                        showLabelDialog = { showLabel = true },
                        onFinish = {
                            lifecycleScope.launch {
                                viewModel.delete()
                                this@ShareActivity.finish()
                            }
                        },
                    )

                    EditLabels(
                        show = showLabel,
                        labels = (uiState as SharedActivityUiState.Success).labels,
                        checks = (uiState as SharedActivityUiState.Success).notepad.labels,
                        //  labels = shareViewModel.labels,
                        onDismissRequest = { showLabel = false },
                        onToggleLabel = viewModel::toggleCheck,
                    )
                }
            }
        }
    }
}

@Composable
private fun chooseTheme(
    uiState: MainActivityUiState,
): ThemeBrand = when (uiState) {
    MainActivityUiState.Loading -> ThemeBrand.DEFAULT
    is MainActivityUiState.Success -> uiState.userData.themeBrand
}

@Composable
private fun shouldUseAndroidTheme(
    uiState: MainActivityUiState,
): Boolean = when (uiState) {
    MainActivityUiState.Loading -> false
    is MainActivityUiState.Success -> when (uiState.userData.themeBrand) {
        ThemeBrand.DEFAULT -> false
        ThemeBrand.PINK -> true
    }
}

@Composable
private fun shouldDisableDynamicTheming(
    uiState: SharedActivityUiState,
): Boolean = when (uiState) {
    SharedActivityUiState.Loading -> false
    is SharedActivityUiState.Success -> !uiState.userData.useDynamicColor
}

@Composable
private fun shouldUseDarkTheme(
    uiState: SharedActivityUiState,
): Boolean = when (uiState) {
    SharedActivityUiState.Loading -> isSystemInDarkTheme()
    is SharedActivityUiState.Success -> when (uiState.userData.darkThemeConfig) {
        DarkThemeConfig.FOLLOW_SYSTEM -> isSystemInDarkTheme()
        DarkThemeConfig.LIGHT -> false
        DarkThemeConfig.DARK -> true
    }
}

private val lightScrim = Color.argb(0xe6, 0xFF, 0xFF, 0xFF)
private val darkScrim = Color.argb(0x80, 0x1b, 0x1b, 0x1b)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ActionEditScreen(
    success: SharedActivityUiState.Success = SharedActivityUiState.Success(),
    title: TextFieldState = TextFieldState(),
    content: TextFieldState = TextFieldState(),
    showLabel: Boolean = true,
    showLabelDialog: () -> Unit = {},
    onFinish: () -> Unit = {},
) {
    Scaffold(
//        floatingActionButton = {
//            ExtendedFloatingActionButton(
//                modifier = Modifier.imePadding(),
//                text = { Text(text = "Add note") },
//                icon = { Icon(imageVector = Icons.Default.Add, contentDescription = "Add") },
//                onClick = {
// //                    onSaveNote(title, subject, images.flatten())
// //                    (context as Activity).finish()
//                },
//            )
//        },
//        topBar = {
//            TopAppBar(
//                title = { },
//                actions = {
//                    IconButton(onClick = onFinish) {
//                        Icon(imageVector = Icons.Outlined.Cancel, contentDescription = "Cancel")
//                    }
//                },
//            )
//        },
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (success.notepad.images.isNotEmpty()) {
                item {
                    Box {
                        success.notepad.images.reversed().chunked(3).forEach { imageList ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                            ) {
                                imageList.forEach {
                                    AsyncImage(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(200.dp),
                                        model = it.path,
                                        contentDescription = "",
                                        contentScale = ContentScale.Crop,
                                    )
                                }
                            }
                        }

                        FilledTonalIconButton(
                            modifier = Modifier.align(Alignment.TopEnd),
                            onClick = onFinish,
                        ) {
                            Icon(imageVector = NoteIcon.Cancel, contentDescription = "Cancel")
                        }
                    }
                }
            }
            item {
                SkTextField(
                    state = title,
                    placeholder = "Title",
                    textStyle = MaterialTheme.typography.titleLarge,
                    imeAction = ImeAction.Next,
                    modifier = Modifier
                        .fillMaxWidth(),

                )
            }

            item {
                SkTextField(
                    state = content,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    placeholder = "Subject",

                    modifier = Modifier
                        .fillMaxWidth(),

                )
            }
            if (success.notepad.labels.isNotEmpty()) {
                item {
                    FlowRow(
                        modifier = Modifier.padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        success.notepad.labels.forEach {
                            LabelCard(
                                name = it.label,
                                color = MaterialTheme.colorScheme.secondary,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
            }
            if (showLabel) {
                item {
                    TextButton(onClick = { showLabelDialog() }) {
                        Text("Add Labels")
                    }
                }
            }
        }
    }
}

@Composable
fun EditLabels(
    show: Boolean = false,
    onDismissRequest: () -> Unit = {},
    labels: List<Label> = emptyList<Label>(),
    checks: List<Label> = emptyList<Label>(),
    onToggleLabel: (Int) -> Unit = {},
) {
    AnimatedVisibility(visible = show) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text(text = "Add Labels") },
            confirmButton = {
                TextButton(onClick = { onDismissRequest() }) {
                    Text("Close")
                }
            },
            text = {
                LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                    itemsIndexed(labels, key = { i, l -> l.id }) { index, label ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,

                        ) {
                            Text(text = label.label, modifier = Modifier.weight(1f))
                            Checkbox(
                                checked = checks.contains(label),
                                onCheckedChange = { onToggleLabel(index) },
                            )
                        }
                    }
                }
            },
        )
    }
}

@Preview
@Composable
fun ActionEditScreenPreview() {
    ActionEditScreen()
}

@Preview
@Composable
fun DialogPreview() {
    EditLabels(
        show = true,
        labels = listOf(
            Label(
                id = 759L,
                label = "Emanuel",
            ),
            Label(
                id = 79L,
                label = "Emanuel",
            ),
            Label(
                id = 59L,
                label = "Emanuel",
            ),
            Label(
                id = 7529L,
                label = "Emanuel",
            ),
            Label(
                id = 7519L,
                label = "Emanuel",
            ),

        ),
    )
}
