package pa.chan.database.typeConverters

import androidx.room.TypeConverter
import pa.chan.database.enums.RecordSessionStatus
import java.util.Date

class Converters {
    @TypeConverter
    fun fromStatusEnum(value: RecordSessionStatus): String? {
        return when(value) {
            RecordSessionStatus.IN_PROGRESS -> "IN_PROGRESS"
            RecordSessionStatus.COMPLETED -> "COMPLETED"
            RecordSessionStatus.PENDING -> "PENDING"
        }
    }

    @TypeConverter
    fun stringToStatusEnum(value: String): RecordSessionStatus? {
        return when(value) {
            "IN_PROGRESS" -> RecordSessionStatus.IN_PROGRESS
            "COMPLETED" -> RecordSessionStatus.COMPLETED
            "PENDING" -> RecordSessionStatus.PENDING
            else -> null
        }
    }

    @TypeConverter
    fun fromTimeStamp(value: Long): Date {
        return Date(value)
    }

    @TypeConverter
    fun dateToTimeStamp(date: Date): Long {
        return date.time
    }
}