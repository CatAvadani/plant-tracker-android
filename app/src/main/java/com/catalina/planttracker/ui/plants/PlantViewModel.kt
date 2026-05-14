package com.catalina.planttracker.ui.plants

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.catalina.planttracker.data.plants.PlantRepository
import com.catalina.planttracker.model.Plant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class PlantUiState {
    object Idle : PlantUiState()
    object Loading : PlantUiState()
    data class Success(val plants: List<Plant>) : PlantUiState()
    data class Error(val message: String) : PlantUiState()
}

class PlantViewModel : ViewModel() {
    private val repository = PlantRepository()

    private val _uiState = MutableStateFlow<PlantUiState>(PlantUiState.Idle)
    val uiState: StateFlow<PlantUiState> = _uiState.asStateFlow()

    private val _selectedPlant = MutableStateFlow<Plant?>(null)
    val selectedPlant: StateFlow<Plant?> = _selectedPlant.asStateFlow()

    fun loadPlants() {
        viewModelScope.launch {
            val cachedPlants = repository.getCachedPlants()
            if (cachedPlants != null) {
                _uiState.value = PlantUiState.Success(cachedPlants)
            } else {
                _uiState.value = PlantUiState.Loading
            }

            repository.getPlants()
                .onSuccess { plants ->
                    _uiState.value = PlantUiState.Success(plants)
                }
                .onFailure { exception ->
                    if (cachedPlants == null) {
                        _uiState.value = PlantUiState.Error(exception.message ?: "Unknown error")
                    }
                }
        }
    }

    fun loadPlant(id: Int) {
        viewModelScope.launch {
            repository.getPlant(id)
                .onSuccess { plant ->
                    _selectedPlant.value = plant
                }
                .onFailure { 
                    _selectedPlant.value = null
                }
        }
    }

    fun createPlant(
        name: String,
        species: String?,
        location: String?,
        wateringFrequencyDays: Int?,
        lastWatered: String?,
        healthStatus: Int?,
        notes: String?,
        imageUrl: String?
    ) {
        viewModelScope.launch {
            _uiState.value = PlantUiState.Loading
            repository.createPlant(
                name, species, location, wateringFrequencyDays,
                lastWatered, healthStatus, notes, imageUrl
            ).onSuccess { plant ->
                val cachedPlants = repository.getCachedPlants().orEmpty()
                _uiState.value = PlantUiState.Success(cachedPlants.ifEmpty { listOf(plant) })
            }.onFailure { exception ->
                _uiState.value = PlantUiState.Error(exception.message ?: "Failed to create plant")
            }
        }
    }

    fun updatePlant(
        id: Int,
        name: String,
        species: String?,
        location: String?,
        wateringFrequencyDays: Int?,
        lastWatered: String?,
        healthStatus: Int?,
        notes: String?,
        imageUrl: String?
    ) {
        viewModelScope.launch {
            _uiState.value = PlantUiState.Loading
            repository.updatePlant(
                id, name, species, location, wateringFrequencyDays,
                lastWatered, healthStatus, notes, imageUrl
            ).onSuccess {
                _uiState.value = PlantUiState.Success(repository.getCachedPlants().orEmpty())
            }.onFailure { exception ->
                _uiState.value = PlantUiState.Error(exception.message ?: "Failed to update plant")
            }
        }
    }

    fun deletePlant(id: Int) {
        viewModelScope.launch {
            _uiState.value = PlantUiState.Loading
            repository.deletePlant(id)
                .onSuccess {
                    _uiState.value = PlantUiState.Success(repository.getCachedPlants().orEmpty())
                }
                .onFailure { exception ->
                    _uiState.value = PlantUiState.Error(exception.message ?: "Failed to delete plant")
                }
        }
    }

    fun resetState() {
        _uiState.value = PlantUiState.Idle
        _selectedPlant.value = null
    }
}

class PlantViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlantViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlantViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
