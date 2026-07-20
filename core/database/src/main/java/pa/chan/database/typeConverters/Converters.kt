package pa.chan.database.typeConverters

import androidx.room.TypeConverter
import pa.chan.domain.enums.RecordSessionStatus
import java.util.Date

class Converters {
    @TypeConverter
    fun fromStatusEnum(value: RecordSessionStatus): String? {
        return when (value) {
            RecordSessionStatus.IN_PROGRESS -> "IN_PROGRESS"
            RecordSessionStatus.COMPLETED -> "COMPLETED"
            RecordSessionStatus.PENDING -> "PENDING"
            RecordSessionStatus.ERROR -> "ERROR"
        }
    }

    @TypeConverter
    fun stringToStatusEnum(value: String): RecordSessionStatus? {
        return when (value) {
            "IN_PROGRESS" -> RecordSessionStatus.IN_PROGRESS
            "COMPLETED" -> RecordSessionStatus.COMPLETED
            "PENDING" -> RecordSessionStatus.PENDING
            "Error" -> RecordSessionStatus.ERROR
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