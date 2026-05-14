package com.catalina.planttracker.ui.screens.home

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.catalina.planttracker.model.Plant
import com.catalina.planttracker.ui.components.PlantBackground
import com.catalina.planttracker.ui.components.PlantCard
import com.catalina.planttracker.ui.components.PlantCream
import com.catalina.planttracker.ui.components.PlantDeepLeaf
import com.catalina.planttracker.ui.components.PlantInk
import com.catalina.planttracker.ui.components.PlantLeaf
import com.catalina.planttracker.ui.components.PlantMint
import com.catalina.planttracker.ui.components.PlantMuted
import com.catalina.planttracker.ui.components.ScreenStateCard
import com.catalina.planttracker.ui.components.SectionHeader
import com.catalina.planttracker.ui.plants.PlantUiState
import com.catalina.planttracker.ui.plants.PlantViewModel
import com.catalina.planttracker.ui.plants.PlantViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onPlantClick: (Int) -> Unit, onAddPlantClick: () -> Unit) {
    val viewModel: PlantViewModel = viewModel(factory = PlantViewModelFactory())
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadPlants()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "My Plants",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PlantDeepLeaf
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddPlantClick,
                containerColor = PlantLeaf,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Plant")
            }
        },
        containerColor = PlantBackground
    ) { innerPadding ->
        when (val state = uiState) {
            is PlantUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ScreenStateCard(
                        title = "Loading your garden",
                        message = "Fetching the latest plant care details.",
                        isLoading = true
                    )
                }
            }

            is PlantUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ScreenStateCard(
                        title = "Could not load plants",
                        message = state.message,
                        isError = true
                    )
                }
            }

            is PlantUiState.Success -> {
                val plants = state.plants
                val needsAttentionPlants = plants
                    .filter { it.healthStatus == 1 || it.healthStatus == 2 }
                    .sortedByDescending { it.healthStatus ?: 0 }
                val healthyCount = plants.count { it.healthStatus == 0 }
                val criticalCount = plants.count { it.healthStatus == 2 }

                if (plants.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(horizontal = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        EmptyHomeState()
                    }
                    return@Scaffold
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    item {
                        HomeDashboardPanel(
                            total = plants.size,
                            healthy = healthyCount,
                            needsCare = needsAttentionPlants.size,
                            critical = criticalCount
                        )
                    }

                    item {
                        SectionHeader(
                            title = "Care queue",
                            subtitle = "Plants that need attention first",
                            trailing = "${needsAttentionPlants.size} open"
                        )
                    }

                    if (needsAttentionPlants.isEmpty()) {
                        item {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                color = Color(0xFFE5F4E5)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = PlantLeaf,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "All clear — every plant is healthy",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = PlantDeepLeaf,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                            }
                        }
                    } else {
                        items(needsAttentionPlants) { plant ->
                            PlantCard(plant, onClick = { onPlantClick(plant.id) })
                        }
                    }

                    item {
                        SectionHeader(
                            title = "Recently added",
                            subtitle = "A quick path back into your collection",
                            trailing = "${plants.take(3).size} shown"
                        )
                    }
                    items(plants.sortedByDescending { it.id }.take(3)) { plant ->
                        PlantCard(plant, onClick = { onPlantClick(plant.id) })
                    }

                    item { Spacer(modifier = Modifier.height(86.dp)) }
                }
            }

            else -> {}
        }
    }
}

@Composable
private fun EmptyHomeState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 72.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Box(
            modifier = Modifier
                .size(104.dp)
                .background(Color.White.copy(alpha = 0.78f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(78.dp)
                    .background(PlantMint, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Eco,
                    contentDescription = null,
                    tint = PlantLeaf,
                    modifier = Modifier.size(42.dp)
                )
            }
        }
        Text(
            text = "No plants yet",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = PlantInk
            )
        )
        Text(
            text = "Tap the + button to add your first plant and start tracking care.",
            style = MaterialTheme.typography.bodyLarge.copy(color = PlantMuted),
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
private fun HomeDashboardPanel(
    total: Int,
    healthy: Int,
    needsCare: Int,
    critical: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(32.dp),
                ambientColor = Color.Transparent,
                spotColor = Color(0x66000000)
            ),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(Color.White, PlantCream, Color(0xFFE8F5E9))
                    )
                )
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Compact header: total count + live badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "$total",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                color = PlantDeepLeaf,
                                fontWeight = FontWeight.ExtraBold
                            )
                        )
                        Text(
                            text = "plant${if (total == 1) "" else "s"} in your collection",
                            style = MaterialTheme.typography.bodyMedium.copy(color = PlantMuted)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(50.dp),
                        color = Color(0xFFE5F4E5)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .background(PlantLeaf, CircleShape)
                            )
                            Text(
                                text = "Live",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = PlantLeaf,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricPill(
                        label = "Healthy",
                        value = healthy,
                        icon = Icons.Default.CheckCircle,
                        color = PlantLeaf,
                        containerColor = Color(0xFFDFF2DE),
                        modifier = Modifier.weight(1f)
                    )
                    MetricPill(
                        label = "Need care",
                        value = needsCare,
                        icon = Icons.Default.WarningAmber,
                        color = Color(0xFF8A6500),
                        containerColor = Color(0xFFFFF2B8),
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricPill(
                        label = "Critical",
                        value = critical,
                        icon = Icons.Default.WarningAmber,
                        color = Color(0xFFB71C1C),
                        containerColor = Color(0xFFFFE2DE),
                        modifier = Modifier.weight(1f)
                    )
                    MetricPill(
                        label = "Total",
                        value = total,
                        icon = Icons.Default.Eco,
                        color = PlantLeaf,
                        containerColor = PlantLeaf,
                        valueColor = Color.White,
                        labelColor = Color.White.copy(alpha = 0.82f),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}


@Composable
private fun MetricPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: Int,
    color: Color,
    containerColor: Color,
    valueColor: Color = PlantInk,
    labelColor: Color = PlantMuted,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = containerColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = valueColor,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = labelColor,
                    fontWeight = FontWeight.SemiBold
                ),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
