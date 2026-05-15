package com.catalina.planttracker.data.network

import com.catalina.planttracker.data.model.PlantAnalysisResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface PlantAnalysisApiService {
    @Multipart
    @POST("api/plant-analysis")
    suspend fun analyzePlant(
        @Part file: MultipartBody.Part
    ): Response<PlantAnalysisResponse>
}
