package com.catalina.planttracker.ui.screens.plants

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.catalina.planttracker.model.Plant
import com.catalina.planttracker.notifications.WateringReminderScheduler
import com.catalina.planttracker.ui.components.PlantBackground
import com.catalina.planttracker.ui.components.PlantCream
import com.catalina.planttracker.ui.components.PlantDeepLeaf
import com.catalina.planttracker.ui.components.PlantInk
import com.catalina.planttracker.ui.components.PlantLeaf
import com.catalina.planttracker.ui.components.PlantLine
import com.catalina.planttracker.ui.components.PlantMint
import com.catalina.planttracker.ui.components.PlantMuted
import com.catalina.planttracker.ui.components.ScreenStateCard
import com.catalina.planttracker.ui.components.SectionHeader
import com.catalina.planttracker.ui.plants.PlantUiState
import com.catalina.planttracker.ui.plants.PlantViewModel
import com.catalina.planttracker.ui.plants.PlantViewModelFactory

private enum class PlantFilter(val label: String) {
    ALL("All"),
    HEALTHY("Healthy"),
    CARE("Care"),
    CRITICAL("Critical")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantsScreen(
    onPlantClick: (Int) -> Unit,
    onAddPlantClick: () -> Unit,
    viewModel: PlantViewModel = viewModel(factory = PlantViewModelFactory())
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(PlantFilter.ALL) }

    LaunchedEffect(Unit) {
        viewModel.loadPlants()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Plant Library",
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val currentState = state) {
                is PlantUiState.Loading -> {
                    ScreenStateCard(
                        title = "Loading plant library",
                        message = "Fetching your collection from the garden shelf.",
                        isLoading = true,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(20.dp)
                    )
                }

                is PlantUiState.Error -> {
                    ScreenStateCard(
                        title = "Could not load plants",
                        message = currentState.message,
                        isError = true,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(20.dp)
                    )
                }

                is PlantUiState.Success -> {
                    LaunchedEffect(currentState.plants) {
                        WateringReminderScheduler.scheduleForPlants(context, currentState.plants)
                    }
                    val filteredPlants = currentState.plants
                        .filterByStatus(selectedFilter)
                        .filterByQuery(query)

                    if (currentState.plants.isEmpty()) {
                        EmptyPlantLibrary(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 34.dp)
                                .padding(bottom = 86.dp)
                        )
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(
                                start = 8.dp,
                                end = 8.dp,
                                top = 8.dp,
                                bottom = 80.dp
                            )
                        ) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                PlantsLibraryControls(
                                    totalCount = currentState.plants.size,
                                    query = query,
                                    onQueryChange = { query = it },
                                    selectedFilter = selectedFilter,
                                    onFilterChange = { selectedFilter = it }
                                )
                            }

                            item(span = { GridItemSpan(maxLineSpan) }) {
                                SectionHeader(
                                    title = "Collection",
                                    subtitle = "Search and filter your plants",
                                    trailing = "${filteredPlants.size} shown"
                                )
                            }

                            if (filteredPlants.isEmpty()) {
                                item(span = { GridItemSpan(maxLineSpan) }) {
                                    ScreenStateCard(
                                        title = "No plants found",
                                        message = "Try a different search or filter.",
                                        icon = Icons.Default.Search
                                    )
                                }
                            } else {
                                items(filteredPlants) { plant ->
                                    PlantGridCard(
                                        plant = plant,
                                        onClick = { onPlantClick(plant.id) }
                                    )
                                }
                            }
                        }
                    }
                }

                else -> {}
            }
        }
    }
}

@Composable
private fun PlantGridCard(plant: Plant, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (!plant.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = plant.imageUrl,
                    contentDescription = plant.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(PlantLeaf),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.6f)
                            )
                        )
                    )
            )

            val (badgeColor, badgeText) = when (plant.healthStatus) {
                0 -> PlantLeaf to "Healthy"
                1 -> Color(0xFFFFA000) to "Care"
                2 -> Color(0xFFE53935) to "Critical"
                else -> PlantMuted to "Unknown"
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
                shape = RoundedCornerShape(50.dp),
                color = badgeColor.copy(alpha = 0.92f)
            ) {
                Text(
                    text = badgeText,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                Text(
                    text = plant.name,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                plant.wateringFrequencyDays?.let { days ->
                    Text(
                        text = "Every ${days}d",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyPlantLibrary(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp, Alignment.CenterVertically)
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
            text = "Your library is empty",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = PlantInk
            ),
            textAlign = TextAlign.Center
        )

        Text(
            text = "Tap the add button to start your collection and keep each plant's care in one place.",
            style = MaterialTheme.typography.bodyLarge.copy(color = PlantMuted),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PlantsLibraryControls(
    totalCount: Int,
    query: String,
    onQueryChange: (String) -> Unit,
    selectedFilter: PlantFilter,
    onFilterChange: (PlantFilter) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(30.dp),
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
                            text = "$totalCount plants",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = PlantDeepLeaf
                            )
                        )
                        Text(
                            text = "Keep the collection easy to scan.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = PlantMuted)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = null,
                        tint = PlantLeaf
                    )
                }

                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Search plants") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = PlantMuted
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PlantLeaf,
                        unfocusedBorderColor = PlantLine,
                        focusedLabelColor = PlantLeaf,
                        cursorColor = PlantLeaf,
                        focusedContainerColor = PlantCream,
                        unfocusedContainerColor = PlantCream
                    )
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PlantFilter.entries.forEach { filter ->
                        FilterPill(
                            label = filter.label,
                            selected = selectedFilter == filter,
                            onClick = { onFilterChange(filter) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterPill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(50.dp),
        color = if (selected) PlantLeaf else PlantMint
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelMedium.copy(
                color = if (selected) Color.White else PlantLeaf,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

private fun List<Plant>.filterByStatus(filter: PlantFilter): List<Plant> {
    return when (filter) {
        PlantFilter.ALL -> this
        PlantFilter.HEALTHY -> filter { it.healthStatus == 0 }
        PlantFilter.CARE -> filter { it.healthStatus == 1 || it.healthStatus == 2 }
        PlantFilter.CRITICAL -> filter { it.healthStatus == 2 }
    }
}

private fun List<Plant>.filterByQuery(query: String): List<Plant> {
    val trimmed = query.trim()
    if (trimmed.isEmpty()) return this

    return filter { plant ->
        plant.name.contains(trimmed, ignoreCase = true) ||
            plant.species.orEmpty().contains(trimmed, ignoreCase = true) ||
            plant.location.orEmpty().contains(trimmed, ignoreCase = true)
    }
}
