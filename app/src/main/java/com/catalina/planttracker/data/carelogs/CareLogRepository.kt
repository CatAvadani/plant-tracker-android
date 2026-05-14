package com.catalina.planttracker.data.carelogs

import com.catalina.planttracker.data.model.CareLogResponse
import com.catalina.planttracker.data.model.CreateCareLogRequest
import com.catalina.planttracker.data.model.ErrorResponse
import com.catalina.planttracker.data.network.RetrofitInstance
import com.google.gson.Gson
import retrofit2.Response

class CareLogRepository {
    private val api get() = RetrofitInstance.careLogApi
    private val gson = Gson()

    suspend fun getCareLogs(plantId: Int): Result<List<CareLogResponse>> {
        return try {
            val response = api.getCareLogs(plantId)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception(responseMessage(response, "Failed to fetch care logs")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createCareLog(
        plantId: Int,
        entryType: Int,
        notes: String?
    ): Result<CareLogResponse> {
        return try {
            val request = CreateCareLogRequest(entryType = entryType, notes = notes)
            val response = api.createCareLog(plantId, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(responseMessage(response, "Failed to create care log")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCareLog(plantId: Int, id: Int): Result<Unit> {
        return try {
            val response = api.deleteCareLog(plantId, id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(responseMessage(response, "Failed to delete care log")))
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
