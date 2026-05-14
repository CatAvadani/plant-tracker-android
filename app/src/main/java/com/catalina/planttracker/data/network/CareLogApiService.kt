package com.catalina.planttracker.data.network

import com.catalina.planttracker.data.model.CareLogResponse
import com.catalina.planttracker.data.model.CreateCareLogRequest
import retrofit2.Response
import retrofit2.http.*

interface CareLogApiService {
    @GET("api/plants/{plantId}/carelogs")
    suspend fun getCareLogs(@Path("plantId") plantId: Int): Response<List<CareLogResponse>>

    @POST("api/plants/{plantId}/carelogs")
    suspend fun createCareLog(
        @Path("plantId") plantId: Int,
        @Body request: CreateCareLogRequest
    ): Response<CareLogResponse>

    @GET("api/plants/{plantId}/carelogs/{id}")
    suspend fun getCareLog(
        @Path("plantId") plantId: Int,
        @Path("id") id: Int
    ): Response<CareLogResponse>

    @DELETE("api/plants/{plantId}/carelogs/{id}")
    suspend fun deleteCareLog(
        @Path("plantId") plantId: Int,
        @Path("id") id: Int
    ): Response<Unit>
}
