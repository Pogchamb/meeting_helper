package pa.chan.network

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import pa.chan.domain.ApiKeyProvider
import pa.chan.network.di.ApplicationScope
import javax.inject.Inject
import javax.inject.Singleton

private val Context.apiKeyDataStore by preferencesDataStore(name = "api_key_store")

@Singleton
class ApiKeyProviderImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    @ApplicationScope private val externalScope: CoroutineScope): ApiKeyProvider {

    private val apiKey = stringPreferencesKey("openai_api_key")

    @Volatile private var cachedKey: String? = null
    @Volatile private var isInitialized: Boolean = false

    init {
        externalScope.launch {
            cachedKey = context.apiKeyDataStore.data.first()[apiKey]
            isInitialized = true
        }
    }


    override fun getApiKey(): String? {
        return cachedKey
    }

    override suspend fun saveApiKey(key: String) {
        context.apiKeyDataStore.edit { preferences ->
            preferences[apiKey] = key
            cachedKey = key
        }
    }

}