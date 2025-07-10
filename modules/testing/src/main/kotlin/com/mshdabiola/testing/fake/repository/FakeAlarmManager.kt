package com.mshdabiola.testing.fake.repository

import com.mshdabiola.common.IAlarmManager
import javax.inject.Inject

class FakeAlarmManager @Inject constructor() : IAlarmManager {
    override fun setAlarm(
        timeInMil: Long,
        interval: Long?,
        requestCode: Int,
        title: String,
        noteId: Long,
        content: String,
    ) {
    }

    override fun deleteAlarm(requestCode: Int) {
    }
}
