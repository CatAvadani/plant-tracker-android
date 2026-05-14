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
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.catalina.planttracker.ui.components.StatTile
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
                shape = RoundedCornerShape(20.dp)
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

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    item {
                        HomeHeroCard(
                            plants = plants,
                            needsAttentionCount = needsAttentionPlants.size,
                            healthyCount = healthyCount
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            StatTile(
                                label = "Total",
                                value = plants.size.toString(),
                                icon = Icons.Default.Eco,
                                modifier = Modifier.weight(1f),
                                containerColor = Color(0xFFDDF0D8)
                            )
                            StatTile(
                                label = "Care",
                                value = needsAttentionPlants.size.toString(),
                                icon = Icons.Default.WarningAmber,
                                modifier = Modifier.weight(1f),
                                containerColor = Color(0xFFFFF3BF),
                                contentColor = Color(0xFF8A6500)
                            )
                            StatTile(
                                label = "Healthy",
                                value = healthyCount.toString(),
                                icon = Icons.Default.CheckCircle,
                                modifier = Modifier.weight(1f),
                                containerColor = Color(0xFFE6F3D7)
                            )
                        }
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
                            ScreenStateCard(
                                title = "All clear today",
                                message = "Every plant is marked healthy. Enjoy the quiet garden.",
                                icon = Icons.Default.CheckCircle
                            )
                        }
                    } else {
                        items(needsAttentionPlants) { plant ->
                            PlantCard(plant, onClick = { onPlantClick(plant.id) })
                        }
                    }

                    if (plants.isNotEmpty()) {
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

@Composable
private fun HomeHeroCard(
    plants: List<Plant>,
    needsAttentionCount: Int,
    healthyCount: Int
) {
    val heroTitle = when {
        plants.isEmpty() -> "Start your plant shelf"
        needsAttentionCount > 0 -> "$needsAttentionCount plant${if (needsAttentionCount == 1) "" else "s"} need care"
        else -> "Your garden is steady"
    }
    val heroSubtitle = when {
        plants.isEmpty() -> "Add your first plant and build a care rhythm that is easy to follow."
        needsAttentionCount > 0 -> "Check the care queue below before adding new plants."
        else -> "$healthyCount healthy plants are on track today."
    }

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
                        listOf(PlantCream, Color.White, Color(0xFFDFF0D8))
                    )
                )
                .padding(22.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
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
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = null,
                            tint = PlantLeaf,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "${plants.size} tracked",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = PlantLeaf,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = heroTitle,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = PlantInk
                        )
                    )
                    Text(
                        text = heroSubtitle,
                        style = MaterialTheme.typography.bodyMedium.copy(color = PlantMuted)
                    )
                }
            }
        }
    }
}
