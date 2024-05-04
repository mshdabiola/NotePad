package com.mshdabiola.playnotepad

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
//import androidx.work.Worker
//import androidx.work.WorkerParameters
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.RemoteMessage.Notification
import timber.log.Timber
import java.net.URL

class MessageService : FirebaseMessagingService() {

    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
//        if (remoteMessage.data.isNotEmpty()) {
//            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
//
//            // Check if data needs to be processed by long running job
//            if (needsToBeScheduled()) {
//                // For long-running tasks (10 seconds or more) use WorkManager.
//                scheduleJob()
//            } else {
//                // Handle message within 10 seconds
//                handleNow(remoteMessage.)
//            }
//        }


        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            sendNotification(it, this)

        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    private fun needsToBeScheduled() = false

    // [START on_new_token]
    /**
     * Called if the FCM registration token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the
     * FCM registration token is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Timber.tag(TAG).d("Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String?) {
        //  Implement this method to send token to your app server.
        Timber.tag(TAG).d("sendRegistrationTokenToServer $token")
    }
    // [END on_new_token]

    private fun scheduleJob() {
        // [START dispatch_job]
//        val work = OneTimeWorkRequest.Builder(MyWorker::class.java)
//            .build()
//        WorkManager.getInstance(this)
//            .beginWith(work)
//            .enqueue()
        // [END dispatch_job]
    }


    private fun sendNotification(notification: Notification, context: Context) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val requestCode = 0
        val pendingIntent = PendingIntent.getActivity(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE,
        )
        Timber.e("notification image ${notification.imageUrl}")

        val channelId = "fcm_default_channel"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setColor(context.getColor(R.color.primary))
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle(notification.title)
            .setContentText(notification.body)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
        notification.imageUrl?.let {
            val bitmap = getBitmap(it.toString())
            notificationBuilder
                .setLargeIcon(bitmap)
                .setStyle(
                    NotificationCompat
                        .BigPictureStyle()
                        .bigPicture(bitmap)
                        .bigLargeIcon(null as Bitmap?)
                )
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                context.getString(R.string.default_notification_channel_id),
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationId = 0
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }

//    internal class MyWorker(appContext: Context, workerParams: WorkerParameters) :
//        Worker(appContext, workerParams) {
//        override fun doWork(): Result {
//            // TODO(developer): add long running task here.
//            return Result.success()
//        }
//    }
}

fun getBitmap(uri: String): Bitmap? {
    return try {

        val input = URL(uri).openStream()

        val bitmap = BitmapFactory.decodeStream(input)

        input.close()
        Timber.e("download")
        bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}