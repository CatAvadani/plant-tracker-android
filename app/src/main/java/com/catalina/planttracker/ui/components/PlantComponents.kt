package com.catalina.planttracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.catalina.planttracker.model.Plant

val PlantBackground = Color(0xFFF1F8E9)
val PlantLeaf = Color(0xFF2E7D32)
val PlantDeepLeaf = Color(0xFF1B5E20)
val PlantMint = Color(0xFFE8F5E9)
val PlantCream = Color(0xFFFFFCF3)
val PlantInk = Color(0xFF27312B)
val PlantMuted = Color(0xFF748174)
val PlantGold = Color(0xFFC49000)
val PlantRed = Color(0xFFB71C1C)
val PlantLine = Color(0xFFC5D2C0)

@Composable
fun SummaryCard(label: String, value: String, modifier: Modifier = Modifier, containerColor: Color) {
    StatTile(
        label = label,
        value = value,
        icon = Icons.Default.LocalFlorist,
        modifier = modifier,
        containerColor = containerColor
    )
}

@Composable
fun StatTile(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.White,
    contentColor: Color = PlantDeepLeaf
) {
    Card(
        modifier = modifier
            .height(104.dp)
            .shadow(6.dp, RoundedCornerShape(24.dp), ambientColor = PlantLeaf.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                tint = contentColor.copy(alpha = 0.75f)
            )
            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium.copy(color = PlantMuted),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun PlantCard(
    plant: Plant,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(28.dp), ambientColor = PlantLeaf.copy(alpha = 0.1f))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PlantImage(plant = plant)
            Spacer(modifier = Modifier.width(14.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = plant.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PlantInk
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = plant.species ?: "Unknown species",
                        style = MaterialTheme.typography.bodyMedium.copy(color = PlantMuted),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                PlantStatusChip(healthStatus = plant.healthStatus)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(
                modifier = Modifier.widthIn(max = 112.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                InfoPill(
                    icon = Icons.Default.Place,
                    text = plant.location ?: "Unknown"
                )
                plant.wateringFrequencyDays?.let { days ->
                    InfoPill(
                        icon = Icons.Default.Schedule,
                        text = "${days}d"
                    )
                }
            }
        }
    }
}

@Composable
fun PlantStatusChip(healthStatus: Int?, modifier: Modifier = Modifier) {
    val color = plantStatusColor(healthStatus)
    val backgroundColor = when (healthStatus) {
        0 -> PlantMint
        1 -> Color(0xFFFFF2B8)
        2 -> Color(0xFFFFE2DE)
        else -> PlantMint
    }
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                imageVector = Icons.Default.WaterDrop,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = color
            )
            Text(
                text = plantStatusLabel(healthStatus),
                modifier = Modifier.widthIn(max = 126.dp),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = color
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ScreenStateCard(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.Eco,
    isLoading: Boolean = false,
    isError: Boolean = false
) {
    if (isLoading) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .background(Color.White.copy(alpha = 0.72f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = PlantLeaf,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(36.dp)
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PlantInk
                )
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium.copy(color = PlantMuted)
            )
        }
        return
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .background(
                        if (isError) MaterialTheme.colorScheme.errorContainer else PlantMint,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isError) Icons.Default.ErrorOutline else icon,
                    contentDescription = null,
                    modifier = Modifier.size(30.dp),
                    tint = if (isError) MaterialTheme.colorScheme.error else PlantLeaf
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PlantInk
                )
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium.copy(color = PlantMuted)
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    trailing: String? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = PlantDeepLeaf
                )
            )
            subtitle?.let {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium.copy(color = PlantMuted)
                )
            }
        }
        trailing?.let {
            Surface(
                shape = RoundedCornerShape(50.dp),
                color = PlantMint
            ) {
                Text(
                    text = it,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = PlantLeaf,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

@Composable
private fun PlantImage(plant: Plant) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFDDF0D8), Color(0xFFF6F9ED))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        if (!plant.imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = plant.imageUrl,
                contentDescription = plant.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(22.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Default.Eco,
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = PlantLeaf
            )
        }
    }
}

@Composable
private fun InfoPill(icon: ImageVector, text: String) {
    Surface(
        modifier = Modifier.widthIn(max = 112.dp),
        shape = RoundedCornerShape(50.dp),
        color = PlantCream
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(13.dp),
                tint = PlantMuted
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = PlantInk,
                    fontWeight = FontWeight.Medium
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

fun plantStatusColor(healthStatus: Int?): Color {
    return when (healthStatus) {
        0 -> PlantLeaf
        1 -> PlantGold
        2 -> PlantRed
        else -> PlantMuted
    }
}

fun plantStatusLabel(healthStatus: Int?): String {
    return when (healthStatus) {
        0 -> "Healthy"
        1 -> "Needs attention"
        2 -> "Critical"
        else -> "Unknown"
    }
}
