package com.catalina.planttracker.ui.screens.plants

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material.icons.filled.History
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import com.catalina.planttracker.ui.carelogs.CareLogUiState
import com.catalina.planttracker.ui.carelogs.CareLogViewModel
import com.catalina.planttracker.ui.carelogs.CareLogViewModelFactory
import com.catalina.planttracker.ui.components.PlantBackground
import com.catalina.planttracker.ui.components.PlantCream
import com.catalina.planttracker.ui.components.PlantDeepLeaf
import com.catalina.planttracker.ui.components.PlantGold
import com.catalina.planttracker.ui.components.PlantInk
import com.catalina.planttracker.ui.components.PlantLeaf
import com.catalina.planttracker.ui.components.PlantMint
import com.catalina.planttracker.ui.components.PlantMuted
import com.catalina.planttracker.ui.components.PlantRed
import com.catalina.planttracker.ui.components.PlantStatusChip
import com.catalina.planttracker.ui.components.ScreenStateCard
import com.catalina.planttracker.ui.components.SectionHeader
import com.catalina.planttracker.ui.components.plantStatusColor
import com.catalina.planttracker.ui.plants.PlantUiState
import com.catalina.planttracker.ui.plants.PlantViewModel
import com.catalina.planttracker.ui.plants.PlantViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantDetailsScreen(
    plantId: Int,
    onBack: () -> Unit,
    onNavigateToEdit: (Int) -> Unit,
    onNavigateToCareHistory: (Int, String) -> Unit
) {
    val viewModel: PlantViewModel = viewModel(factory = PlantViewModelFactory())
    val selectedPlant by viewModel.selectedPlant.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val careLogViewModel: CareLogViewModel = viewModel(factory = CareLogViewModelFactory())
    val careLogState by careLogViewModel.careLogUiState.collectAsStateWithLifecycle()

    var isDeleting by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isLogWatering by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(plantId) {
        viewModel.loadPlant(plantId)
    }

    LaunchedEffect(uiState) {
        if (isDeleting && uiState is PlantUiState.Success) {
            onBack()
            viewModel.resetState()
        }
    }

    LaunchedEffect(careLogState) {
        if (isLogWatering) {
            when (careLogState) {
                is CareLogUiState.Success -> {
                    isLogWatering = false
                    snackbarHostState.showSnackbar("Watering logged!")
                }
                is CareLogUiState.Error -> {
                    isLogWatering = false
                }
                else -> {}
            }
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                        onRequestDelete = { showDeleteDialog = true },
                        onCareHistory = { onNavigateToCareHistory(plant.id, plant.name) },
                        onLogWatering = {
                            isLogWatering = true
                            careLogViewModel.createCareLog(plantId, 0, null)
                            viewModel.updateLastWatered(plantId)
                        }
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
    onRequestDelete: () -> Unit,
    onCareHistory: () -> Unit,
    onLogWatering: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        PlantHero(plant)

        // Stat cards
        val due = dueInfo(plant.lastWatered, plant.wateringFrequencyDays)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DetailStatCard(
                label = "Frequency",
                value = plant.wateringFrequencyDays?.let { "${it}d" } ?: "-",
                icon = Icons.Default.WaterDrop,
                modifier = Modifier.weight(1f),
                tint = PlantLeaf,
                containerColor = PlantLeaf,
                valueColor = Color.White,
                labelColor = Color.White.copy(alpha = 0.82f)
            )
            DetailStatCard(
                label = "Health",
                value = when (plant.healthStatus) {
                    0 -> "Good"
                    1 -> "Fair"
                    2 -> "Poor"
                    else -> "-"
                },
                icon = Icons.Default.Eco,
                modifier = Modifier.weight(1f),
                tint = plantStatusColor(plant.healthStatus),
                containerColor = plantStatusContainerColor(plant.healthStatus)
            )
            DetailStatCard(
                label = "Due",
                value = due.value,
                icon = Icons.Default.EventRepeat,
                modifier = Modifier.weight(1f),
                tint = due.tint,
                containerColor = due.containerColor
            )
        }

        SectionHeader(
            title = "Care details",
            subtitle = "The basics for this plant"
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(10.dp, RoundedCornerShape(30.dp), ambientColor = PlantLeaf.copy(alpha = 0.08f)),
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
                DetailRow(
                    Icons.Default.WaterDrop,
                    "Last watered",
                    formatFullDate(plant.lastWatered)
                )
            }
        }

        SectionHeader(
            title = "Notes",
            subtitle = "Care observations and reminders"
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(10.dp, RoundedCornerShape(30.dp), ambientColor = PlantLeaf.copy(alpha = 0.08f)),
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

        // Care log actions
        Button(
            onClick = onLogWatering,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PlantLeaf),
            shape = RoundedCornerShape(20.dp)
        ) {
            Icon(
                Icons.Default.WaterDrop,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                "Log Watering",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
            )
        }

        OutlinedButton(
            onClick = onCareHistory,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, PlantLeaf.copy(alpha = 0.4f))
        ) {
            Icon(
                Icons.Default.History,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = PlantDeepLeaf
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Care History",
                style = MaterialTheme.typography.labelLarge.copy(color = PlantDeepLeaf)
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = PlantMuted
            )
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
                Text(if (deleting) "Deleting…" else "Delete")
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
                    .padding(18.dp)
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(24.dp),
                        ambientColor = PlantInk.copy(alpha = 0.12f),
                        spotColor = PlantInk.copy(alpha = 0.08f)
                    ),
                shape = RoundedCornerShape(24.dp),
                color = Color.White.copy(alpha = 0.97f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.72f))
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
    tint: Color = PlantLeaf,
    containerColor: Color = Color.White,
    valueColor: Color = PlantInk,
    labelColor: Color = PlantMuted
) {
    Card(
        modifier = modifier
            .height(112.dp)
            .shadow(8.dp, RoundedCornerShape(24.dp), ambientColor = tint.copy(alpha = 0.1f)),
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
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .background(Color.White.copy(alpha = 0.62f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(17.dp))
            }
            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = valueColor,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = labelColor,
                        fontWeight = FontWeight.SemiBold
                    )
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

// — Helpers —

private data class DueInfo(val value: String, val tint: Color, val containerColor: Color)

private fun dueInfo(lastWatered: String?, frequencyDays: Int?): DueInfo {
    if (lastWatered.isNullOrBlank() || frequencyDays == null) {
        return DueInfo("-", PlantMuted, PlantMint)
    }
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val last = sdf.parse(lastWatered.substringBefore("T")) ?: return DueInfo("-", PlantMuted, PlantMint)
        val todayStr = sdf.format(Date())
        val today = sdf.parse(todayStr) ?: return DueInfo("-", PlantMuted, PlantMint)
        val daysSince = ((today.time - last.time) / (1000L * 60 * 60 * 24)).toInt()
        val daysLeft = frequencyDays - daysSince
        when {
            daysLeft < 0 -> DueInfo("${-daysLeft}d late", PlantRed, Color(0xFFFFE2DE))
            daysLeft == 0 -> DueInfo("Today!", PlantGold, Color(0xFFFFF2B8))
            daysLeft <= 3 -> DueInfo("${daysLeft}d", PlantGold, Color(0xFFFFF9E6))
            else -> DueInfo("${daysLeft}d", PlantLeaf, PlantMint)
        }
    } catch (e: Exception) {
        DueInfo("-", PlantMuted, PlantMint)
    }
}

private fun formatFullDate(value: String?): String {
    if (value.isNullOrBlank()) return "Never"
    return try {
        val input = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val output = SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH)
        val date = input.parse(value.substringBefore("T")) ?: return value.substringBefore("T")
        output.format(date)
    } catch (e: Exception) {
        value.substringBefore("T")
    }
}

private fun plantStatusContainerColor(healthStatus: Int?): Color = when (healthStatus) {
    0 -> Color(0xFFDFF2DE)
    1 -> Color(0xFFFFF2B8)
    2 -> Color(0xFFFFE2DE)
    else -> PlantMint
}
