package pa.chan.domain

import pa.chan.domain.models.SummaryRequestModel
import pa.chan.domain.models.SummaryResponseModel

interface SummaryRepository {
    suspend fun generateSummary(request: SummaryRequestModel): SummaryResponseModel
}