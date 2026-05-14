package com.catalina.planttracker.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String = "", val icon: ImageVector? = null) {
    // Auth Screens
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")

    // Main Screens
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Plants : Screen("plants", "Plants", Icons.Default.Eco)
    object Calendar : Screen("calendar", "Calendar", Icons.Default.CalendarMonth)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)

    // Secondary Screens
    object PlantDetails : Screen("plant_details/{plantId}", "Plant Details")
    object AddPlant : Screen("add_plant", "Add Plant")
    object EditPlant : Screen("edit_plant/{plantId}", "Edit Plant")
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Plants,
    Screen.Calendar,
    Screen.Settings
)
