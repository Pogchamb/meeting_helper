package pa.chan.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pa.chan.database.dao.RecordDao
import pa.chan.database.entity.RecordSessionEntity
import pa.chan.database.entity.toModel
import pa.chan.domain.enums.RecordSessionStatus
import pa.chan.domain.models.RecordSessionModel
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

    override suspend fun updateStatus(
        id: Long,
        status: RecordSessionStatus
    ) {
        recordDao.updateStatus(id, status)
    }

    override suspend fun updateTextAndStatus(
        id: Long,
        status: RecordSessionStatus,
        text: String
    ) {
       recordDao.updateTextAndStatus(id, status, text)
    }

    override fun getSessions(): Flow<List<RecordSessionModel>> {
        return  recordDao.selectAllRecordSession().map { entityList ->
            entityList.map {
                it.toModel()
            }
        }
    }

    override suspend fun getSessionById(id: Long): RecordSessionModel? {
        return recordDao.getSessionById(id)?.toModel()
    }

}