package com.catalina.planttracker.model

data class Plant(
    val name: String,
    val species: String,
    val status: String,
    val nextWatering: String
)

val fakePlants = listOf(
    Plant("Fiddle Leaf Fig", "Ficus lyrata", "Healthy", "In 2 days"),
    Plant("Snake Plant", "Dracaena trifasciata", "Needs Water", "Today"),
    Plant("Monstera Deliciosa", "Monstera deliciosa", "Healthy", "In 5 days"),
    Plant("Pothos", "Epipremnum aureum", "Healthy", "In 3 days"),
    Plant("Aloe Vera", "Aloe barbadensis miller", "Needs Water", "Tomorrow")
)
