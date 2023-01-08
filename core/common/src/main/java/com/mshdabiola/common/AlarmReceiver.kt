package com.mshdabiola.common

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Toast.makeText(context, "Alarm", Toast.LENGTH_SHORT).show()
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val id = "com.mshdabiola.notepad.alarm"
        val title = intent.getStringExtra("title")?.ifBlank { "Alarm" } ?: "Alarm"
        val noteId = intent.getLongExtra("id", 0)
        val content = intent.getStringExtra("content")?.ifBlank { "Alarm notification" }
            ?: "Alarm notification"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(id, "NotePad Notification", "for alarm", notificationManager)
        }
        sendNotification(id, title, content, context, noteId, notificationManager)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel(
        id: String,
        name: String,
        description: String,
        notificationManager: NotificationManager
    ) {
        val channel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT)
        channel.description = description

        channel.enableLights(true)
        channel.enableVibration(true)
        channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 100)

        notificationManager.createNotificationChannel(channel)

    }

    private fun sendNotification(
        id: String,
        title: String,
        message: String,
        context: Context,
        notiId: Long,
        notificationManager: NotificationManager
    ) {
        val notification = NotificationCompat.Builder(context, id)
            .setSmallIcon(android.R.drawable.stat_notify_chat)
            .setContentTitle(title)
            .setContentText(message)
            .setChannelId(id)
            .build()

        notificationManager.notify(notiId.toInt(), notification)

    }
}