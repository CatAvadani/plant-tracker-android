package com.catalina.planttracker.ui.carelogs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.catalina.planttracker.data.carelogs.CareLogRepository
import com.catalina.planttracker.data.model.CareLogResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class CareLogUiState {
    object Idle : CareLogUiState()
    object Loading : CareLogUiState()
    data class Success(val data: List<CareLogResponse>) : CareLogUiState()
    data class Error(val message: String) : CareLogUiState()
}

class CareLogViewModel : ViewModel() {
    private val repository = CareLogRepository()

    private val _careLogUiState = MutableStateFlow<CareLogUiState>(CareLogUiState.Idle)
    val careLogUiState: StateFlow<CareLogUiState> = _careLogUiState.asStateFlow()

    fun loadCareLogs(plantId: Int) {
        viewModelScope.launch {
            _careLogUiState.value = CareLogUiState.Loading
            repository.getCareLogs(plantId)
                .onSuccess { logs -> _careLogUiState.value = CareLogUiState.Success(logs) }
                .onFailure { e -> _careLogUiState.value = CareLogUiState.Error(e.message ?: "Failed to load care logs") }
        }
    }

    fun createCareLog(plantId: Int, entryType: Int, notes: String?) {
        viewModelScope.launch {
            repository.createCareLog(plantId, entryType, notes)
                .onSuccess { loadCareLogs(plantId) }
                .onFailure { e -> _careLogUiState.value = CareLogUiState.Error(e.message ?: "Failed to create care log") }
        }
    }

    fun deleteCareLog(plantId: Int, id: Int) {
        viewModelScope.launch {
            repository.deleteCareLog(plantId, id)
                .onSuccess { loadCareLogs(plantId) }
                .onFailure { e -> _careLogUiState.value = CareLogUiState.Error(e.message ?: "Failed to delete care log") }
        }
    }

    fun resetState() {
        _careLogUiState.value = CareLogUiState.Idle
    }
}

class CareLogViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CareLogViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CareLogViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
