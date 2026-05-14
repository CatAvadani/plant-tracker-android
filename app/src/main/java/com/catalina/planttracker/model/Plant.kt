package com.catalina.planttracker.model

enum class HealthStatus(val value: Int) {
    HEALTHY(0),
    NEEDS_ATTENTION(1),
    CRITICAL(2);
    companion object {
        fun fromInt(value: Int) = entries.firstOrNull { it.value == value } ?: HEALTHY
    }
}

data class Plant(
    val id: Int,
    val name: String,
    val species: String? = null,
    val location: String? = null,
    val wateringFrequencyDays: Int? = null,
    val lastWatered: String? = null,
    val healthStatus: Int? = null,
    val notes: String? = null,
    val imageUrl: String? = null,
    val createdAt: String? = null
)

val fakePlants = listOf(
    Plant(1, "Fiddle Leaf Fig", "Ficus lyrata", "Living Room", 7, "2023-10-25", 0, "Needs indirect sunlight."),
    Plant(2, "Snake Plant", "Dracaena trifasciata", "Bedroom", 14, "2023-10-20", 1, "Very low maintenance."),
    Plant(3, "Monstera Deliciosa", "Monstera deliciosa", "Office", 7, "2023-10-22", 0, "Likes humidity."),
    Plant(4, "Pothos", "Epipremnum aureum", "Kitchen", 5, "2023-10-24", 0, "Grows very fast."),
    Plant(5, "Aloe Vera", "Aloe barbadensis miller", "Balcony", 21, "2023-10-15", 1, "Full sun.")
)
