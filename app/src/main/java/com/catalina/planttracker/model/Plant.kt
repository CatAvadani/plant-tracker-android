package com.catalina.planttracker.model

data class Plant(
    val name: String,
    val species: String,
    val status: String,
    val nextWatering: String,
    val wateringFrequency: String = "Every 7 days",
    val location: String = "Living Room",
    val notes: String = "Needs indirect sunlight.",
    val lastWatered: String = "3 days ago",
    val careTips: List<String> = listOf("Avoid overwatering", "Wipe leaves for dust")
)

val fakePlants = listOf(
    Plant("Fiddle Leaf Fig", "Ficus lyrata", "Healthy", "In 2 days"),
    Plant("Snake Plant", "Dracaena trifasciata", "Needs Water", "Today", "Every 14 days", "Bedroom", "Very low maintenance."),
    Plant("Monstera Deliciosa", "Monstera deliciosa", "Healthy", "In 5 days", "Every 7 days", "Office", "Likes humidity."),
    Plant("Pothos", "Epipremnum aureum", "Healthy", "In 3 days", "Every 5 days", "Kitchen", "Grows very fast."),
    Plant("Aloe Vera", "Aloe barbadensis miller", "Needs Water", "Tomorrow", "Every 21 days", "Balcony", "Full sun.")
)
