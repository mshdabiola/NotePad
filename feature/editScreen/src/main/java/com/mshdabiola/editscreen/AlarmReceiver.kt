package com.mshdabiola.editscreen

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.mshdabiola.designsystem.R

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Toast.makeText(context, "Alarm", Toast.LENGTH_SHORT).show()
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val id = "com.mshdabiola.notepad.alarm"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(id, "NotePad Notification", "for alarm", notificationManager)
        }
        sendNotification(id, "Alarm", "Alarm notification", context, notificationManager)

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
        notificationManager: NotificationManager
    ) {
        val notification = NotificationCompat.Builder(context, id)
            .setSmallIcon(R.drawable.outline_alarm_add_24)
            .setContentTitle(title)
            .setContentText(message)
            .setChannelId(id)
            .build()

        notificationManager.notify(101, notification)

    }
}