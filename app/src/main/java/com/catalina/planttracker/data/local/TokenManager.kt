package com.catalina.planttracker.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class TokenManager(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val KEY_JWT_TOKEN = "jwt_token"
        private const val KEY_API_KEY = "api_key"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_display_name"

        private val _sessionExpired = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
        val sessionExpired: SharedFlow<Unit> = _sessionExpired.asSharedFlow()

        fun emitSessionExpired() {
            _sessionExpired.tryEmit(Unit)
        }
    }

    fun saveToken(token: String) {
        sharedPreferences.edit().putString(KEY_JWT_TOKEN, token).apply()
    }

    fun getToken(): String? = sharedPreferences.getString(KEY_JWT_TOKEN, null)

    fun saveApiKey(apiKey: String) {
        sharedPreferences.edit().putString(KEY_API_KEY, apiKey).apply()
    }

    fun getApiKey(): String? {
        val key = sharedPreferences.getString(KEY_API_KEY, null)
        return if (key.isNullOrEmpty()) null else key
    }

    fun saveUser(email: String, name: String) {
        sharedPreferences.edit().apply {
            putString(KEY_USER_EMAIL, email)
            putString(KEY_USER_NAME, name)
            apply()
        }
    }

    fun getUser(): Pair<String?, String?> {
        return Pair(
            sharedPreferences.getString(KEY_USER_EMAIL, null),
            sharedPreferences.getString(KEY_USER_NAME, null)
        )
    }

    fun clearAll() {
        sharedPreferences.edit().clear().commit()
    }
}
