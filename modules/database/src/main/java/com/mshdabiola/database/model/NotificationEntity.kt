package com.mshdabiola.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Entity representing the notification place for a note.
 * It is associated with a [NoteEntity] via [noteId].
 */
@Entity(
    tableName = "notification_table",
    foreignKeys = [
        ForeignKey(
            entity = NoteEntity::class,
            parentColumns = ["id"],
            childColumns = ["note_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "note_id", index = true)
    val noteId: Long,

    val reminderDateTimeStamp: Long,

    /**
     * Type of place.
     * 0: Home, 1: Work, 2: School, 3: Custom/Edit.
     * This can be mapped to/from [com.mshdabiola.model.NotificationPlace].
     */
    @ColumnInfo(name = "place_type")
    val placeType: Int,

    /**
     * Custom place name/address if [placeType] indicates a custom location.
     * Null otherwise.
     */
    @ColumnInfo(name = "custom_place_name")
    val customPlaceName: String? = null,

    /**
     * Type of interval, corresponding to [com.mshdabiola.model.NotificationInterval.index].
     * 0: DoNotRepeat, 1: Daily, 2: Weekly, 3: Monthly, 4: Yearly, 5: Custom.
     */
    @ColumnInfo(name = "type_index")
    val typeIndex: Int,

    @ColumnInfo(name = "interval_value", defaultValue = "1")
    val intervalValue: String = "1", // E.g., repeat every '1' day, '2' weeks

    /**
     * For weekly intervals, stores days of the week as a comma-separated string (e.g., "1,3,5" for Mon, Wed, Fri).
     * Null if not applicable.
     */
    @ColumnInfo(name = "weekly_days")
    val weeklyDays: String? = null,

    /**
     * For monthly intervals, true if it should occur on the same day of the month.
     * Null if not applicable.
     */
    @ColumnInfo(name = "monthly_same_day")
    val monthlySameDay: Boolean? = null,

    /**
     * Type of interval end, corresponding to [com.mshdabiola.model.IntervalEnd.index].
     * 0: Forever, 1: EndDate, 2: NumberOfTimes.
     */
    @ColumnInfo(name = "interval_end_type_index")
    val intervalEndTypeIndex: Int,

    /**
     * For [com.mshdabiola.model.IntervalEnd.EndDate], stores the end date as epoch days.
     * Null if not applicable.
     */
    @ColumnInfo(name = "end_date_epoch_day")
    val endDateEpochDay: Long? = null,

    /**
     * For [com.mshdabiola.model.IntervalEnd.NumberOfTimes], stores the number of occurrences.
     * Null if not applicable.
     */
    @ColumnInfo(name = "number_of_times")
    val numberOfTimes: Int? = null,

)
