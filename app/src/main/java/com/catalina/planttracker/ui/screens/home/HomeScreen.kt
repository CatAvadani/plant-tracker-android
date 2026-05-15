package com.catalina.planttracker.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.WaterDrop
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.catalina.planttracker.R
import com.catalina.planttracker.model.Plant
import com.catalina.planttracker.notifications.WateringReminderScheduler
import com.catalina.planttracker.ui.carelogs.CareLogViewModel
import com.catalina.planttracker.ui.carelogs.CareLogViewModelFactory
import com.catalina.planttracker.ui.components.PlantBackground
import com.catalina.planttracker.ui.components.PlantCard
import com.catalina.planttracker.ui.components.PlantCream
import com.catalina.planttracker.ui.components.PlantDeepLeaf
import com.catalina.planttracker.ui.components.PlantInk
import com.catalina.planttracker.ui.components.PlantLeaf
import com.catalina.planttracker.ui.components.PlantMint
import com.catalina.planttracker.ui.components.PlantMuted
import com.catalina.planttracker.ui.components.PlantRed
import com.catalina.planttracker.ui.components.PlantGold
import com.catalina.planttracker.ui.components.ScreenStateCard
import com.catalina.planttracker.ui.components.SectionHeader
import com.catalina.planttracker.ui.plants.PlantUiState
import com.catalina.planttracker.ui.plants.PlantViewModel
import com.catalina.planttracker.ui.plants.PlantViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onPlantClick: (Int) -> Unit, onAddPlantClick: () -> Unit) {
    val viewModel: PlantViewModel = viewModel(factory = PlantViewModelFactory())
    val careLogViewModel: CareLogViewModel = viewModel(factory = CareLogViewModelFactory())
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadPlants()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddPlantClick,
                containerColor = PlantLeaf,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.home_action_add_plant)
                )
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
                        title = stringResource(R.string.home_loading_title),
                        message = stringResource(R.string.home_loading_message),
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
                        title = stringResource(R.string.home_error_title),
                        message = state.message,
                        isError = true
                    )
                }
            }

            is PlantUiState.Success -> {
                val plants = state.plants
                LaunchedEffect(plants) {
                    WateringReminderScheduler.scheduleForPlants(context, plants)
                }
                val needsAttentionPlants = plants
                    .filter { it.healthStatus == 1 || it.healthStatus == 2 }
                    .sortedByDescending { it.healthStatus ?: 0 }
                val healthyCount = plants.count { it.healthStatus == 0 }
                val criticalCount = plants.count { it.healthStatus == 2 }

                val today = Calendar.getInstance().time
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                fun daysSince(dateStr: String?): Int {
                    val date = dateStr?.let { runCatching { sdf.parse(it.substringBefore("T")) }.getOrNull() }
                        ?: return Int.MAX_VALUE
                    return ((today.time - date.time) / (1000L * 60 * 60 * 24)).toInt()
                }
                val forgottenPlants = plants
                    .filter { plant ->
                        plant.healthStatus != 1 && plant.healthStatus != 2 &&
                        daysSince(plant.lastWatered) >= 14
                    }
                    .sortedByDescending { daysSince(it.lastWatered) }
                    .take(5)

                val todayCareTasks = plants.filter { plant ->
                    plant.healthStatus == 0 && (
                        (plant.lastWatered == null) ||
                        (plant.wateringFrequencyDays != null &&
                         daysSince(plant.lastWatered) >= plant.wateringFrequencyDays)
                    )
                }

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
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        end = 20.dp,
                        top = 24.dp,
                        bottom = 100.dp
                    )
                ) {
                    item {
                        HomeGreetingHeader(total = plants.size)
                    }

                    item {
                        TodayCareSection(
                            tasks = todayCareTasks,
                            plantViewModel = viewModel,
                            careLogViewModel = careLogViewModel,
                            onPlantClick = onPlantClick
                        )
                    }

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
                            title = stringResource(R.string.home_care_queue_title),
                            subtitle = stringResource(R.string.home_care_queue_subtitle),
                            trailing = stringResource(
                                R.string.home_open_count,
                                needsAttentionPlants.size
                            )
                        )
                    }

                    if (needsAttentionPlants.isEmpty()) {
                        item {
                            AllClearBanner()
                        }
                    } else {
                        items(needsAttentionPlants) { plant ->
                            PlantCard(plant, onClick = { onPlantClick(plant.id) })
                        }
                    }

                    item {
                        SectionHeader(
                            title = stringResource(R.string.home_forgotten_title),
                            subtitle = stringResource(R.string.home_forgotten_subtitle),
                            trailing = if (forgottenPlants.isEmpty()) {
                                stringResource(R.string.home_all_good)
                            } else {
                                pluralStringResource(
                                    R.plurals.home_plants_count,
                                    forgottenPlants.size,
                                    forgottenPlants.size
                                )
                            }
                        )
                    }

                    if (forgottenPlants.isEmpty()) {
                        item { AllWateredBanner() }
                    } else {
                        items(forgottenPlants) { plant ->
                            PlantCard(plant, onClick = { onPlantClick(plant.id) })
                        }
                    }
                }
            }

            else -> {}
        }
    }
}

