package pa.chan.domain.useCases

import pa.chan.domain.contracts.ApiKeyProvider
import javax.inject.Inject

class SaveApiKeyUseCase @Inject constructor(
    private val apiKeyProvider: ApiKeyProvider
) {
    suspend operator fun invoke(apiKey: String) {
        apiKeyProvider.saveApiKey(apiKey)
    }
}