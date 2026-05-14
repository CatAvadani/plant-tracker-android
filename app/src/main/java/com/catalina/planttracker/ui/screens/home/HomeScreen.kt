package com.catalina.planttracker.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.catalina.planttracker.model.fakePlants
import com.catalina.planttracker.ui.components.PlantCard
import com.catalina.planttracker.ui.components.SummaryCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onPlantClick: (Int) -> Unit, onAddPlantClick: () -> Unit) {
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SummarySection()
            }

            item {
                Text(
                    text = "Plant Collection",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    ),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(fakePlants) { plant ->
                PlantCard(plant, onClick = { onPlantClick(plant.id) })
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun SummarySection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SummaryCard(label = "Total Plants", value = "12", modifier = Modifier.weight(1f), containerColor = Color(0xFFC8E6C9))
        SummaryCard(label = "Needs Water", value = "2", modifier = Modifier.weight(1f), containerColor = Color(0xFFFFF9C4))
        SummaryCard(label = "Healthy", value = "10", modifier = Modifier.weight(1f), containerColor = Color(0xFFDCEDC8))
    }
}
