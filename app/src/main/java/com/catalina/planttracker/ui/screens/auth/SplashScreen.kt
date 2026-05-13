package com.catalina.planttracker.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.catalina.planttracker.data.local.TokenManager
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavigate: (String) -> Unit) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }

    LaunchedEffect(Unit) {
        delay(1000)
        if (tokenManager.getToken() != null) {
            onNavigate("home")
        } else {
            onNavigate("login")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F8E9)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.LocalFlorist,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = Color(0xFF2E7D32)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Plant Tracker",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20)
                )
            )
        }
    }
}
