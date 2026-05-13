package com.catalina.planttracker.data.network

import com.catalina.planttracker.data.network.model.ApiKeyResponse
import com.catalina.planttracker.data.network.model.AuthResponse
import com.catalina.planttracker.data.network.model.LoginRequest
import com.catalina.planttracker.data.network.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<Unit>

    @POST("api/apikey/generate")
    suspend fun generateApiKey(): Response<ApiKeyResponse>
}
