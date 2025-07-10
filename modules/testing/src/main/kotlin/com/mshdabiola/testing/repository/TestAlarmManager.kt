package com.mshdabiola.testing.repository

import com.mshdabiola.common.IAlarmManager

class TestAlarmManager : IAlarmManager {
    override fun setAlarm(
        timeInMil: Long,
        interval: Long?,
        requestCode: Int,
        title: String,
        noteId: Long,
        content: String,
    ) {
        TODO("Not yet implemented")
    }

    override fun deleteAlarm(requestCode: Int) {
        TODO("Not yet implemented")
    }
}
