package com.catalina.planttracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.catalina.planttracker.data.network.RetrofitInstance
import com.catalina.planttracker.navigation.PlantNavGraph
import com.catalina.planttracker.ui.theme.PlantTrackerAndroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Retrofit
        RetrofitInstance.getRetrofit(this)

        enableEdgeToEdge()
        setContent {
            PlantTrackerAndroidTheme {
                PlantNavGraph()
            }
        }
    }
}
