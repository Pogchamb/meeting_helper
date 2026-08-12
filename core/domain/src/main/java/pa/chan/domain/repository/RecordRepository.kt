package pa.chan.domain.repository

import pa.chan.domain.enums.RecordSessionStatus
import pa.chan.domain.models.RecordSessionModel
import kotlinx.coroutines.flow.Flow

interface RecordRepository {
    suspend fun savePendingRecord(path: String): Long

    suspend fun updateStatus(id: Long, status: RecordSessionStatus)

    suspend fun updateSummary(id: Long, summary: String)

    suspend fun getSessionById(id: Long): RecordSessionModel?

    suspend fun updateTextAndStatus(id: Long, status: RecordSessionStatus, text: String)

    fun getSessions(): Flow<List<RecordSessionModel>>

}