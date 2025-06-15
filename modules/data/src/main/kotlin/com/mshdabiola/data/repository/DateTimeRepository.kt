package com.mshdabiola.data.repository

import com.mshdabiola.model.IntervalEnd
import com.mshdabiola.model.NotificationDate
import com.mshdabiola.model.NotificationInterval
import com.mshdabiola.model.NotificationPlace
import com.mshdabiola.model.NotificationTime
import com.mshdabiola.model.NotificationUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

class DateTimeRepository @Inject constructor() {
    private val _notificationUiState = MutableStateFlow<NotificationUiState?>(null)
    val notificationUiState: StateFlow<NotificationUiState?> = _notificationUiState

    fun initialize(
        currentDateTime: LocalDateTime,
        currentInterval: NotificationInterval,
        currentPlace: NotificationPlace? = null,
    ) {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val times = listOf(
            NotificationTime(LocalTime(7, 0, 0)),
            NotificationTime(LocalTime(13, 0, 0)),
            NotificationTime(LocalTime(19, 0, 0)),
            NotificationTime(LocalTime(20, 0, 0)),
            NotificationTime(LocalTime(20, 0, 0), true),
        )
            .map {
                it.copy(isEnable = currentDateTime.time > it.time)
            }

        val dates = listOf(
            NotificationDate(now.date, false),
            NotificationDate(now.date.plus(1, DateTimeUnit.DAY), false),
            NotificationDate(now.date.plus(1, DateTimeUnit.WEEK), false),
            NotificationDate(now.date.plus(1, DateTimeUnit.MONTH), true),
        )

        val intervals = listOf(
            NotificationInterval.DoNotRepeat,
            NotificationInterval.Daily(
                intervalEnd = IntervalEnd.Forever,
            ),
            NotificationInterval.Weekly(
                days = listOf(now.date.dayOfWeek),
                intervalEnd = IntervalEnd.Forever,
            ),
            NotificationInterval.Monthly(
                sameDay = true,
                intervalEnd = IntervalEnd.Forever,
            ),
            NotificationInterval.Yearly(
                intervalEnd = IntervalEnd.Forever,
            ),
            NotificationInterval.Custom,
        )

        val places = listOf(
            NotificationPlace.Home,
            NotificationPlace.Work,
            NotificationPlace.School,
            NotificationPlace.Edit(""),
        )

        _notificationUiState.value = NotificationUiState(
            currentTime = currentDateTime.time,
            currentDate = currentDateTime.date,
            currentInterval = currentInterval,
            currentPlace = currentPlace,
            times = times,
            dates = dates,
            intervals = intervals,
            places = places,
        )
    }

    fun setPlace(place: NotificationPlace) {
        _notificationUiState.value = _notificationUiState.value?.copy(currentPlace = place)
    }
    fun setDate(date: LocalDate) {
        _notificationUiState.value = _notificationUiState.value?.copy(currentDate = date)
    }
    fun setTime(time: LocalTime) {
        _notificationUiState.value = _notificationUiState.value?.copy(currentTime = time)
    }
    fun setInterval(interval: NotificationInterval) {
        _notificationUiState.value = _notificationUiState.value?.copy(currentInterval = interval)
    }
}
