package com.catalina.planttracker.ui.screens.plants

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.catalina.planttracker.data.model.PlantAnalysisResponse
import com.catalina.planttracker.ui.analysis.PlantAnalysisUiState
import com.catalina.planttracker.ui.analysis.PlantAnalysisViewModel
import com.catalina.planttracker.ui.analysis.PlantAnalysisViewModelFactory
import com.catalina.planttracker.ui.components.PlantBackground
import com.catalina.planttracker.ui.components.PlantCream
import com.catalina.planttracker.ui.components.PlantDeepLeaf
import com.catalina.planttracker.ui.components.PlantInk
import com.catalina.planttracker.ui.components.PlantLeaf
import com.catalina.planttracker.ui.components.PlantLine
import com.catalina.planttracker.ui.components.PlantMint
import com.catalina.planttracker.ui.components.PlantMuted
import com.catalina.planttracker.ui.components.ScreenStateCard
import com.catalina.planttracker.ui.components.plantStatusColor
import com.catalina.planttracker.ui.components.plantStatusLabel
import com.catalina.planttracker.ui.plants.PlantUiState
import com.catalina.planttracker.ui.plants.PlantViewModel
import com.catalina.planttracker.ui.plants.PlantViewModelFactory
import java.io.File

private data class HealthChoice(
    val value: Int,
    val icon: ImageVector,
    val containerColor: Color
)

