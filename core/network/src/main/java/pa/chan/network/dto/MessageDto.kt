package pa.chan.network.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MessageDto(
    val role: String,
    val content: String
)
