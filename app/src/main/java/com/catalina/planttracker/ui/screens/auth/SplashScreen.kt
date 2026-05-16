package com.catalina.planttracker.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.catalina.planttracker.data.local.TokenManager
import com.catalina.planttracker.navigation.Screen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavigate: (String) -> Unit) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }

    LaunchedEffect(Unit) {
        delay(1000)
        if (tokenManager.getToken() != null) {
            onNavigate(Screen.Home.route)
        } else {
            onNavigate(Screen.Login.route)
        }
    }

    AuthBackground {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AuthBrandHeader()
                Spacer(modifier = Modifier.height(34.dp))
                CircularProgressIndicator(
                    modifier = Modifier.size(34.dp),
                    color = AuthLeaf,
                    strokeWidth = 3.dp
                )
                Spacer(modifier = Modifier.height(18.dp))
                Icon(
                    imageVector = Icons.Default.Eco,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = AuthSage
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Checking your garden",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = AuthInk.copy(alpha = 0.68f)
                    )
                )
            }
        }
    }
}
