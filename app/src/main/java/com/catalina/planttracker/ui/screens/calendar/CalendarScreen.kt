package com.catalina.planttracker.ui.screens.calendar

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.catalina.planttracker.model.Plant
import com.catalina.planttracker.ui.components.PlantBackground
import com.catalina.planttracker.ui.components.PlantCream
import com.catalina.planttracker.ui.components.PlantDeepLeaf
import com.catalina.planttracker.ui.components.PlantInk
import com.catalina.planttracker.ui.components.PlantLeaf
import com.catalina.planttracker.ui.components.PlantMint
import com.catalina.planttracker.ui.components.PlantMuted
import com.catalina.planttracker.ui.components.ScreenStateCard
import com.catalina.planttracker.ui.components.SectionHeader
import com.catalina.planttracker.ui.components.plantStatusColor
import com.catalina.planttracker.ui.components.plantStatusLabel
import com.catalina.planttracker.ui.plants.PlantUiState
import com.catalina.planttracker.ui.plants.PlantViewModel
import com.catalina.planttracker.ui.plants.PlantViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: PlantViewModel = viewModel(factory = PlantViewModelFactory())
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadPlants()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Watering Schedule",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PlantDeepLeaf
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = PlantBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val currentState = state) {
                is PlantUiState.Loading -> {
                    ScreenStateCard(
                        title = "Loading care schedule",
                        message = "Preparing watering reminders from your plants.",
                        isLoading = true,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(20.dp)
                    )
                }

                is PlantUiState.Error -> {
                    ScreenStateCard(
                        title = "Could not load schedule",
                        message = currentState.message,
                        isError = true,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(20.dp)
                    )
                }

                is PlantUiState.Success -> {
                    val plants = currentState.plants
                    val careQueue = plants.sortedWith(
                        compareByDescending<Plant> { it.healthStatus ?: 0 }
                            .thenBy { it.wateringFrequencyDays ?: Int.MAX_VALUE }
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        item {
                            CalendarHeroCard(
                                plantCount = plants.size,
                                reminderCount = careQueue.count { it.wateringFrequencyDays != null }
                            )
                        }

                        item {
                            WeekStrip()
                        }

                        item {
                            SectionHeader(
                                title = "Upcoming care",
                                subtitle = "Sorted by health status and watering rhythm",
                                trailing = "${careQueue.size} plants"
                            )
                        }

                        if (careQueue.isEmpty()) {
                            item {
                                ScreenStateCard(
                                    title = "No reminders yet",
                                    message = "Add watering frequency to plants to build a care schedule.",
                                    icon = Icons.Default.CalendarMonth
                                )
                            }
                        } else {
                            items(careQueue) { plant ->
                                ReminderItem(plant)
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(86.dp))
                        }
                    }
                }

                else -> {}
            }
        }
    }
}

@Composable
private fun CalendarHeroCard(
    plantCount: Int,
    reminderCount: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(14.dp, RoundedCornerShape(32.dp), ambientColor = PlantLeaf.copy(alpha = 0.14f)),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(Color.White, PlantCream, Color(0xFFDDEFD6))
                    )
                )
                .padding(22.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .background(PlantMint, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = PlantLeaf,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Surface(
                        shape = RoundedCornerShape(50.dp),
                        color = Color.White.copy(alpha = 0.78f)
                    ) {
                        Text(
                            text = "$reminderCount reminders",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = PlantLeaf,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Care rhythm",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = PlantInk
                        )
                    )
                    Text(
                        text = if (plantCount == 0) {
                            "Add plants to start building a watering plan."
                        } else {
                            "A lightweight schedule for $plantCount plants, ready for real reminders in Phase 4."
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(color = PlantMuted)
                    )
                }
            }
        }
    }
}

@Composable
private fun WeekStrip() {
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = Color.White
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "This week",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PlantInk
                        )
                    )
                    Text(
                        text = "Visual placeholder until reminders are connected",
                        style = MaterialTheme.typography.bodySmall.copy(color = PlantMuted)
                    )
                }
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = PlantLeaf
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                days.forEachIndexed { index, day ->
                    val selected = index == 2
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(18.dp),
                        color = if (selected) PlantLeaf else PlantMint
                    ) {
                        Text(
                            text = day,
                            modifier = Modifier.padding(vertical = 12.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = if (selected) Color.White else PlantLeaf,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReminderItem(plant: Plant) {
    val statusColor = plantStatusColor(plant.healthStatus)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(28.dp), ambientColor = PlantLeaf.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(statusColor.copy(alpha = 0.13f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.WaterDrop,
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(25.dp)
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
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
                    text = careSummary(plant),
                    style = MaterialTheme.typography.bodyMedium.copy(color = PlantMuted),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = plantStatusLabel(plant.healthStatus),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = statusColor,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Reminder",
                    tint = PlantMuted
                )
            }
        }
    }
}

private fun careSummary(plant: Plant): String {
    val frequency = plant.wateringFrequencyDays?.let { "Every $it days" } ?: "No frequency set"
    val lastWatered = plant.lastWatered ?: "Never watered"
    return "$frequency | Last: $lastWatered"
}
