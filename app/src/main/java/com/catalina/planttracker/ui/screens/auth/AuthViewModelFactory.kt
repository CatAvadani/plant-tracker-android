package com.catalina.planttracker.ui.screens.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.catalina.planttracker.data.local.TokenManager
import com.catalina.planttracker.data.network.RetrofitInstance
import com.catalina.planttracker.data.repository.AuthRepository

class AuthViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            val authApiService = RetrofitInstance.getAuthService(context)
            val tokenManager = TokenManager(context)
            val repository = AuthRepository(authApiService, tokenManager)
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
