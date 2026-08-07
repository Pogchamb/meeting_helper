package pa.chan.network

import pa.chan.network.dto.RequestStructureDto
import pa.chan.network.dto.ResponseStructureDto
import retrofit2.http.Body
import retrofit2.http.POST

interface OpenAIApi {

    @POST("chat/completions")
    suspend fun createChatCompletion(@Body request: RequestStructureDto): ResponseStructureDto

}