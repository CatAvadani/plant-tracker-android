package com.catalina.planttracker.data.network

import com.catalina.planttracker.data.local.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath
        val requestBuilder = request.newBuilder()

        if (!path.contains("api/auth/login") && !path.contains("api/auth/register")) {
            tokenManager.getToken()?.let { token ->
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }
            tokenManager.getApiKey()?.let { apiKey ->
                requestBuilder.addHeader("X-Api-Key", apiKey)
            }
        }

        val response = chain.proceed(requestBuilder.build())

        if (response.code == 401) {
            tokenManager.clearAll()
            TokenManager.emitSessionExpired()
        }

        return response
    }
}
