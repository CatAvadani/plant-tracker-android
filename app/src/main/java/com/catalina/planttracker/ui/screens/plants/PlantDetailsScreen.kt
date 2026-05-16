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
import androidx.compose.material.icons.filled.CalendarMonth
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
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.catalina.planttracker.R
import com.catalina.planttracker.data.model.CareLogEntryType
import com.catalina.planttracker.model.Plant
import com.catalina.planttracker.ui.carelogs.CareLogUiState
import com.catalina.planttracker.ui.carelogs.CareLogViewModel
import com.catalina.planttracker.ui.carelogs.CareLogViewModelFactory
import com.catalina.planttracker.ui.components.PlantBackground
import com.catalina.planttracker.ui.components.PlantCream
import com.catalina.planttracker.ui.components.PlantDeepLeaf
import com.catalina.planttracker.ui.components.PlantInk
import com.catalina.planttracker.ui.components.PlantLeaf
import com.catalina.planttracker.ui.components.PlantLine
import com.catalina.planttracker.ui.components.PlantMint
import com.catalina.planttracker.ui.components.PlantMuted
import com.catalina.planttracker.ui.components.PlantGold
import com.catalina.planttracker.ui.components.PlantRed
import com.catalina.planttracker.ui.components.PlantStatusChip
import com.catalina.planttracker.ui.components.ScreenStateCard
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
    val wateringLoggedMessage = stringResource(R.string.plant_details_watering_logged)

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
                    viewModel.updateLastWatered(plantId)
                    isLogWatering = false
                    snackbarHostState.showSnackbar(wateringLoggedMessage)
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
                        loggingWatering = isLogWatering,
                        onEdit = { onNavigateToEdit(plantId) },
                        onRequestDelete = { showDeleteDialog = true },
                        onCareHistory = { onNavigateToCareHistory(plant.id, plant.name) },
                        onLogWatering = {
                            isLogWatering = true
                            careLogViewModel.createCareLog(
                                plantId,
                                CareLogEntryType.WATERED.value,
                                null
                            )
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
    loggingWatering: Boolean,
    onEdit: () -> Unit,
    onRequestDelete: () -> Unit,
    onCareHistory: () -> Unit,
    onLogWatering: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        PlantHero(plant)

        val due = dueInfo(plant.lastWatered, plant.wateringFrequencyDays)

        // Watering Schedule Card
        SectionTitle("Watering Schedule")
        ScheduleCard(plant, due)

        // Plant Info Card
        SectionTitle("About")
        InfoCard(plant)

        // Notes Card
        SectionTitle("Notes")
        NotesCard(plant.notes)

        // Care Actions
        Spacer(modifier = Modifier.height(4.dp))
        Button(
            onClick = onLogWatering,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !loggingWatering,
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
                stringResource(R.string.plant_details_log_watering),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
            )
        }

        OutlinedButton(
            onClick = onCareHistory,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, PlantLeaf.copy(alpha = 0.35f)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = PlantDeepLeaf)
        ) {
            Icon(
                Icons.Default.History,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                stringResource(R.string.plant_details_care_history),
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
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

        // Management Actions
        SectionTitle("Manage")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onEdit,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, PlantLine),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = PlantInk, containerColor = Color(
                    0xFFDFECDA
                )
                )
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Edit", fontWeight = FontWeight.Medium)
            }
            Button(
                onClick = onRequestDelete,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                enabled = !deleting
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    if (deleting) "Deleting…" else "Delete",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(
            color = PlantDeepLeaf,
            fontWeight = FontWeight.Bold
        ),
        modifier = Modifier.padding(top = 4.dp)
    )
}

@Composable
private fun PlantHero(plant: Plant) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(28.dp), ambientColor = PlantLeaf.copy(alpha = 0.12f)),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        ) {
            if (!plant.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = plant.imageUrl,
                    contentDescription = plant.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(28.dp)),
                    contentScale = ContentScale.Crop
                )
                // Bottom gradient scrim for text readability
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.45f)
                                )
                            )
                        )
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFFE8F5E9),
                                    Color(0xFFDDF0D8),
                                    Color(0xFFF1F8E9)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(Color.White.copy(alpha = 0.72f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Eco,
                            contentDescription = null,
                            modifier = Modifier.size(56.dp),
                            tint = PlantLeaf
                        )
                    }
                }
            }

            // Floating health chip
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                PlantStatusChip(healthStatus = plant.healthStatus)
            }
        }
    }
}

