package pa.chan.data.mappers

import pa.chan.domain.models.SummaryResponseModel
import pa.chan.network.dto.ResponseStructureDto

fun ResponseStructureDto.toModel(): SummaryResponseModel {
    return SummaryResponseModel(
        this.choices.firstOrNull()?.message?.content ?: ""
    )
}

