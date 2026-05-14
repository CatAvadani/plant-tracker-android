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
    val name: String,
    val species: String? = null,
    val location: String? = null,
    val wateringFrequencyDays: Int? = null,
    val lastWatered: String? = null,
    val healthStatus: Int? = null,
    val notes: String? = null,
    val imageUrl: String? = null
)
