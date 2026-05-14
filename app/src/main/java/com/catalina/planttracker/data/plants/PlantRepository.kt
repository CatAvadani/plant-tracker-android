package com.catalina.planttracker.data.plants

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.catalina.planttracker.data.model.ErrorResponse
import com.catalina.planttracker.data.model.CreatePlantRequest
import com.catalina.planttracker.data.model.UpdatePlantRequest
import com.catalina.planttracker.data.network.RetrofitInstance
import com.catalina.planttracker.model.Plant
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response

class PlantRepository {
    private val api get() = RetrofitInstance.plantApi
    private val gson = Gson()

    companion object {
        private var cachedPlants: List<Plant>? = null
    }

    fun getCachedPlants(): List<Plant>? = cachedPlants

    suspend fun getPlants(): Result<List<Plant>> {
        return try {
            val response = api.getPlants()
            if (response.isSuccessful) {
                val plants = response.body() ?: emptyList()
                cachedPlants = plants
                Result.success(plants)
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
                val plant = response.body()!!
                cachedPlants = cachedPlants?.let { current -> current + plant }
                Result.success(plant)
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
                val plant = response.body()!!
                cachedPlants = cachedPlants?.map { current ->
                    if (current.id == id) plant else current
                }
                Result.success(plant)
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
                cachedPlants = cachedPlants?.filterNot { it.id == id }
                Result.success(Unit)
            } else {
                Result.failure(Exception(responseMessage(response, "Failed to delete plant")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadImage(imageUri: Uri, context: Context): Result<String> {
        return try {
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(imageUri)
                ?: return Result.failure(Exception("Could not open input stream"))

            val bytes = inputStream.use { it.readBytes() }
            if (bytes.isEmpty()) {
                return Result.failure(Exception("Selected image file is empty"))
            }

            val mediaType = (contentResolver.getType(imageUri) ?: "image/jpeg")
                .toMediaTypeOrNull()
                ?: "image/jpeg".toMediaType()
            val fileName = contentResolver.fileName(imageUri) ?: "plant_image.jpg"
            val requestFile = bytes.toRequestBody(mediaType)
            val partNames = listOf("image", "imageFile", "file")

            var lastError = "Failed to upload image"
            for (partName in partNames) {
                val body = MultipartBody.Part.createFormData(partName, fileName, requestFile)
                val response = api.uploadImage(body)
                if (response.isSuccessful && response.body() != null) {
                    return Result.success(response.body()!!.imageUrl)
                }
                lastError = responseMessage(response, "Failed to upload image")
            }

            Result.failure(Exception(lastError))
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

private fun android.content.ContentResolver.fileName(uri: Uri): String? {
    return query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (nameIndex >= 0 && cursor.moveToFirst()) {
            cursor.getString(nameIndex)
        } else {
            null
        }
    }
}
