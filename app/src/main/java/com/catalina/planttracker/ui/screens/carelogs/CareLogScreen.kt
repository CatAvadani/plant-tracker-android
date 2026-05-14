package com.catalina.planttracker.ui.screens.carelogs

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Yard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.catalina.planttracker.R
import com.catalina.planttracker.data.model.CareLogEntryType
import com.catalina.planttracker.data.model.CareLogResponse
import com.catalina.planttracker.ui.carelogs.CareLogUiState
import com.catalina.planttracker.ui.carelogs.CareLogViewModel
import com.catalina.planttracker.ui.carelogs.CareLogViewModelFactory
import com.catalina.planttracker.ui.components.PlantBackground
import com.catalina.planttracker.ui.components.PlantDeepLeaf
import com.catalina.planttracker.ui.components.PlantInk
import com.catalina.planttracker.ui.components.PlantLeaf
import com.catalina.planttracker.ui.components.PlantMint
import com.catalina.planttracker.ui.components.PlantMuted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CareLogScreen(
    plantId: String,
    plantName: String,
    onBack: () -> Unit
) {
    val viewModel: CareLogViewModel = viewModel(factory = CareLogViewModelFactory())
    val uiState by viewModel.careLogUiState.collectAsStateWithLifecycle()
    val parsedPlantId = remember(plantId) { plantId.toIntOrNull() }

    var showBottomSheet by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }

    LaunchedEffect(parsedPlantId) {
        parsedPlantId?.let(viewModel::loadCareLogs)
    }

    LaunchedEffect(uiState) {
        if (isSubmitting && uiState is CareLogUiState.Success) {
            isSubmitting = false
            showBottomSheet = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        plantName,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = PlantDeepLeaf
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back),
                            tint = PlantInk
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            if (parsedPlantId != null) {
                FloatingActionButton(
                    onClick = { showBottomSheet = true },
                    containerColor = PlantLeaf,
                    contentColor = Color.White
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = stringResource(R.string.care_log_action_add)
                    )
                }
            }
        },
        containerColor = PlantBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (parsedPlantId == null) {
                Text(
                    stringResource(R.string.care_log_invalid_plant),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(32.dp),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.error
                    ),
                    textAlign = TextAlign.Center
                )
                return@Box
            }

            when (val state = uiState) {
                is CareLogUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = PlantLeaf
                    )
                }
                is CareLogUiState.Success -> {
                    if (state.data.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Notes,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = PlantMuted.copy(alpha = 0.5f)
                            )
                            Text(
                                stringResource(R.string.care_log_empty_title),
                                style = MaterialTheme.typography.titleMedium.copy(color = PlantMuted),
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(state.data) { log ->
                                CareLogCard(
                                    log = log,
                                    onDelete = { viewModel.deleteCareLog(parsedPlantId, log.id) }
                                )
                            }
                        }
                    }
                }
                is CareLogUiState.Error -> {
                    Text(
                        state.message,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.error
                        ),
                        textAlign = TextAlign.Center
                    )
                }
                else -> {}
            }
        }
    }

    if (showBottomSheet && parsedPlantId != null) {
        AddCareLogBottomSheet(
            onDismiss = { showBottomSheet = false },
            onSave = { entryType, notes ->
                isSubmitting = true
                viewModel.createCareLog(parsedPlantId, entryType, notes)
            }
        )
    }
}

@Composable
private fun CareLogCard(
    log: CareLogResponse,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(PlantMint, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = careLogIcon(log.entryType),
                    contentDescription = null,
                    tint = PlantLeaf,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    stringResource(CareLogEntryType.fromInt(log.entryType).labelRes),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = PlantInk
                    )
                )
                Text(
                    log.createdAt.split("T").firstOrNull() ?: log.createdAt,
                    style = MaterialTheme.typography.bodySmall.copy(color = PlantMuted)
                )
                log.notes?.let { notes ->
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        notes,
                        style = MaterialTheme.typography.bodyMedium.copy(color = PlantMuted)
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(R.string.action_delete),
                    tint = PlantMuted
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddCareLogBottomSheet(
    onDismiss: () -> Unit,
    onSave: (entryType: Int, notes: String?) -> Unit
) {
    var selectedType by remember { mutableStateOf(CareLogEntryType.WATERED) }
    var notes by remember { mutableStateOf("") }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                stringResource(R.string.care_log_sheet_title),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = PlantDeepLeaf
                )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CareLogEntryType.entries.forEach { type ->
                    FilterChip(
                        selected = selectedType == type,
                        onClick = { selectedType = type },
                        label = { Text(stringResource(type.labelRes)) },
                        leadingIcon = {
                            Icon(
                                careLogIcon(type.value),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                }
            }

            OutlinedTextField(
                value = notes,
                onValueChange = { if (it.length <= 1000) notes = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.care_log_notes_optional)) },
                minLines = 3,
                maxLines = 5
            )

            Button(
                onClick = { onSave(selectedType.value, notes.takeIf { it.isNotBlank() }) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PlantLeaf),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(stringResource(R.string.action_save), style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

private fun careLogIcon(entryType: Int): ImageVector = when (entryType) {
    0 -> Icons.Default.WaterDrop
    1 -> Icons.Default.Grass
    2 -> Icons.Default.Yard
    3 -> Icons.Default.ContentCut
    4 -> Icons.Default.BugReport
    5 -> Icons.Default.Favorite
    else -> Icons.AutoMirrored.Filled.Notes
}
