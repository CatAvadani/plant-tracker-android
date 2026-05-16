package com.catalina.planttracker.data.network

import com.catalina.planttracker.data.local.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath
        val requestBuilder = request.newBuilder()
        val isAuthAttempt = path.contains("api/auth/login") || path.contains("api/auth/register")

        if (!isAuthAttempt) {
            tokenManager.getToken()?.let { token ->
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }
        }

        val response = chain.proceed(requestBuilder.build())

        if (!isAuthAttempt && response.code == 401) {
            tokenManager.clearAll()
            TokenManager.emitSessionExpired()
        }

        return response
    }
}
