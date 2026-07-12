package pa.chan.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import pa.chan.database.entity.RecordSessionEntity

@Dao
interface RecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecordSession(recordSessionEntity: RecordSessionEntity): Long

    @Update
    suspend fun updateRecordSession(vararg recordSessionEntity: RecordSessionEntity)

    @Delete
    suspend fun deleteRecordSession(vararg recordSessionEntity: RecordSessionEntity)

    @Query("SELECT * FROM RecordSessionEntity ORDER BY date DESC")
    fun selectAllRecordSession(): Flow<List<RecordSessionEntity>>
}
