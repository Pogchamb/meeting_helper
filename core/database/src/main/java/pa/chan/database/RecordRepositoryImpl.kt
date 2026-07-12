package pa.chan.database

import pa.chan.database.dao.RecordDao
import pa.chan.database.entity.RecordSessionEntity
import pa.chan.domain.enums.RecordSessionStatus
import pa.chan.domain.repository.RecordRepository
import java.util.Date
import javax.inject.Inject

class RecordRepositoryImpl @Inject constructor(private val recordDao: RecordDao) :
    RecordRepository {

    override suspend fun savePendingRecord(path: String): Long {
        val recordSessionEntity = RecordSessionEntity(
            pathToFile = path,
            status = RecordSessionStatus.PENDING,
            text = null,
            date = Date()
        )


        return recordDao.insertRecordSession(recordSessionEntity)
    }

}