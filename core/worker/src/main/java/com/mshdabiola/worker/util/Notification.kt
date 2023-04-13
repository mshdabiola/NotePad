package com.mshdabiola.worker.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.ForegroundInfo

private const val NotificationId = 0
private const val NotificationChannelID = "NotificationChannel"

fun Context.syncForegroundInfo() = ForegroundInfo(
    NotificationId,
    saveWorkNotification(),
)

/**
 * Notification displayed on lower API levels when sync workers are being
 * run with a foreground service
 */
private fun Context.saveWorkNotification(): Notification {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            NotificationChannelID,
            "game saver",
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = "for saving games"
        }
        // Register the channel with the system
        val notificationManager: NotificationManager? =
            getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

        notificationManager?.createNotificationChannel(channel)
    }

    return NotificationCompat.Builder(
        this,
        NotificationChannelID,
    )
        .setSmallIcon(
            android.R.drawable.ic_menu_save,
        )
        .setContentTitle("Saving current game")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()
}