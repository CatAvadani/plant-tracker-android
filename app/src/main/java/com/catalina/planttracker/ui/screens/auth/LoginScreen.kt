package com.catalina.planttracker.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.catalina.planttracker.ui.auth.AuthState
import com.catalina.planttracker.ui.auth.AuthViewModel
import com.catalina.planttracker.ui.auth.AuthViewModelFactory
import kotlinx.coroutines.launch

private enum class AuthTab { Login, Register }

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    val context = LocalContext.current
    val viewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(context))
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var selectedTab by remember { mutableStateOf(AuthTab.Login) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val authState by viewModel.authState.collectAsStateWithLifecycle()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            if (selectedTab == AuthTab.Login) {
                onLoginSuccess()
                viewModel.resetState()
            } else {
                viewModel.resetState()
                selectedTab = AuthTab.Login
                password = ""
                confirmPassword = ""
                displayName = ""
                snackbarHostState.showSnackbar("Account created! Please log in.")
            }
        }
    }

    AuthBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 4.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(28.dp))
                AuthBrandHeader(compact = true)
                Spacer(modifier = Modifier.height(28.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        val title = if (selectedTab == AuthTab.Login) {
                            "Welcome back"
                        } else {
                            "Create account"
                        }
                        val subtitle = if (selectedTab == AuthTab.Login) {
                            "Sign in to check watering plans, plant health, and upcoming care."
                        } else {
                            "Start tracking your plants and keep their care schedule in one place."
                        }

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AuthDeepLeaf
                                ),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = AuthInk.copy(alpha = 0.68f)
                                ),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }

                        AuthTabSwitcher(
                            selectedTab = selectedTab,
                            onTabSelected = {
                                selectedTab = it
                                viewModel.resetState()
                            }
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        if (selectedTab == AuthTab.Register) {
                            AuthField(
                                value = displayName,
                                onValueChange = { displayName = it },
                                label = "Display Name",
                                icon = Icons.Default.Person
                            )
                        }

                        AuthField(
                            value = email,
                            onValueChange = { email = it },
                            label = "Email Address",
                            icon = Icons.Default.Email,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                        )

                        AuthField(
                            value = password,
                            onValueChange = { password = it },
                            label = "Password",
                            icon = Icons.Default.Lock,
                            visualTransformation = if (passwordVisible) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                            passwordVisible = passwordVisible,
                            onPasswordVisibilityChange = { passwordVisible = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                        )

                        if (selectedTab == AuthTab.Register) {
                            AuthField(
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it },
                                label = "Confirm Password",
                                icon = Icons.Default.Lock,
                                visualTransformation = if (confirmPasswordVisible) {
                                    VisualTransformation.None
                                } else {
                                    PasswordVisualTransformation()
                                },
                                passwordVisible = confirmPasswordVisible,
                                onPasswordVisibilityChange = { confirmPasswordVisible = it },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                            )
                        }

                        if (selectedTab == AuthTab.Login) {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                TextButton(
                                    onClick = {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Coming soon")
                                        }
                                    }
                                ) {
                                    Text(
                                        "Forgot password?",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            color = AuthLeaf,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                            }
                        }

                        if (authState is AuthState.Error) {
                            AuthErrorMessage((authState as AuthState.Error).message)
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        val buttonText = if (selectedTab == AuthTab.Login) "Login" else "Register"
                        val enabled = when (selectedTab) {
                            AuthTab.Login -> authState !is AuthState.Loading &&
                                email.isNotBlank() &&
                                password.isNotBlank()

                            AuthTab.Register -> authState !is AuthState.Loading &&
                                displayName.isNotBlank() &&
                                email.isNotBlank() &&
                                password.isNotBlank() &&
                                password == confirmPassword
                        }

                        AuthPrimaryButton(
                            text = buttonText,
                            loading = authState is AuthState.Loading,
                            enabled = enabled,
                            onClick = {
                                when (selectedTab) {
                                    AuthTab.Login -> viewModel.login(email, password)
                                    AuthTab.Register -> viewModel.register(
                                        email,
                                        password,
                                        confirmPassword,
                                        displayName
                                    )
                                }
                            }
                        )

                        val metaText = if (selectedTab == AuthTab.Login) {
                            "Your plant journal is synced securely."
                        } else {
                            "Passwords must match before registration."
                        }
                        AuthMetaRow(metaText)
                    }

                Spacer(modifier = Modifier.height(24.dp))
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun AuthTabSwitcher(
    selectedTab: AuthTab,
    onTabSelected: (AuthTab) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(50.dp),
        color = Color(0xFFDFECDA)
    ) {
        Row(modifier = Modifier.padding(4.dp)) {
            AuthTabButton(
                text = "Login",
                selected = selectedTab == AuthTab.Login,
                onClick = { onTabSelected(AuthTab.Login) },
                modifier = Modifier.weight(1f)
            )
            AuthTabButton(
                text = "Register",
                selected = selectedTab == AuthTab.Register,
                onClick = { onTabSelected(AuthTab.Register) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun AuthTabButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50.dp),
        color = if (selected) Color.White else Color.Transparent,
        shadowElevation = if (selected) 2.dp else 0.dp,
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    color = if (selected) AuthDeepLeaf else AuthInk.copy(alpha = 0.5f)
                )
            )
        }
    }
}
