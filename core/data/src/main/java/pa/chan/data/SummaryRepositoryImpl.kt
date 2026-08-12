package pa.chan.data

import pa.chan.data.mappers.toModel
import pa.chan.domain.repository.SummaryRepository
import pa.chan.domain.configs.PromptConfig
import pa.chan.domain.exceptions.SummaryGenerationException
import pa.chan.domain.models.SummaryRequestModel
import pa.chan.domain.models.SummaryResponseModel
import pa.chan.network.OpenAIApi
import pa.chan.network.dto.MessageDto
import pa.chan.network.dto.RequestStructureDto
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class SummaryRepositoryImpl @Inject constructor(
    private val openAIApi: OpenAIApi
) : SummaryRepository {
    override suspend fun generateSummary(request: SummaryRequestModel): SummaryResponseModel {
        val systemPrompt = PromptConfig.SYSTEM_PROMPT
        val messages = listOf(
            MessageDto(role = "system", content = systemPrompt),
            MessageDto(role = "user", content = request.transcribedText)
        )

        val requestDto = RequestStructureDto(
            model = "llama3-8b-8192",
            messages = messages
        )

        try {
            val response = openAIApi.createChatCompletion(request = requestDto)
            val responseModel = response.toModel()

            return responseModel
        } catch (e: HttpException) {
            throw SummaryGenerationException("Ошибка генерации Summary", e)
        } catch (e: IOException) {
            throw SummaryGenerationException("Проблемы с подключением к сети", e)
        }
    }
}