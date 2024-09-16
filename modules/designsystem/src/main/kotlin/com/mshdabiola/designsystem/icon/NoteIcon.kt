package com.mshdabiola.designsystem.icon

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardVoice
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.outlined.Brush
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.KeyboardVoice
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.ui.graphics.Color
import com.mshdabiola.designsystem.R

object NoteIcon {

    val Image = R.drawable.modules_designsystem_outline_image_24
    val Label = R.drawable.modules_designsystem_outline_label_24

    val background = listOf(
        NoteBg(Color(0xFF737D55), R.drawable.modules_designsystem_asset_1),
        NoteBg(Color(0xFFDB69A3), R.drawable.modules_designsystem_asset_2),
        NoteBg(Color(0xFFB80D57), R.drawable.modules_designsystem_asset_3),
        NoteBg(Color(0xFFC26744), R.drawable.modules_designsystem_asset_4),
        NoteBg(Color(0xFFA3F7B7), R.drawable.modules_designsystem_asset_5),
        NoteBg(Color(0xFFE660B7), R.drawable.modules_designsystem_asset_6),
        NoteBg(Color(0xFFEE7D62), R.drawable.modules_designsystem_asset_7),
        NoteBg(Color(0xFFCACA5E), R.drawable.modules_designsystem_asset_8),
        NoteBg(Color(0xFF8D59B6), R.drawable.modules_designsystem_asset_9),
        NoteBg(Color(0xFFCF1879), R.drawable.modules_designsystem_asset_10),
    )
    val noteColors = listOf(
        Color(0xFFB590CA),
        Color(0xFFA8D3DA),
        Color(0xFFF5CAB3),
        Color(0xFFF3ECB8),
        Color(0xFFC2F0FC),
        Color(0xFFFFB6B6),
        Color(0xFFDBC890),
        Color(0xFFEF9A9A),
        Color(0xFFF48FB1),
        Color(0xFFB39DDB),
        Color(0xFF81D4FA),
        Color(0xFFA5D6A7),
        Color(0xFFFFF59D),
        Color(0xFFFFCC80),
        Color(0xFFFFAB91),
    )

    val searchIcons = arrayOf(
        Icons.Outlined.Notifications,
        Icons.Outlined.CheckBox,
        Icons.Outlined.Image,
        Icons.Outlined.KeyboardVoice,
        Icons.Outlined.Brush,
        Icons.Outlined.Link,
        Icons.AutoMirrored.Outlined.Label,
    )
}
