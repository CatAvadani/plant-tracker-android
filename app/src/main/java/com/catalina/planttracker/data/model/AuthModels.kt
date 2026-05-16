package com.catalina.planttracker.data.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val expiresAt: String,
    val user: UserResponse? = null
)

data class UserResponse(
    val id: String,
    val email: String,
    val displayName: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val confirmPassword: String,
    val displayName: String
)

data class RegisterResponse(
    val message: String? = null
)

data class ErrorResponse(
    val message: String? = null
)