@Composable
private fun CompactStatCard(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    accentColor: Color = PlantLeaf,
    containerColor: Color = Color.White
) {
    val iconTint = when {
        accentColor == Color.White && containerColor == PlantLeaf -> PlantLeaf
        accentColor == Color.White -> PlantInk
        else -> accentColor
    }
    Card(
        modifier = modifier
            .height(100.dp)
            .shadow(6.dp, RoundedCornerShape(20.dp), ambientColor = iconTint.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(Color.White.copy(alpha = 0.85f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = accentColor,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = accentColor.copy(alpha = 0.72f),
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

@Composable
private fun ScheduleCard(plant: Plant, due: DueInfo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(24.dp), ambientColor = PlantLeaf.copy(alpha = 0.08f)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ScheduleColumn(
                icon = Icons.Default.EventRepeat,
                label = "Frequency",
                value = plant.wateringFrequencyDays?.let { "Every $it d" } ?: "—",
                accentColor = PlantLeaf,
                modifier = Modifier.weight(1f)
            )
            VerticalScheduleDivider()
            ScheduleColumn(
                icon = Icons.Default.CalendarMonth,
                label = "Last watered",
                value = formatFullDate(plant.lastWatered),
                accentColor = PlantMuted,
                modifier = Modifier.weight(1f)
            )
            VerticalScheduleDivider()
            ScheduleColumn(
                icon = Icons.Default.WaterDrop,
                label = "Next due",
                value = due.value,
                accentColor = due.tint,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ScheduleColumn(
    icon: ImageVector,
    label: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(accentColor.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(17.dp))
        }
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall.copy(
                color = PlantInk,
                fontWeight = FontWeight.Bold
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = PlantMuted,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
private fun VerticalScheduleDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(44.dp)
            .background(PlantLine)
    )
}

@Composable
private fun InfoCard(plant: Plant) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(24.dp), ambientColor = PlantLeaf.copy(alpha = 0.08f)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            InfoRow(Icons.Default.LocalFlorist, "Species", plant.species ?: "Unknown")
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = PlantLine)
            InfoRow(Icons.Default.Place, "Location", plant.location ?: "Unknown")
        }
    }
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(PlantMint, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = PlantLeaf, modifier = Modifier.size(20.dp))
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
private fun NotesCard(notes: String?) {
    val hasNotes = !notes.isNullOrBlank()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                8.dp,
                RoundedCornerShape(24.dp),
                ambientColor = if (hasNotes) PlantLeaf.copy(alpha = 0.06f) else PlantLeaf.copy(alpha = 0.08f)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (hasNotes) PlantCream else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (hasNotes) {
                Text(
                    text = notes,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = PlantInk.copy(alpha = 0.85f),
                        lineHeight = 22.sp
                    )
                )
            } else {
                Text(
                    text = "No notes yet",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = PlantMuted,
                        fontWeight = FontWeight.Medium
                    )
                )
                Text(
                    text = "Tap Edit to add care observations.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = PlantMuted.copy(alpha = 0.7f))
                )
            }
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
        return DueInfo("—", PlantMuted, PlantMint)
    }
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val last = sdf.parse(lastWatered.substringBefore("T")) ?: return DueInfo("—", PlantMuted, PlantMint)
        val todayStr = sdf.format(Date())
        val today = sdf.parse(todayStr) ?: return DueInfo("—", PlantMuted, PlantMint)
        val daysSince = ((today.time - last.time) / (1000L * 60 * 60 * 24)).toInt()
        val daysLeft = frequencyDays - daysSince
        when {
            daysLeft < 0 -> DueInfo("${-daysLeft}d late", PlantRed, Color(0xFFFFE2DE))
            daysLeft == 0 -> DueInfo("Today!", PlantGold, Color(0xFFFFF2B8))
            daysLeft <= 3 -> DueInfo("$daysLeft d", PlantGold, Color(0xFFFFF9E6))
            else -> DueInfo("$daysLeft d", PlantLeaf, PlantMint)
        }
    } catch (e: Exception) {
        DueInfo("—", PlantMuted, PlantMint)
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
