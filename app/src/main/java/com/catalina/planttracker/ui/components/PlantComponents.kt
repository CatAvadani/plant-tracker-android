package com.catalina.planttracker.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.catalina.planttracker.model.HealthStatus
import com.catalina.planttracker.model.Plant

@Composable
fun SummaryCard(label: String, value: String, modifier: Modifier = Modifier, containerColor: Color) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
    }
}

@Composable
fun PlantCard(plant: Plant, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = plant.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = plant.species ?: "Unknown Species",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                val healthInt = plant.healthStatus
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val statusColor = when(healthInt) {
                        0 -> Color(0xFF2E7D32) // Darker Green for better contrast
                        1 -> Color(0xFFC49000) // Darker Yellow/Gold
                        2 -> Color(0xFFB71C1C) // Darker Red
                        else -> Color.Gray
                    }
                    val statusText = when(healthInt) {
                        0 -> "Healthy"
                        1 -> "Needs Attention"
                        2 -> "Critical"
                        else -> "Unknown"
                    }
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelMedium,
                        color = statusColor
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Location",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
                Text(
                    text = plant.location ?: "Unknown",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = Color(0xFF2E7D32)
                )
            }
        }
    }
}
