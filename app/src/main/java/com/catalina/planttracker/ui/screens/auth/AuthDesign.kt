package com.catalina.planttracker.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

internal val AuthLeaf = Color(0xFF2E7D32)
internal val AuthDeepLeaf = Color(0xFF1B5E20)
internal val AuthMint = Color(0xFFE8F5E9)
internal val AuthCream = Color(0xFFFFFCF3)
internal val AuthSage = Color(0xFF7BA77D)
internal val AuthInk = Color(0xFF17351F)

@Composable
internal fun AuthBackground(content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(AuthCream, AuthMint, Color(0xFFDDEFD6))
                )
            )
    ) {
        DecorativeCircle(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 44.dp, end = 24.dp),
            size = 150.dp,
            color = Color(0xFFB8DDB4)
        )
        DecorativeCircle(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, bottom = 96.dp),
            size = 210.dp,
            color = Color(0xFFF2DFA6)
        )
        DecorativeCircle(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 48.dp),
            size = 64.dp,
            color = Color(0xFFA7C7A4)
        )
        content()
    }
}

@Composable
private fun DecorativeCircle(
    modifier: Modifier,
    size: androidx.compose.ui.unit.Dp,
    color: Color
) {
    Box(
        modifier = modifier
            .size(size)
            .background(color.copy(alpha = 0.42f), CircleShape)
    )
}

@Composable
internal fun AuthScreenFrame(
    title: String,
    subtitle: String,
    footer: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    AuthBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(28.dp))
            AuthBrandHeader(compact = true)
            Spacer(modifier = Modifier.height(28.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(16.dp, RoundedCornerShape(30.dp), ambientColor = AuthLeaf.copy(alpha = 0.14f)),
                shape = RoundedCornerShape(30.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.94f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = AuthDeepLeaf
                            )
                        )
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodyMedium.copy(color = AuthInk.copy(alpha = 0.68f))
                        )
                    }
                    content()
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            footer()
        }
    }
}

@Composable
internal fun AuthBrandHeader(compact: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        PlantCareMark(compact = compact)
        Spacer(modifier = Modifier.height(if (compact) 10.dp else 18.dp))
        Text(
            text = "Plant Tracker",
            style = if (compact) {
                MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = AuthDeepLeaf)
            } else {
                MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold, color = AuthDeepLeaf)
            }
        )
        if (!compact) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "A calm place to care for every plant.",
                style = MaterialTheme.typography.bodyLarge.copy(color = AuthInk.copy(alpha = 0.7f)),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun PlantCareMark(compact: Boolean) {
    val containerSize = if (compact) 74.dp else 116.dp
    val leafSize = if (compact) 40.dp else 64.dp
    val dropContainerSize = if (compact) 25.dp else 34.dp
    val dropSize = if (compact) 14.dp else 18.dp

    Box(
        modifier = Modifier.size(containerSize),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier.size(containerSize),
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.88f),
            shadowElevation = if (compact) 8.dp else 14.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Surface(
                    modifier = Modifier.size(if (compact) 54.dp else 84.dp),
                    shape = RoundedCornerShape(if (compact) 20.dp else 30.dp),
                    color = AuthMint
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Eco,
                            contentDescription = null,
                            modifier = Modifier.size(leafSize),
                            tint = AuthLeaf
                        )
                    }
                }
            }
        }
        Surface(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-4).dp, y = (-4).dp)
                .size(dropContainerSize),
            shape = CircleShape,
            color = Color(0xFFF2DFA6),
            shadowElevation = 3.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.WaterDrop,
                    contentDescription = null,
                    modifier = Modifier.size(dropSize),
                    tint = AuthDeepLeaf
                )
            }
        }
    }
}

@Composable
internal fun AuthField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    passwordVisible: Boolean? = null,
    onPasswordVisibilityChange: ((Boolean) -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AuthSage
            )
        },
        trailingIcon = {
            if (passwordVisible != null && onPasswordVisibilityChange != null) {
                IconButton(onClick = { onPasswordVisibilityChange(!passwordVisible) }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        tint = AuthSage
                    )
                }
            }
        },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        singleLine = true,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AuthLeaf,
            unfocusedBorderColor = Color(0xFFD1DFC8),
            focusedLabelColor = AuthLeaf,
            cursorColor = AuthLeaf,
            focusedContainerColor = Color(0xFFFAFFF8),
            unfocusedContainerColor = Color(0xFFFAFFF8)
        )
    )
}

@Composable
internal fun AuthPrimaryButton(
    text: String,
    loading: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AuthLeaf,
            disabledContainerColor = Color(0xFFB7CDB1),
            contentColor = Color.White,
            disabledContentColor = Color.White.copy(alpha = 0.78f)
        ),
        enabled = enabled
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Text(text, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
internal fun AuthErrorMessage(message: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.errorContainer
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            color = MaterialTheme.colorScheme.onErrorContainer,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
internal fun AuthMetaRow(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                color = AuthInk.copy(alpha = 0.58f),
                fontWeight = FontWeight.Medium
            )
        )
    }
}
