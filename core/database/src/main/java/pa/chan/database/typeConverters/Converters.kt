package pa.chan.database.typeConverters

import androidx.room.TypeConverter
import pa.chan.domain.enums.RecordSessionStatus
import java.util.Date

class Converters {
    @TypeConverter
    fun fromStatusEnum(value: RecordSessionStatus): String {
        return value.name
    }

    @TypeConverter
    fun stringToStatusEnum(value: String): RecordSessionStatus {
        return RecordSessionStatus.valueOf(value)
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