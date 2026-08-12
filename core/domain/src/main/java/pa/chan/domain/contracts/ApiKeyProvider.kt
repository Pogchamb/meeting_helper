package pa.chan.domain.contracts

interface ApiKeyProvider {
    fun getApiKey(): String?
    suspend fun saveApiKey(key: String)
}