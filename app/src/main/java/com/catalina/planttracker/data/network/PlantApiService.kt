package com.catalina.planttracker.data.network

import com.catalina.planttracker.model.Plant
import com.catalina.planttracker.data.model.CreatePlantRequest
import com.catalina.planttracker.data.model.UpdatePlantRequest
import retrofit2.Response
import retrofit2.http.*

interface PlantApiService {
    @GET("api/plants")
    suspend fun getPlants(): Response<List<Plant>>

    @GET("api/plants/{id}")
    suspend fun getPlant(@Path("id") id: Int): Response<Plant>

    @POST("api/plants")
    suspend fun createPlant(@Body request: CreatePlantRequest): Response<Plant>

    @PUT("api/plants/{id}")
    suspend fun updatePlant(@Path("id") id: Int, @Body request: UpdatePlantRequest): Response<Plant>

    @DELETE("api/plants/{id}")
    suspend fun deletePlant(@Path("id") id: Int): Response<Unit>
}
