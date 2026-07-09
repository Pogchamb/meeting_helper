package pa.chan.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pa.chan.database.dao.RecordDao
import pa.chan.database.entity.RecordSessionEntity
import pa.chan.database.typeConverters.Converters


@Database(entities = [RecordSessionEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDao
}