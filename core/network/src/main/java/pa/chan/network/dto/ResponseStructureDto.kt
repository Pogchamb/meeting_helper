package pa.chan.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ResponseStructureDto(
    val id: String,
    @Json(name = "object")
    val obj: String,
    val created: Long,
    val model: String,
    val choices: List<ChoicesDto>,
    val usage: UsageDto
)
