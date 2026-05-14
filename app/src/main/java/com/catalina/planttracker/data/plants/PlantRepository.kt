package com.catalina.planttracker.data.plants

import com.catalina.planttracker.data.model.ErrorResponse
import com.catalina.planttracker.data.model.CreatePlantRequest
import com.catalina.planttracker.data.model.UpdatePlantRequest
import com.catalina.planttracker.data.network.RetrofitInstance
import com.catalina.planttracker.model.Plant
import com.google.gson.Gson
import retrofit2.Response

class PlantRepository {
    private val api get() = RetrofitInstance.plantApi
    private val gson = Gson()

    suspend fun getPlants(): Result<List<Plant>> {
        return try {
            val response = api.getPlants()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception(responseMessage(response, "Failed to fetch plants")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPlant(id: Int): Result<Plant> {
        return try {
            val response = api.getPlant(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(responseMessage(response, "Failed to fetch plant")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createPlant(
        name: String,
        species: String?,
        location: String?,
        wateringFrequencyDays: Int?,
        lastWatered: String?,
        healthStatus: Int?,
        notes: String?,
        imageUrl: String?
    ): Result<Plant> {
        return try {
            val request = CreatePlantRequest(
                name = name,
                species = species,
                location = location,
                wateringFrequencyDays = wateringFrequencyDays,
                lastWatered = lastWatered,
                healthStatus = healthStatus,
                notes = notes,
                imageUrl = imageUrl
            )
            val response = api.createPlant(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(responseMessage(response, "Failed to create plant")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePlant(
        id: Int,
        name: String,
        species: String?,
        location: String?,
        wateringFrequencyDays: Int?,
        lastWatered: String?,
        healthStatus: Int?,
        notes: String?,
        imageUrl: String?
    ): Result<Plant> {
        return try {
            val request = UpdatePlantRequest(
                name = name,
                species = species,
                location = location,
                wateringFrequencyDays = wateringFrequencyDays,
                lastWatered = lastWatered,
                healthStatus = healthStatus,
                notes = notes,
                imageUrl = imageUrl
            )
            val response = api.updatePlant(id, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(responseMessage(response, "Failed to update plant")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deletePlant(id: Int): Result<Unit> {
        return try {
            val response = api.deletePlant(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(responseMessage(response, "Failed to delete plant")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun responseMessage(response: Response<*>, fallback: String): String {
        val errorBody = response.errorBody()?.string()
        val serverMessage = errorBody?.let { body ->
            runCatching {
                gson.fromJson(body, ErrorResponse::class.java).message
            }.getOrNull()
                ?: body.takeIf { it.isNotBlank() }
        }

        return if (!serverMessage.isNullOrBlank()) {
            "$fallback: $serverMessage"
        } else {
            "$fallback: HTTP ${response.code()}"
        }
    }
}
