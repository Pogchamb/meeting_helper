package pa.chan.network.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RequestStructureDto(
    val model: String,
    val messages: List<MessageDto>
)
