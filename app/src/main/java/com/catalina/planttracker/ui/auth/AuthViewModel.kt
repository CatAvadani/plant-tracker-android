package com.catalina.planttracker.ui.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.catalina.planttracker.data.auth.AuthRepository
import com.catalina.planttracker.data.local.TokenManager
import com.catalina.planttracker.data.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.login(email, password)
            if (result == null) {
                _authState.value = AuthState.Success
            } else {
                _authState.value = AuthState.Error(result)
            }
        }
    }

    fun register(email: String, password: String, confirmPassword: String, displayName: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.register(email, password, confirmPassword, displayName)
            if (result == null) {
                _authState.value = AuthState.Success
            } else {
                _authState.value = AuthState.Error(result)
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }

    fun logout(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.logout()
            onComplete()
        }
    }

    fun getUser(): Pair<String?, String?> {
        return repository.getUser()
    }
}

class AuthViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            val tokenManager = TokenManager(context)
            val apiService = RetrofitInstance.getAuthService(context)
            val repository = AuthRepository(apiService, tokenManager)
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
