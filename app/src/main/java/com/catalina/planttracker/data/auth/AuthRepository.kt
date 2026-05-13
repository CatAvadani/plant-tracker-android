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
                    
                    // Save user info if available
                    body.user?.let { user ->
                        tokenManager.saveUser(user.email, user.displayName)
                    }
                    
                    // Clear existing API key before generating a new one
                    // to ensure no old/invalid key is sent in the header
                    tokenManager.saveApiKey("") 

                    // Generate and save API Key
                    val apiKeyResponse = api.generateApiKey()
                    if (apiKeyResponse.isSuccessful) {
                        val apiKeyBody = apiKeyResponse.body()
                        if (apiKeyBody != null) {
                            tokenManager.saveApiKey(apiKeyBody.apiKey)
                            null // Success
                        } else {
                            "API Key generation failed: Empty response"
                        }
                    } else {
                        "API Key generation failed: ${apiKeyResponse.code()}"
                    }
                } else {
                    "Login failed: Empty response"
                }
            } else {
                "Login failed: ${loginResponse.code()}"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Connection error: ${e.localizedMessage ?: "Unknown error"}"
        }
    }

    suspend fun register(email: String, password: String, confirmPassword: String, displayName: String): String? {
        return try {
            val response = api.register(RegisterRequest(email, password, confirmPassword, displayName))
            if (response.isSuccessful) {
                null // Success
            } else {
                "Registration failed: ${response.code()}"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Registration error: ${e.localizedMessage ?: "Unknown error"}"
        }
    }

    fun logout() {
        tokenManager.clearAll()
    }

    fun isLoggedIn(): Boolean {
        return tokenManager.getToken() != null
    }
}
