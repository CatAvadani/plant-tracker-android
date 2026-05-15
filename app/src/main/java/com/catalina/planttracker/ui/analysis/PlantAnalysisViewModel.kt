package com.catalina.planttracker.ui.analysis

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.catalina.planttracker.data.analysis.PlantAnalysisRepository
import com.catalina.planttracker.data.model.PlantAnalysisResponse
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class PlantAnalysisUiState {
    object Idle : PlantAnalysisUiState()
    object Loading : PlantAnalysisUiState()
    data class Success(val result: PlantAnalysisResponse) : PlantAnalysisUiState()
    data class Error(val message: String, val isRetryable: Boolean = false) : PlantAnalysisUiState()
}

class PlantAnalysisViewModel : ViewModel() {
    private val repository = PlantAnalysisRepository()
    private var analysisJob: Job? = null

    private val _uiState = MutableStateFlow<PlantAnalysisUiState>(PlantAnalysisUiState.Idle)
    val uiState: StateFlow<PlantAnalysisUiState> = _uiState.asStateFlow()

    fun analyzeImage(uri: Uri, context: Context) {
        analysisJob?.cancel()
        analysisJob = viewModelScope.launch {
            _uiState.value = PlantAnalysisUiState.Loading
            repository.analyzePlant(uri, context.applicationContext)
                .onSuccess { result ->
                    _uiState.value = PlantAnalysisUiState.Success(result)
                }
                .onFailure { exception ->
                    val msg = exception.message ?: "Analysis failed. Please try again."
                    val retryable = msg.contains("AI analysis", ignoreCase = true) ||
                            msg.contains("try again", ignoreCase = true)
                    _uiState.value = PlantAnalysisUiState.Error(msg, retryable)
                }
        }
    }

    fun cancelAnalysis() {
        analysisJob?.cancel(CancellationException("Plant analysis cancelled"))
        _uiState.value = PlantAnalysisUiState.Idle
    }

    fun reset() {
        analysisJob?.cancel()
        _uiState.value = PlantAnalysisUiState.Idle
    }
}

class PlantAnalysisViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlantAnalysisViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlantAnalysisViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
