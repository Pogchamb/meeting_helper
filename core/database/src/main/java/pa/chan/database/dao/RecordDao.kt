package pa.chan.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import pa.chan.database.entity.RecordSessionEntity
import pa.chan.domain.enums.RecordSessionStatus

@Dao
interface RecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecordSession(recordSessionEntity: RecordSessionEntity): Long

    @Query("SELECT * FROM RecordSessionEntity WHERE id = :id")
    suspend fun getSessionById(id: Long): RecordSessionEntity?

    @Query("UPDATE RecordSessionEntity SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: RecordSessionStatus)

    @Query("UPDATE RecordSessionEntity SET status = :status, text = :text WHERE id = :id")
    suspend fun updateTextAndStatus(id: Long, status: RecordSessionStatus, text: String)

    @Query("UPDATE RecordSessionEntity SET summary = :summary WHERE id = :id")
    suspend fun updateSummary(id: Long, summary: String)
    @Delete
    suspend fun deleteRecordSession(vararg recordSessionEntity: RecordSessionEntity)

    @Query("SELECT * FROM RecordSessionEntity ORDER BY date DESC")
    fun selectAllRecordSession(): Flow<List<RecordSessionEntity>>


}
