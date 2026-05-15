package com.catalina.planttracker.data.model

data class CreatePlantRequest(
    val name: String,
    val species: String? = null,
    val location: String? = null,
    val wateringFrequencyDays: Int? = null,
    val lastWatered: String? = null,
    val healthStatus: Int? = null,
    val notes: String? = null,
    val imageUrl: String? = null
)

data class UpdatePlantRequest(
    val name: String? = null,
    val species: String? = null,
    val location: String? = null,
    val wateringFrequencyDays: Int? = null,
    val lastWatered: String? = null,
    val healthStatus: Int? = null,
    val notes: String? = null,
    val imageUrl: String? = null
)

data class ImageUploadResponse(
    val imageUrl: String
)

data class PlantAnalysisResponse(
    val plantName: String,
    val confidence: Double,
    val healthStatus: String,
    val possibleIssues: List<String>,
    val wateringSuggestions: List<String>,
    val lightingSuggestions: List<String>,
    val careSuggestions: List<String>,
    val notes: String,
    val imageUrl: String,
    val wateringFrequencyDays: Int? = null
)
