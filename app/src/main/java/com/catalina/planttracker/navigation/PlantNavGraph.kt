package com.catalina.planttracker.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.catalina.planttracker.data.local.TokenManager
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.catalina.planttracker.ui.components.BottomNavigationBar
import com.catalina.planttracker.ui.screens.auth.LoginScreen
import com.catalina.planttracker.ui.screens.auth.RegisterScreen
import com.catalina.planttracker.ui.screens.auth.SplashScreen
import com.catalina.planttracker.ui.screens.calendar.CalendarScreen
import com.catalina.planttracker.ui.screens.home.HomeScreen
import com.catalina.planttracker.ui.screens.plants.AddPlantScreen
import com.catalina.planttracker.ui.screens.plants.EditPlantScreen
import com.catalina.planttracker.ui.screens.plants.PlantDetailsScreen
import com.catalina.planttracker.ui.screens.plants.PlantsScreen
import com.catalina.planttracker.ui.screens.settings.SettingsScreen

@Composable
fun PlantNavGraph(navController: NavHostController = rememberNavController()) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    LaunchedEffect(Unit) {
        TokenManager.sessionExpired.collect {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    // Define which screens should show the bottom bar
    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Plants.route,
        Screen.Calendar.route,
        Screen.Settings.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Auth Graph
            composable(Screen.Splash.route) {
                SplashScreen(onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                })
            }
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route)
                    }
                )
            }
            composable(Screen.Register.route) {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.popBackStack(Screen.Login.route, inclusive = false)
                    },
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route)
                    }
                )
            }

            // Main App Graph
            composable(Screen.Home.route) { 
                HomeScreen(
                    onPlantClick = { id -> navController.navigate("plant_details/$id") },
                    onAddPlantClick = { navController.navigate(Screen.AddPlant.route) }
                ) 
            }
            composable(Screen.Plants.route) { 
                PlantsScreen(
                    onPlantClick = { id -> navController.navigate("plant_details/$id") },
                    onAddPlantClick = { navController.navigate(Screen.AddPlant.route) }
                ) 
            }
            composable(Screen.Calendar.route) { CalendarScreen() }
            composable(Screen.Settings.route) {
                SettingsScreen(onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                })
            }

            // Secondary Screens
            composable(
                route = Screen.PlantDetails.route,
                arguments = listOf(navArgument("plantId") { type = NavType.StringType })
            ) { backStackEntry ->
                val plantIdStr = backStackEntry.arguments?.getString("plantId") ?: return@composable
                val plantId = plantIdStr.toIntOrNull() ?: return@composable

                PlantDetailsScreen(
                    plantId = plantId,
                    onBack = { navController.popBackStack() },
                    onNavigateToEdit = { id -> navController.navigate("edit_plant/$id") }
                )
            }

            composable(
                route = Screen.EditPlant.route,
                arguments = listOf(navArgument("plantId") { type = NavType.StringType })
            ) { backStackEntry ->
                val plantId = backStackEntry.arguments?.getString("plantId") ?: return@composable
                EditPlantScreen(
                    plantId = plantId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.AddPlant.route) {
                AddPlantScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
