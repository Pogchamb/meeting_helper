package pa.chan.domain.repository

interface RecordRepository {
    suspend fun savePendingRecord(path: String) : Long
}