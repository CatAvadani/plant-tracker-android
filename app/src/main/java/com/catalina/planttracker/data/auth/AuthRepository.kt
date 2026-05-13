package com.catalina.planttracker.data.auth

import com.catalina.planttracker.data.local.TokenManager
import com.catalina.planttracker.data.model.LoginRequest
import com.catalina.planttracker.data.model.RegisterRequest
import com.catalina.planttracker.data.network.AuthApiService

class AuthRepository(
    private val api: AuthApiService,
    private val tokenManager: TokenManager
) {

    suspend fun login(email: String, password: String): String? {
        return try {
            val loginResponse = api.login(LoginRequest(email, password))
            if (loginResponse.isSuccessful) {
                val body = loginResponse.body()
                if (body != null) {
                    tokenManager.saveToken(body.token)
                    
                    // Generate and save API Key
                    val apiKeyResponse = api.generateApiKey()
                    if (apiKeyResponse.isSuccessful) {
                        apiKeyResponse.body()?.let {
                            tokenManager.saveApiKey(it.apiKey)
                        }
                    }
                    null // Success
                } else {
                    "Login failed: Empty response"
                }
            } else {
                "Login failed: ${loginResponse.code()}"
            }
        } catch (e: Exception) {
            e.message ?: "An unknown error occurred"
        }
    }

    suspend fun register(email: String, password: String): String? {
        return try {
            val response = api.register(RegisterRequest(email, password, password)) // Using password as confirmPassword for now
            if (response.isSuccessful) {
                null // Success
            } else {
                "Registration failed: ${response.code()}"
            }
        } catch (e: Exception) {
            e.message ?: "An unknown error occurred"
        }
    }

    fun logout() {
        tokenManager.clearAll()
    }

    fun isLoggedIn(): Boolean {
        return tokenManager.getToken() != null
    }
}
