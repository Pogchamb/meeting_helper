package pa.chan.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChoicesDto(
    val index: Int,
    val message: MessageDto,
    @Json(name = "finish_reason")
    val finishReason: String
)
