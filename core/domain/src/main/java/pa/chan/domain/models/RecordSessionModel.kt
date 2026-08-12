package pa.chan.domain.models

import pa.chan.domain.enums.RecordSessionStatus
import java.util.Date

data class RecordSessionModel(
    val id: Long,
    val pathToFile: String,
    val status: RecordSessionStatus,
    val text: String?,
    val date: Date,
    val summary: String? = null
)
