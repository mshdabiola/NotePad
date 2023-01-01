package com.mshdabiola.editscreen

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AlarmManager
@Inject constructor(
    @ApplicationContext private val context: Context
) {


    fun setAlarm(timeInMil: Long, interval: Long?) {
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val alarmIntent = Intent(context, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        }

// Set the alarm to start at 8:30 a.m.
//        val calendar: Calendar = Calendar.getInstance().apply {
//            timeInMillis = System.currentTimeMillis()
//            set(Calendar.HOUR_OF_DAY, 8)
//            set(Calendar.MINUTE, 30)
//        }

// setRepeating() lets you specify a precise custom interval--in this case,
// 20 minutes.
        if (interval == null) {
            alarmMgr.set(
                /* type = */ AlarmManager.RTC_WAKEUP,
                /* triggerAtMillis = */ timeInMil,
                /* operation = */ alarmIntent
            )
        } else {
            alarmMgr.setRepeating(
                /* type = */ AlarmManager.RTC_WAKEUP,
                /* triggerAtMillis = */ timeInMil,
                /* intervalMillis = 1000 * 60 * 20*/interval,
                /* operation = */ alarmIntent
            )
        }


    }
}