@Composable
private fun TodayCareSection(
    tasks: List<Plant>,
    plantViewModel: PlantViewModel,
    careLogViewModel: CareLogViewModel,
    onPlantClick: (Int) -> Unit
) {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val todayStr = sdf.format(Date())
    var recentlyTappedIds by remember { mutableStateOf(setOf<Int>()) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader(
            title = stringResource(R.string.home_today_care_title),
            trailing = "${tasks.size}"
        )

        if (tasks.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = PlantMint.copy(alpha = 0.6f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = PlantLeaf,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = stringResource(R.string.home_today_care_all_clear),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = PlantDeepLeaf,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                tasks.forEach { plant ->
                    val isAnimating = recentlyTappedIds.contains(plant.id)
                    TodayCareTaskRow(
                        plant = plant,
                        isAnimating = isAnimating,
                        onClick = { onPlantClick(plant.id) },
                        onWater = {
                            recentlyTappedIds = recentlyTappedIds + plant.id
                            careLogViewModel.createCareLog(plant.id, 0, null)
                            plantViewModel.updatePlant(
                                id = plant.id,
                                name = plant.name,
                                species = plant.species,
                                location = plant.location,
                                wateringFrequencyDays = plant.wateringFrequencyDays,
                                lastWatered = todayStr,
                                healthStatus = plant.healthStatus,
                                notes = plant.notes,
                                imageUrl = plant.imageUrl
                            )
                            plantViewModel.loadPlants()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TodayCareTaskRow(
    plant: Plant,
    isAnimating: Boolean,
    onClick: () -> Unit,
    onWater: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onWater),
        shape = RoundedCornerShape(18.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier.size(36.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isAnimating) Icons.Default.Check else Icons.Default.WaterDrop,
                    contentDescription = null,
                    tint = if (isAnimating) PlantLeaf else PlantLeaf.copy(alpha = 0.7f),
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = plant.name,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = PlantInk,
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 1
                )
                plant.location?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = PlantMuted
                        ),
                        maxLines = 1
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PlantMint)
                    .clickable(onClick = onClick),
                contentAlignment = Alignment.Center
            ) {
                if (!plant.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = plant.imageUrl,
                        contentDescription = plant.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = null,
                        tint = PlantLeaf,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeGreetingHeader(total: Int) {
    val greeting = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 0..11 -> stringResource(R.string.home_greeting_morning)
        in 12..16 -> stringResource(R.string.home_greeting_afternoon)
        else -> stringResource(R.string.home_greeting_evening)
    }
    val subtitle = when {
        total == 0 -> stringResource(R.string.home_collection_empty_subtitle)
        else -> pluralStringResource(R.plurals.home_collection_subtitle, total, total)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = greeting,
            style = MaterialTheme.typography.titleMedium.copy(
                color = PlantMuted,
                fontWeight = FontWeight.Medium
            )
        )
        Text(
            text = stringResource(R.string.home_title),
            style = MaterialTheme.typography.headlineLarge.copy(
                color = PlantDeepLeaf,
                fontWeight = FontWeight.ExtraBold
            )
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = PlantMuted,
                fontWeight = FontWeight.Normal
            )
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
                            text = pluralStringResource(
                                R.plurals.home_dashboard_collection_label,
                                total,
                                total
                            ),
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
                                text = stringResource(R.string.home_dashboard_live),
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
                        label = stringResource(R.string.home_metric_healthy),
                        value = healthy,
                        icon = Icons.Default.CheckCircle,
                        color = PlantLeaf,
                        containerColor = Color(0xFFDFF2DE),
                        modifier = Modifier.weight(1f)
                    )
                    MetricPill(
                        label = stringResource(R.string.home_metric_need_care),
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
                        label = stringResource(R.string.home_metric_critical),
                        value = critical,
                        icon = Icons.Default.WarningAmber,
                        color = Color(0xFFB71C1C),
                        containerColor = Color(0xFFFFE2DE),
                        modifier = Modifier.weight(1f)
                    )
                    MetricPill(
                        label = stringResource(R.string.home_metric_total),
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
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun AllClearBanner() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = PlantMint.copy(alpha = 0.6f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = PlantLeaf,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = stringResource(R.string.home_all_clear_banner),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = PlantDeepLeaf,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

@Composable
private fun AllWateredBanner() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = PlantMint.copy(alpha = 0.6f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = PlantLeaf,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = stringResource(R.string.home_all_watered_banner),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = PlantDeepLeaf,
                    fontWeight = FontWeight.SemiBold
                )
            )
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
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(Color.White.copy(alpha = 0.78f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .background(PlantMint, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Eco,
                    contentDescription = null,
                    tint = PlantLeaf,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
        Text(
            text = stringResource(R.string.home_empty_title),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = PlantInk
            )
        )
        Text(
            text = stringResource(R.string.home_empty_message),
            style = MaterialTheme.typography.bodyLarge.copy(color = PlantMuted),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}
