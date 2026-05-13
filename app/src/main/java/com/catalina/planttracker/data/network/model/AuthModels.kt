package com.catalina.planttracker.data.network.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val confirmPassword: String
)

data class AuthResponse(
    val token: String,
    val expiresAt: String
)

data class ApiKeyResponse(
    val apiKey: String
)