private val healthChoices = listOf(
    HealthChoice(0, Icons.Default.Eco, PlantLeaf),
    HealthChoice(1, Icons.Default.WarningAmber, Color(0xFFFFF2B8)),
    HealthChoice(2, Icons.Default.WarningAmber, Color(0xFFFFE2DE))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlantScreen(onBack: () -> Unit) {
    val viewModel: PlantViewModel = viewModel(factory = PlantViewModelFactory())
    val analysisViewModel: PlantAnalysisViewModel = viewModel(factory = PlantAnalysisViewModelFactory())
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val uploadedImageUrl by viewModel.uploadedImageUrl.collectAsStateWithLifecycle()
    val analysisState by analysisViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var species by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var healthStatus by remember { mutableStateOf(0) }
    var nameError by remember { mutableStateOf(false) }
    var frequencyError by remember { mutableStateOf<String?>(null) }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            analysisViewModel.reset()
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            selectedImageUri = it.savePlantAnalysisImage(context)
            analysisViewModel.reset()
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is PlantUiState.Success && isSaving) {
            viewModel.resetState()
            onBack()
        }
    }

    // Effect to handle createPlant after image upload completes
    LaunchedEffect(uploadedImageUrl) {
        if (uploadedImageUrl != null && isSaving) {
            viewModel.createPlant(
                name = name,
                species = species.ifBlank { null },
                location = location.ifBlank { null },
                wateringFrequencyDays = frequency.toIntOrNull(),
                lastWatered = null,
                healthStatus = healthStatus,
                notes = notes.ifBlank { null },
                imageUrl = uploadedImageUrl
            )
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is PlantUiState.Error && isSaving) {
            isSaving = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Add New Plant",
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
                            contentDescription = "Back",
                            tint = PlantInk
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = PlantBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            AddPlantHero(
                selectedImageUri = selectedImageUri,
                onPickImage = { pickerLauncher.launch("image/*") }
            )

            PlantAnalysisHelperCard(
                selectedImageUri = selectedImageUri,
                analysisState = analysisState,
                onPickImage = { pickerLauncher.launch("image/*") },
                onCaptureImage = { cameraLauncher.launch(null) },
                onAnalyzeImage = {
                    selectedImageUri?.let { uri ->
                        analysisViewModel.analyzeImage(uri, context)
                    }
                },
                onCancelAnalysis = analysisViewModel::cancelAnalysis,
                onRetryAnalysis = {
                    selectedImageUri?.let { uri ->
                        analysisViewModel.analyzeImage(uri, context)
                    }
                },
                onApply = { result ->
                    name = result.plantName
                    if (species.isBlank()) {
                        species = result.plantName
                    }
                    healthStatus = result.healthStatus.toPlantHealthStatus()
                    notes = result.toPlantNotes()
                    nameError = false
                }
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(32.dp), ambientColor = PlantLeaf.copy(alpha = 0.12f)),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AddPlantField(
                        value = name,
                        onValueChange = {
                            name = it
                            nameError = it.isBlank()
                        },
                        label = "Plant name",
                        icon = Icons.Default.LocalFlorist,
                        isError = nameError,
                        supportingText = if (nameError) "Name is required" else null
                    )

                    AddPlantField(
                        value = species,
                        onValueChange = { species = it },
                        label = "Species",
                        icon = Icons.Default.Eco
                    )

                    AddPlantField(
                        value = location,
                        onValueChange = { location = it },
                        label = "Location",
                        icon = Icons.Default.Place
                    )

                    AddPlantField(
                        value = frequency,
                        onValueChange = {
                            frequency = it.filter(Char::isDigit)
                            frequencyError = wateringFrequencyError(frequency)
                        },
                        label = "Watering frequency",
                        icon = Icons.Default.EventRepeat,
                        keyboardType = KeyboardType.Number,
                        isError = frequencyError != null,
                        supportingText = frequencyError,
                        suffix = "days"
                    )

                    HealthStatusSelector(
                        selected = healthStatus,
                        onSelected = { healthStatus = it }
                    )

                    AddPlantField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = "Notes",
                        icon = Icons.AutoMirrored.Filled.Notes,
                        minLines = 3
                    )
                }
            }

            if (uiState is PlantUiState.Error) {
                ScreenStateCard(
                    title = "Could not save plant",
                    message = (uiState as PlantUiState.Error).message,
                    isError = true
                )
            }

            val isLoading = uiState is PlantUiState.Loading
            Button(
                onClick = {
                    if (isSaving || isLoading) return@Button
                    val currentFrequencyError = wateringFrequencyError(frequency)
                    if (name.isBlank()) {
                        nameError = true
                    } else if (currentFrequencyError != null) {
                        frequencyError = currentFrequencyError
                    } else {
                        isSaving = true
                        if (selectedImageUri != null) {
                            viewModel.uploadImage(selectedImageUri!!, context)
                        } else {
                            viewModel.createPlant(
                                name = name,
                                species = species.ifBlank { null },
                                location = location.ifBlank { null },
                                wateringFrequencyDays = frequency.toIntOrNull(),
                                lastWatered = null,
                                healthStatus = healthStatus,
                                notes = notes.ifBlank { null },
                                imageUrl = null
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(8.dp, RoundedCornerShape(18.dp), ambientColor = PlantLeaf.copy(alpha = 0.18f)),
                enabled = !isSaving && !isLoading && name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PlantLeaf,
                    disabledContainerColor = Color(0xFFB7CDB1),
                    contentColor = Color.White,
                    disabledContentColor = Color.White.copy(alpha = 0.78f)
                ),
                shape = RoundedCornerShape(18.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Save Plant", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AddPlantHero(
    selectedImageUri: Uri?,
    onPickImage: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(14.dp, RoundedCornerShape(32.dp), ambientColor = PlantLeaf.copy(alpha = 0.12f))
            .clickable { onPickImage() },
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(184.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color.White, PlantCream, Color(0xFFDDEFD6))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            if (selectedImageUri != null) {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "Selected plant image",
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(32.dp)),
                    contentScale = ContentScale.Crop
                )
                // Overlay for picking again
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Box(
                        modifier = Modifier
                            .padding(12.dp)
                            .size(40.dp)
                            .background(Color.White.copy(alpha = 0.8f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.AddAPhoto,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = PlantLeaf
                        )
                    }
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(PlantMint, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddAPhoto,
                            contentDescription = null,
                            modifier = Modifier.size(34.dp),
                            tint = PlantLeaf
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Add photo",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = PlantLeaf,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "Tap to select an image",
                            style = MaterialTheme.typography.bodySmall.copy(color = PlantMuted)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlantAnalysisHelperCard(
    selectedImageUri: Uri?,
    analysisState: PlantAnalysisUiState,
    onPickImage: () -> Unit,
    onCaptureImage: () -> Unit,
    onAnalyzeImage: () -> Unit,
    onCancelAnalysis: () -> Unit,
    onRetryAnalysis: () -> Unit,
    onApply: (PlantAnalysisResponse) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(24.dp), ambientColor = PlantLeaf.copy(alpha = 0.10f)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(PlantMint, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = PlantLeaf
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "AI plant helper",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = PlantInk,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Analyze a photo, then review and apply suggestions.",
                        style = MaterialTheme.typography.bodySmall.copy(color = PlantMuted)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onPickImage,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Choose")
                }
                OutlinedButton(
                    onClick = onCaptureImage,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Camera")
                }
            }

            Button(
                onClick = onAnalyzeImage,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = selectedImageUri != null && analysisState !is PlantAnalysisUiState.Loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PlantDeepLeaf,
                    disabledContainerColor = Color(0xFFB7CDB1),
                    contentColor = Color.White,
                    disabledContentColor = Color.White.copy(alpha = 0.78f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (analysisState is PlantAnalysisUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Analyzing")
                } else {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(19.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Analyze Photo", fontWeight = FontWeight.SemiBold)
                }
            }

            when (analysisState) {
                PlantAnalysisUiState.Idle -> Unit
                PlantAnalysisUiState.Loading -> {
                    TextButton(
                        onClick = onCancelAnalysis,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Cancel")
                    }
                }
                is PlantAnalysisUiState.Error -> {
                    ScreenStateCard(
                        title = "Could not analyze photo",
                        message = analysisState.message,
                        isError = true
                    )
                    if (analysisState.isRetryable && selectedImageUri != null) {
                        OutlinedButton(
                            onClick = onRetryAnalysis,
                            modifier = Modifier.align(Alignment.End),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Retry")
                        }
                    }
                }
                is PlantAnalysisUiState.Success -> {
                    PlantAnalysisResultCard(
                        result = analysisState.result,
                        onApply = { onApply(analysisState.result) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PlantAnalysisResultCard(
    result: PlantAnalysisResponse,
    onApply: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(PlantCream, RoundedCornerShape(18.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = PlantLeaf
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = result.plantName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = PlantInk,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "${(result.confidence * 100).toInt()}% confidence - ${result.healthStatus}",
                    style = MaterialTheme.typography.bodySmall.copy(color = PlantMuted)
                )
            }
        }

        if (result.notes.isNotBlank()) {
            Text(
                text = result.notes,
                style = MaterialTheme.typography.bodyMedium.copy(color = PlantInk)
            )
        }

        AnalysisSuggestionSection("Possible issues", result.possibleIssues)
        AnalysisSuggestionSection("Watering", result.wateringSuggestions)
        AnalysisSuggestionSection("Lighting", result.lightingSuggestions)
        AnalysisSuggestionSection("Care", result.careSuggestions)

        Button(
            onClick = onApply,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PlantLeaf),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Apply Suggestions", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun AnalysisSuggestionSection(
    title: String,
    suggestions: List<String>
) {
    if (suggestions.isEmpty()) return

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge.copy(
                color = PlantDeepLeaf,
                fontWeight = FontWeight.Bold
            )
        )
        suggestions.forEach { suggestion ->
            Text(
                text = "- $suggestion",
                style = MaterialTheme.typography.bodySmall.copy(color = PlantInk)
            )
        }
    }
}

@Composable
private fun AddPlantField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    supportingText: String? = null,
    suffix: String? = null,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isError) MaterialTheme.colorScheme.error else PlantMuted
            )
        },
        trailingIcon = {
            suffix?.let {
                Text(
                    text = it,
                    modifier = Modifier.padding(end = 12.dp),
                    style = MaterialTheme.typography.labelMedium.copy(color = PlantMuted)
                )
            }
        },
        modifier = modifier.fillMaxWidth(),
        minLines = minLines,
        isError = isError,
        supportingText = supportingText?.let {
            {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(18.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PlantLeaf,
            unfocusedBorderColor = PlantLine,
            errorBorderColor = MaterialTheme.colorScheme.error,
            focusedLabelColor = PlantLeaf,
            cursorColor = PlantLeaf,
            focusedContainerColor = PlantCream,
            unfocusedContainerColor = PlantCream,
            errorContainerColor = PlantCream
        )
    )
}

@Composable
private fun HealthStatusSelector(
    selected: Int,
    onSelected: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Health status",
            style = MaterialTheme.typography.titleSmall.copy(
                color = PlantInk,
                fontWeight = FontWeight.Bold
            )
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            healthChoices.forEach { choice ->
                val selectedChoice = selected == choice.value
                val color = plantStatusColor(choice.value)
                val selectedContainerColor = when (choice.value) {
                    0 -> PlantLeaf
                    1 -> Color(0xFFC49000)
                    2 -> Color(0xFFB71C1C)
                    else -> PlantLeaf
                }
                val tileColor = if (selectedChoice) selectedContainerColor else choice.containerColor
                val contentColor = if (selectedChoice) Color.White else color
                val labelColor = if (selectedChoice) Color.White else color
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(104.dp)
                        .shadow(7.dp, RoundedCornerShape(20.dp), ambientColor = color.copy(alpha = 0.08f))
                        .clickable { onSelected(choice.value) },
                    shape = RoundedCornerShape(20.dp),
                    color = tileColor
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(Color.White.copy(alpha = 0.62f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = choice.icon,
                                contentDescription = null,
                                modifier = Modifier.size(19.dp),
                                tint = contentColor
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = plantStatusLabel(choice.value),
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = labelColor,
                                fontWeight = FontWeight.Bold
                            ),
                            textAlign = TextAlign.Center,
                            maxLines = 2
                        )
                    }
                }
            }
        }
    }
}

private fun Bitmap.savePlantAnalysisImage(context: Context): Uri {
    val file = File(context.cacheDir, "plant_analysis_${System.currentTimeMillis()}.jpg")
    file.outputStream().use { output ->
        compress(Bitmap.CompressFormat.JPEG, 92, output)
    }
    return Uri.fromFile(file)
}

private fun String.toPlantHealthStatus(): Int {
    val normalized = lowercase()
    return when {
        normalized.contains("critical") ||
            normalized.contains("poor") ||
            normalized.contains("disease") ||
            normalized.contains("pest") ||
            normalized.contains("severe") -> 2
        normalized.contains("dry") ||
            normalized.contains("issue") ||
            normalized.contains("moderate") ||
            normalized.contains("attention") ||
            normalized.contains("care") -> 1
        else -> 0
    }
}

private fun PlantAnalysisResponse.toPlantNotes(): String {
    val sections = buildList {
        if (notes.isNotBlank()) add(notes)
        addSuggestionText("Possible issues", possibleIssues)
        addSuggestionText("Watering", wateringSuggestions)
        addSuggestionText("Lighting", lightingSuggestions)
        addSuggestionText("Care", careSuggestions)
    }
    return sections.joinToString(separator = "\n\n")
}

private fun wateringFrequencyError(value: String): String? {
    val days = value.toIntOrNull()
    return when {
        value.isBlank() -> "Watering frequency is required"
        days == null || days !in 1..365 -> "Watering frequency must be between 1 and 365 days"
        else -> null
    }
}

private fun MutableList<String>.addSuggestionText(title: String, suggestions: List<String>) {
    if (suggestions.isNotEmpty()) {
        add("$title:\n${suggestions.joinToString(separator = "\n") { "- $it" }}")
    }
}
