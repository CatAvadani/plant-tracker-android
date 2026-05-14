package com.catalina.planttracker.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.catalina.planttracker.ui.components.PlantCard
import com.catalina.planttracker.ui.components.SummaryCard
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
                            color = Color(0xFF1B5E20)
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddPlantClick,
                containerColor = Color(0xFF2E7D32),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Plant")
            }
        },
        containerColor = Color(0xFFF1F8E9)
    ) { innerPadding ->
        when (val state = uiState) {
            is PlantUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF2E7D32))
                }
            }
            is PlantUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.message, color = Color.Red)
                }
            }
            is PlantUiState.Success -> {
                val plants = state.plants
                val totalPlants = plants.size
                val needsAttentionPlants = plants.filter { it.healthStatus == 1 || it.healthStatus == 2 }
                val healthyCount = plants.count { it.healthStatus == 0 }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        SummarySection(
                            total = totalPlants.toString(),
                            needsAttention = needsAttentionPlants.size.toString(),
                            healthy = healthyCount.toString()
                        )
                    }

                    item {
                        Text(
                            text = "Needs Attention",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            ),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    if (needsAttentionPlants.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Text(
                                    text = "All plants are happy today!",
                                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.Gray
                                )
                            }
                        }
                    } else {
                        items(needsAttentionPlants) { plant ->
                            PlantCard(plant, onClick = { onPlantClick(plant.id) })
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
            else -> {}
        }
    }
}

@Composable
fun SummarySection(total: String, needsAttention: String, healthy: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SummaryCard(label = "Total Plants", value = total, modifier = Modifier.weight(1f), containerColor = Color(0xFFC8E6C9))
        SummaryCard(label = "Needs Attention", value = needsAttention, modifier = Modifier.weight(1f), containerColor = Color(0xFFFFF9C4))
        SummaryCard(label = "Healthy", value = healthy, modifier = Modifier.weight(1f), containerColor = Color(0xFFDCEDC8))
    }
}
