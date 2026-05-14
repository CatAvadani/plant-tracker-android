package com.catalina.planttracker.data.plants

import com.catalina.planttracker.data.model.CreatePlantRequest
import com.catalina.planttracker.data.model.UpdatePlantRequest
import com.catalina.planttracker.data.network.RetrofitInstance
import com.catalina.planttracker.model.Plant

class PlantRepository {
    private val api = RetrofitInstance.plantApi

    suspend fun getPlants(): Result<List<Plant>> {
        return try {
            val response = api.getPlants()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Failed to fetch plants: ${response.message()}"))
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
                Result.failure(Exception("Failed to fetch plant: ${response.message()}"))
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
                Result.failure(Exception("Failed to create plant: ${response.message()}"))
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
                Result.failure(Exception("Failed to update plant: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deletePlant(id: Int): String? {
        return try {
            val response = api.deletePlant(id)
            if (response.isSuccessful) {
                null
            } else {
                "Failed to delete plant: ${response.message()}"
            }
        } catch (e: Exception) {
            e.message ?: "An unknown error occurred"
        }
    }
}
