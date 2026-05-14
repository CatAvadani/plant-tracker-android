package com.catalina.planttracker.ui.screens.auth

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.catalina.planttracker.ui.auth.AuthState
import com.catalina.planttracker.ui.auth.AuthViewModel
import com.catalina.planttracker.ui.auth.AuthViewModelFactory

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(context))

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val authState by viewModel.authState.collectAsStateWithLifecycle()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onLoginSuccess()
            viewModel.resetState()
        }
    }

    AuthScreenFrame(
        title = "Welcome back",
        subtitle = "Sign in to check watering plans, plant health, and upcoming care.",
        footer = {
            TextButton(onClick = onNavigateToRegister) {
                Text("Create a new account", color = AuthLeaf)
            }
        }
    ) {
        Spacer(modifier = Modifier.height(4.dp))
        AuthField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            icon = Icons.Default.Email,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        AuthField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            icon = Icons.Default.Lock,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            passwordVisible = passwordVisible,
            onPasswordVisibilityChange = { passwordVisible = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        if (authState is AuthState.Error) {
            AuthErrorMessage((authState as AuthState.Error).message)
        }

        Spacer(modifier = Modifier.height(4.dp))
        AuthPrimaryButton(
            text = "Login",
            loading = authState is AuthState.Loading,
            enabled = authState !is AuthState.Loading &&
                email.isNotBlank() &&
                password.isNotBlank(),
            onClick = { viewModel.login(email, password) },
        )
        AuthMetaRow("Your plant journal is synced securely.")
    }
}
