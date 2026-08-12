package pa.chan.domain.useCases

import pa.chan.domain.repository.SummaryRepository
import pa.chan.domain.models.SummaryRequestModel
import pa.chan.domain.models.SummaryResponseModel
import javax.inject.Inject

class GenerateSummaryUseCase @Inject constructor(private val summaryRepository: SummaryRepository) {

    suspend operator fun invoke(transcribedText: String): SummaryResponseModel {
        return summaryRepository.generateSummary(SummaryRequestModel(transcribedText))
    }

}