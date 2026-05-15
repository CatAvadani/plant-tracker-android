package com.catalina.planttracker.data.analysis

import android.content.Context
import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import com.catalina.planttracker.data.model.ErrorResponse
import com.catalina.planttracker.data.model.PlantAnalysisResponse
import com.catalina.planttracker.data.network.RetrofitInstance
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source
import retrofit2.Response
import kotlinx.coroutines.CancellationException
import java.io.File

private const val MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024L
private val ALLOWED_MIME_TYPES = setOf("image/jpeg", "image/png", "image/webp")

class PlantAnalysisRepository {
    private val api get() = RetrofitInstance.plantAnalysisApi
    private val gson = Gson()

    suspend fun analyzePlant(imageUri: Uri, context: Context): Result<PlantAnalysisResponse> {
        return try {
            val contentResolver = context.contentResolver

            val mimeType = contentResolver.getType(imageUri) ?: "image/jpeg"
            if (mimeType !in ALLOWED_MIME_TYPES) {
                return Result.failure(Exception("Unsupported image format. Please select a JPEG, PNG, or WebP image."))
            }

            val fileSize = contentResolver.fileSize(imageUri) ?: context.fileSize(imageUri) ?: 0L
            if (fileSize == 0L) {
                return Result.failure(Exception("Selected image file is empty."))
            }
            if (fileSize > MAX_FILE_SIZE_BYTES) {
                return Result.failure(Exception("Image exceeds the 5 MB limit. Please choose a smaller image."))
            }

            val mediaType = mimeType.toMediaTypeOrNull()!!
            val fileName = contentResolver.fileName(imageUri) ?: "plant_image.jpg"

            val requestBody = object : RequestBody() {
                override fun contentType() = mediaType
                override fun contentLength() = fileSize
                override fun writeTo(sink: BufferedSink) {
                    val stream = contentResolver.openInputStream(imageUri)
                        ?: throw IllegalStateException("Could not open image stream")
                    stream.use { sink.writeAll(it.source()) }
                }
            }

            val part = MultipartBody.Part.createFormData("file", fileName, requestBody)
            val response = api.analyzePlant(part)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(userFacingError(response)))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Analysis failed. Please try again."))
        }
    }

    private fun userFacingError(response: Response<*>): String {
        val errorBody = response.errorBody()?.string()
        val serverMessage = errorBody?.let { body ->
            runCatching { gson.fromJson(body, ErrorResponse::class.java).message }.getOrNull()
                ?: body.takeIf { it.isNotBlank() }
        }
        return when (response.code()) {
            400 -> "Invalid or unsupported image. Please try a different photo."
            401 -> "Your session has expired. Please log in again."
            404 -> "AI plant analysis is not available on this server yet."
            500 -> {
                if (serverMessage?.contains("not configured", ignoreCase = true) == true) {
                    "AI plant analysis is not configured yet."
                } else {
                    "A server error occurred. Please try again later."
                }
            }
            502 -> "AI analysis failed. Please try again."
            else -> serverMessage?.takeIf { it.isNotBlank() } ?: "Analysis failed (HTTP ${response.code()})."
        }
    }
}

private fun android.content.ContentResolver.fileName(uri: Uri): String? =
    query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
        val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (idx >= 0 && cursor.moveToFirst()) cursor.getString(idx) else null
    }

private fun android.content.ContentResolver.fileSize(uri: Uri): Long? =
    query(uri, arrayOf(OpenableColumns.SIZE), null, null, null)?.use { cursor ->
        val idx = cursor.getColumnIndex(OpenableColumns.SIZE)
        if (idx >= 0 && cursor.moveToFirst() && !cursor.isNull(idx)) cursor.getLong(idx) else null
    }

private fun Context.fileSize(uri: Uri): Long? {
    if (uri.scheme == ContentResolver.SCHEME_FILE) {
        return uri.path?.let { File(it).length() }
    }

    return contentResolver.openAssetFileDescriptor(uri, "r")?.use { descriptor ->
        descriptor.length.takeIf { it >= 0L }
    }
}
