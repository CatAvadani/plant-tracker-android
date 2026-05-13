package com.catalina.planttracker.data.network

import com.catalina.planttracker.data.local.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        // Add JWT Token
        tokenManager.getToken()?.let { token ->
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        // Add API Key
        tokenManager.getApiKey()?.let { apiKey ->
            requestBuilder.addHeader("X-Api-Key", apiKey)
        }

        return chain.proceed(requestBuilder.build())
    }
}
