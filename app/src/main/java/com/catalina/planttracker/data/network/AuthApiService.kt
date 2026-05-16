package com.catalina.planttracker.data.network

import com.catalina.planttracker.data.model.LoginRequest
import com.catalina.planttracker.data.model.LoginResponse
import com.catalina.planttracker.data.model.RegisterRequest
import com.catalina.planttracker.data.model.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}
