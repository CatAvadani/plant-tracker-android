package com.catalina.planttracker.data.auth

import com.catalina.planttracker.data.local.TokenManager
import com.catalina.planttracker.data.model.ErrorResponse
import com.catalina.planttracker.data.model.LoginRequest
import com.catalina.planttracker.data.model.RegisterRequest
import com.catalina.planttracker.data.network.AuthApiService
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class AuthRepository(
    private val api: AuthApiService,
    private val tokenManager: TokenManager
) {
    private val gson = Gson()

    suspend fun login(email: String, password: String): String? {
        return try {
            val loginResponse = api.login(LoginRequest(email.trim(), password))
            if (loginResponse.isSuccessful) {
                val body = loginResponse.body()
                if (body != null) {
                    tokenManager.saveToken(body.token)

                    // Save user info if available
                    body.user?.let { user ->
                        tokenManager.saveUser(user.email, user.displayName)
                    }

                    null // Success
                } else {
                    "Login failed: Empty response"
                }
            } else {
                responseMessage(loginResponse, "Login failed")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Connection error: ${e.localizedMessage ?: "Unknown error"}"
        }
    }

    suspend fun register(email: String, password: String, confirmPassword: String, displayName: String): String? {
        return try {
            val response = api.register(RegisterRequest(email.trim(), password, confirmPassword, displayName.trim()))
            if (response.isSuccessful) {
                null // Success
            } else {
                responseMessage(response, "Registration failed")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Registration error: ${e.localizedMessage ?: "Unknown error"}"
        }
    }

    suspend fun logout() {
        withContext(Dispatchers.IO) {
            tokenManager.clearAll()
        }
    }

    fun isLoggedIn(): Boolean {
        return tokenManager.getToken() != null
    }

    fun getUser(): Pair<String?, String?> {
        return tokenManager.getUser()
    }

    private fun responseMessage(response: Response<*>, fallback: String): String {
        val serverMessage = response.errorBody()?.string()?.let { errorBody ->
            runCatching {
                gson.fromJson(errorBody, ErrorResponse::class.java).message
            }.getOrNull()
        }

        return if (!serverMessage.isNullOrBlank()) {
            serverMessage
        } else {
            "$fallback: ${response.code()}"
        }
    }
}
