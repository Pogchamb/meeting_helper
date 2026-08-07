package pa.chan.network

import okhttp3.Interceptor
import okhttp3.Response
import okio.IOException
import pa.chan.domain.ApiKeyProvider
import javax.inject.Inject
import kotlin.jvm.Throws

class AuthTokenInterceptor @Inject constructor(private val apiKeyProvider: ApiKeyProvider): Interceptor {


    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val apiKey = apiKeyProvider.getApiKey()

        val  request = if (apiKey != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $apiKey")
                .build()
        } else {
            chain.request()
        }

        return chain.proceed(request)
    }


}