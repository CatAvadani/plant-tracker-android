package com.catalina.planttracker.data.repository

import com.catalina.planttracker.data.local.TokenManager
import com.catalina.planttracker.data.network.AuthApiService
import com.catalina.planttracker.data.network.model.AuthResponse
import com.catalina.planttracker.data.network.model.LoginRequest
import com.catalina.planttracker.data.network.model.RegisterRequest
import retrofit2.Response

class AuthRepository(
    private val apiService: AuthApiService,
    private val tokenManager: TokenManager
) {
    suspend fun login(request: LoginRequest): Result<AuthResponse> {
        return try {
            val response = apiService.login(request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    tokenManager.saveToken(body.token)
                    // Automatically generate API key after login
                    generateApiKey()
                    Result.success(body)
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                Result.failure(Exception("Login failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(request: RegisterRequest): Result<Unit> {
        return try {
            val response = apiService.register(request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Registration failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun generateApiKey(): Result<String> {
        return try {
            val response = apiService.generateApiKey()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    tokenManager.saveApiKey(body.apiKey)
                    Result.success(body.apiKey)
                } else {
                    Result.failure(Exception("Empty API key response"))
                }
            } else {
                Result.failure(Exception("API Key generation failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        tokenManager.clearAll()
    }

    fun isLoggedIn(): Boolean {
        return tokenManager.getToken() != null
    }
}
