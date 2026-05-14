package com.catalina.planttracker.ui.screens.auth

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(context))

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val authState by viewModel.authState.collectAsStateWithLifecycle()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onRegisterSuccess()
            viewModel.resetState()
        }
    }

    AuthScreenFrame(
        title = "Create account",
        subtitle = "Start tracking your plants and keep their care schedule in one place.",
        footer = {
            TextButton(onClick = onNavigateToLogin) {
                Text("Already have an account? Login", color = AuthLeaf)
            }
        }
    ) {
        Spacer(modifier = Modifier.height(4.dp))
        AuthField(
            value = name,
            onValueChange = { name = it },
            label = "Full Name",
            icon = Icons.Default.Person
        )
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
        AuthField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Confirm Password",
            icon = Icons.Default.Lock,
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            passwordVisible = confirmPasswordVisible,
            onPasswordVisibilityChange = { confirmPasswordVisible = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        if (authState is AuthState.Error) {
            AuthErrorMessage((authState as AuthState.Error).message)
        }

        Spacer(modifier = Modifier.height(4.dp))
        AuthPrimaryButton(
            text = "Register",
            loading = authState is AuthState.Loading,
            enabled = authState !is AuthState.Loading &&
                name.isNotBlank() &&
                email.isNotBlank() &&
                password.isNotBlank() &&
                password == confirmPassword,
            onClick = { 
                if (password == confirmPassword) {
                    viewModel.register(email, password, confirmPassword, name) 
                }
            },
        )
        AuthMetaRow("Passwords must match before registration.")
    }
}
