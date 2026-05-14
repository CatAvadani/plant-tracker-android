package com.catalina.planttracker.ui.screens.plants

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.catalina.planttracker.model.Plant
import com.catalina.planttracker.ui.components.PlantBackground
import com.catalina.planttracker.ui.components.PlantCream
import com.catalina.planttracker.ui.components.PlantDeepLeaf
import com.catalina.planttracker.ui.components.PlantInk
import com.catalina.planttracker.ui.components.PlantLeaf
import com.catalina.planttracker.ui.components.PlantMint
import com.catalina.planttracker.ui.components.PlantMuted
import com.catalina.planttracker.ui.components.PlantStatusChip
import com.catalina.planttracker.ui.components.ScreenStateCard
import com.catalina.planttracker.ui.components.SectionHeader
import com.catalina.planttracker.ui.components.plantStatusColor
import com.catalina.planttracker.ui.plants.PlantUiState
import com.catalina.planttracker.ui.plants.PlantViewModel
import com.catalina.planttracker.ui.plants.PlantViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantDetailsScreen(
    plantId: Int,
    onBack: () -> Unit,
    onNavigateToEdit: (Int) -> Unit
) {
    val viewModel: PlantViewModel = viewModel(factory = PlantViewModelFactory())
    val selectedPlant by viewModel.selectedPlant.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var isDeleting by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(plantId) {
        viewModel.loadPlant(plantId)
    }

    LaunchedEffect(uiState) {
        if (isDeleting && uiState is PlantUiState.Success) {
            onBack()
            viewModel.resetState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        selectedPlant?.name ?: "Plant Details",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = PlantDeepLeaf
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = PlantInk
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToEdit(plantId) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = PlantInk)
                    }
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        enabled = uiState !is PlantUiState.Loading
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = PlantInk)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = PlantBackground
    ) { innerPadding ->
        val plant = selectedPlant

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState is PlantUiState.Error && plant == null -> {
                    ScreenStateCard(
                        title = "Could not load plant",
                        message = (uiState as PlantUiState.Error).message,
                        isError = true,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(20.dp)
                    )
                }

                plant == null || (uiState is PlantUiState.Loading && !isDeleting) -> {
                    ScreenStateCard(
                        title = "Loading plant",
                        message = "Fetching care details and notes.",
                        isLoading = true,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(20.dp)
                    )
                }

                else -> {
                    PlantDetailsContent(
                        plant = plant,
                        errorMessage = (uiState as? PlantUiState.Error)?.message,
                        deleting = isDeleting,
                        onEdit = { onNavigateToEdit(plantId) },
                        onRequestDelete = { showDeleteDialog = true }
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        DeletePlantDialog(
            plantName = selectedPlant?.name ?: "this plant",
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                showDeleteDialog = false
                isDeleting = true
                viewModel.deletePlant(plantId)
            }
        )
    }
}

@Composable
private fun PlantDetailsContent(
    plant: Plant,
    errorMessage: String?,
    deleting: Boolean,
    onEdit: () -> Unit,
    onRequestDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        PlantHero(plant)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DetailStatCard(
                label = "Water",
                value = plant.wateringFrequencyDays?.let { "${it}d" } ?: "-",
                icon = Icons.Default.WaterDrop,
                modifier = Modifier.weight(1f)
            )
            DetailStatCard(
                label = "Health",
                value = when (plant.healthStatus) {
                    0 -> "Good"
                    1 -> "Care"
                    2 -> "Critical"
                    else -> "-"
                },
                icon = Icons.Default.Eco,
                modifier = Modifier.weight(1f),
                tint = plantStatusColor(plant.healthStatus)
            )
            DetailStatCard(
                label = "Last",
                value = formatDate(plant.lastWatered),
                icon = Icons.Default.EventRepeat,
                modifier = Modifier.weight(1f)
            )
        }

        SectionHeader(
            title = "Care details",
            subtitle = "The basics for this plant"
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DetailRow(Icons.Default.LocalFlorist, "Species", plant.species ?: "Unknown")
                DetailRow(Icons.Default.Place, "Location", plant.location ?: "Unknown")
                DetailRow(
                    Icons.Default.EventRepeat,
                    "Watering frequency",
                    plant.wateringFrequencyDays?.let { "Every $it days" } ?: "Not set"
                )
                DetailRow(Icons.Default.WaterDrop, "Last watered", formatFullDate(plant.lastWatered))
            }
        }

        SectionHeader(
            title = "Notes",
            subtitle = "Care observations and reminders"
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(PlantMint, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Notes,
                        contentDescription = null,
                        tint = PlantLeaf,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = plant.notes ?: "No notes available.",
                    style = MaterialTheme.typography.bodyLarge.copy(color = PlantMuted),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (errorMessage != null) {
            ScreenStateCard(
                title = if (deleting) "Could not delete plant" else "Plant action failed",
                message = errorMessage,
                isError = true
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onEdit,
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp),
                shape = RoundedCornerShape(18.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Edit")
            }
            Button(
                onClick = onRequestDelete,
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                shape = RoundedCornerShape(18.dp),
                enabled = !deleting
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (deleting) "Deleting" else "Delete")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun PlantHero(plant: Plant) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(14.dp, RoundedCornerShape(34.dp), ambientColor = PlantLeaf.copy(alpha = 0.14f)),
        shape = RoundedCornerShape(34.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(290.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color.White, PlantCream, Color(0xFFDDEFD6))
                    )
                )
        ) {
            if (!plant.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = plant.imageUrl,
                    contentDescription = plant.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(34.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .background(PlantMint, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Eco,
                            contentDescription = null,
                            modifier = Modifier.size(62.dp),
                            tint = PlantLeaf
                        )
                    }
                }
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(18.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color.White.copy(alpha = 0.92f)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    Text(
                        text = plant.name,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = PlantInk
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    PlantStatusChip(healthStatus = plant.healthStatus)
                }
            }
        }
    }
}

@Composable
private fun DetailStatCard(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    tint: Color = PlantLeaf
) {
    Card(
        modifier = modifier.height(104.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = PlantInk,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium.copy(color = PlantMuted)
                )
            }
        }
    }
}

@Composable
private fun DetailRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(PlantMint, RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = PlantLeaf, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(color = PlantMuted)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = PlantInk
                )
            )
        }
    }
}

@Composable
private fun DeletePlantDialog(
    plantName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete plant?") },
        text = { Text("This will permanently remove $plantName from your collection.") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun formatDate(value: String?): String {
    if (value.isNullOrBlank()) return "Never"
    return value.substringBefore("T").substringAfterLast("-")
}

private fun formatFullDate(value: String?): String {
    if (value.isNullOrBlank()) return "Never"
    return value.substringBefore("T")
}
