package com.mshdabiola.designsystem.icon

import androidx.compose.ui.graphics.Color
import com.mshdabiola.designsystem.R

object NoteIcon {

    val Image = R.drawable.outline_image_24
    val Brush = R.drawable.baseline_brush_24
    val Check = R.drawable.outline_check_box_24
    val Voice = R.drawable.outline_keyboard_voice_24
    val Photo = R.drawable.outline_photo_camera_24
    val Addbox = R.drawable.outline_add_box
    val Copy = R.drawable.baseline_content_copy_24
    val Label = R.drawable.outline_label_24
    val Save = R.drawable.outline_save_24
    val Alarm = R.drawable.outline_alarm_add_24
    val Archive = R.drawable.outline_archive_24
    val Pin = R.drawable.outline_push_pin_24
    val PinFill = R.drawable.baseline_push_pin_24
    val ImageNoteSupported = R.drawable.outline_image_not_supported_24
    val Notification = R.drawable.outline_notifications_24
    val NotificationActive = R.drawable.outline_notifications_active_24
    val ColorLens = R.drawable.outline_color_lens_24
    val ColorNotSupported = R.drawable.outline_format_color_reset_24

    val background = listOf(
        NoteBg(Color.Blue, R.drawable.asset_2),
        NoteBg(Color.Blue, R.drawable.asset_2),
        NoteBg(Color.Blue, R.drawable.asset_3),
        NoteBg(Color.Blue, R.drawable.asset_4),
    )
    val noteColors = listOf(
        Color.Black,
        Color.Red,
        Color.Green,
        Color.Blue,
        Color.Magenta
    )
}
