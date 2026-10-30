package com.example.ess_mich_ki.ui.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ess_mich_ki.data.local.entity.FoodItemEntity
import com.example.ess_mich_ki.ui.theme.CarbsColor
import com.example.ess_mich_ki.ui.theme.EssmichkiTheme
import com.example.ess_mich_ki.ui.theme.FatColor
import com.example.ess_mich_ki.ui.theme.NothingRed
import com.example.ess_mich_ki.ui.theme.NothingSage
import com.example.ess_mich_ki.ui.theme.NothingYellow
import com.example.ess_mich_ki.ui.theme.ProteinColor
import kotlin.math.roundToInt

@Composable
fun FoodDetailScreen(
    viewModel: FoodDetailViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    FoodDetailContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onToggleFavorite = viewModel::toggleFavorite,
        onQuantityChange = viewModel::updateQuantity,
        onServingSelected = viewModel::selectServingOption,
        onMealTypeSelected = viewModel::selectMealType,
        onToggleNutritionFacts = viewModel::toggleNutritionFacts,
        onToggleMicronutrients = viewModel::toggleMicronutrients,
        onAnalyzeWithCoach = viewModel::analyzeWithFitiaCoach,
        onAddFoodToMeal = {
            viewModel.addFoodToMeal {
                onBackClick()
            }
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodDetailContent(
    uiState: FoodDetailUiState,
    onBackClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onQuantityChange: (Double) -> Unit,
    onServingSelected: (ServingOption) -> Unit,
    onMealTypeSelected: (String) -> Unit,
    onToggleNutritionFacts: () -> Unit,
    onToggleMicronutrients: () -> Unit,
    onAnalyzeWithCoach: () -> Unit,
    onAddFoodToMeal: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showServingDropdown by remember { mutableStateOf(false) }
    var showMealDropdown by remember { mutableStateOf(false) }

    val food = uiState.food
    val mealTypes = listOf("Frühstück", "Mittagessen", "Abendessen", "Snack")

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(32.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Zurück",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(32.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (uiState.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorit",
                            tint = if (uiState.isFavorite) NothingRed else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = onAddFoodToMeal,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NothingYellow,
                            contentColor = Color.Black
                        )
                    ) {
                        Text(
                            text = "Hinzufügen zu ${uiState.selectedMealType}",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Food Name Header Card
            item {
                Card(
                    shape = RoundedCornerShape(28.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Food Icon Badge
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when {
                                    food?.category?.contains("Fruit", true) == true -> "🫐"
                                    food?.category?.contains("Sweets", true) == true -> "🍫"
                                    food?.category?.contains("Dairy", true) == true -> "🥛"
                                    else -> "🥗"
                                },
                                fontSize = 32.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = food?.name ?: "Lade...",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                            if (food?.isVerified == true) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Verifizierte Angaben",
                                    tint = NothingSage,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = food?.category?.uppercase() ?: "LEBENSMITTEL",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "ANGABEN PRO ${uiState.selectedServingOption.label.uppercase()}",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            color = NothingRed,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 4 Key Macro Cards Grid (Calories, Protein, Carbs, Fat)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MacroStatCard(
                        label = "KCAL",
                        value = "${uiState.calculatedCalories.roundToInt()}",
                        accentColor = NothingRed,
                        modifier = Modifier.weight(1f)
                    )
                    MacroStatCard(
                        label = "PROT",
                        value = "${uiState.calculatedProtein.roundToInt()}g",
                        accentColor = ProteinColor,
                        modifier = Modifier.weight(1f)
                    )
                    MacroStatCard(
                        label = "CARBS",
                        value = "${uiState.calculatedCarbs.roundToInt()}g",
                        accentColor = CarbsColor,
                        modifier = Modifier.weight(1f)
                    )
                    MacroStatCard(
                        label = "FETT",
                        value = "${uiState.calculatedFat.roundToInt()}g",
                        accentColor = FatColor,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // "★ Analyze with Coach" AI Button
            item {
                OutlinedButton(
                    onClick = onAnalyzeWithCoach,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    shape = CircleShape
                ) {
                    if (uiState.isCoachAnalyzing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = NothingRed,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Coach analysiert...", fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.onSurface)
                    } else {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "KI-Coach",
                            tint = NothingYellow,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Mit KI-Coach analysieren",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Fitia Coach Analysis Result Card
            if (uiState.coachAnalysisText != null) {
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, NothingRed),
                        colors = CardDefaults.cardColors(
                            containerColor = NothingRed.copy(alpha = 0.12f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "Coach AI",
                                    tint = NothingRed
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "KI-COACH ANALYSE",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = NothingRed
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = uiState.coachAnalysisText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Carbs / Macro Ratio Progress Bar
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        val totalMacros = (uiState.calculatedProtein + uiState.calculatedCarbs + uiState.calculatedFat).coerceAtLeast(1.0)
                        val carbsPct = (uiState.calculatedCarbs / totalMacros * 100).roundToInt()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "KOHLENHYDRAT-ANTEIL",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 0.8.sp
                            )
                            Text(
                                text = "$carbsPct%",
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = CarbsColor
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { (carbsPct / 100f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape),
                            color = CarbsColor,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }

            // Serving & Quantity Selection Section
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "PORTION & MAHLZEIT",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.8.sp
                        )

                        // Quantity Stepper Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "MENGE:",
                                style = MaterialTheme.typography.labelLarge,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                IconButton(
                                    onClick = { onQuantityChange(uiState.selectedQuantity - 0.5) },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Remove,
                                        contentDescription = "Verringern",
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Text(
                                    text = if (uiState.selectedQuantity % 1.0 == 0.0) "${uiState.selectedQuantity.toInt()}" else "${uiState.selectedQuantity}",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )

                                IconButton(
                                    onClick = { onQuantityChange(uiState.selectedQuantity + 0.5) },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Erhöhen",
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        // Serving Selector Dropdown
                        Box {
                            OutlinedButton(
                                onClick = { showServingDropdown = true },
                                modifier = Modifier.fillMaxWidth(),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                shape = CircleShape
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("EINHEIT: ${uiState.selectedServingOption.label.uppercase()}", fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.onSurface)
                                    Icon(Icons.Default.ExpandMore, contentDescription = "Einheit wählen", tint = MaterialTheme.colorScheme.onSurface)
                                }
                            }

                            DropdownMenu(
                                expanded = showServingDropdown,
                                onDismissRequest = { showServingDropdown = false }
                            ) {
                                uiState.availableServingOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option.label, fontFamily = FontFamily.Monospace) },
                                        onClick = {
                                            onServingSelected(option)
                                            showServingDropdown = false
                                        }
                                    )
                                }
                            }
                        }

                        // Meal Type Selector Dropdown
                        Box {
                            OutlinedButton(
                                onClick = { showMealDropdown = true },
                                modifier = Modifier.fillMaxWidth(),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                shape = CircleShape
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("MAHLZEIT: ${uiState.selectedMealType.uppercase()}", fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.onSurface)
                                    Icon(Icons.Default.ExpandMore, contentDescription = "Mahlzeit wählen", tint = MaterialTheme.colorScheme.onSurface)
                                }
                            }

                            DropdownMenu(
                                expanded = showMealDropdown,
                                onDismissRequest = { showMealDropdown = false }
                            ) {
                                mealTypes.forEach { type ->
                                    DropdownMenuItem(
                                        text = { Text(type.uppercase(), fontFamily = FontFamily.Monospace) },
                                        onClick = {
                                            onMealTypeSelected(type)
                                            showMealDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Accordion Section: Nutrition Facts
            item {
                AccordionCard(
                    title = "NÄHRWERTE",
                    isExpanded = uiState.isNutritionFactsExpanded,
                    onToggle = onToggleNutritionFacts
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        NutritionRow("KALORIEN", "${uiState.calculatedCalories.roundToInt()} KCAL")
                        NutritionRow("PROTEIN", "${uiState.calculatedProtein.roundToInt()} G")
                        NutritionRow("KOHLENHYDRATE", "${uiState.calculatedCarbs.roundToInt()} G")
                        NutritionRow("FETT", "${uiState.calculatedFat.roundToInt()} G")
                    }
                }
            }

            // Accordion Section: Micronutrients
            item {
                AccordionCard(
                    title = "MIKRONÄHRSTOFFE",
                    isExpanded = uiState.isMicronutrientsExpanded,
                    onToggle = onToggleMicronutrients
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        NutritionRow("ZUCKER 🍬", "${uiState.calculatedSugars.roundToInt()} G")
                        NutritionRow("BALLASTSTOFFE 🌾", "${uiState.calculatedFiber.roundToInt()} G")
                        NutritionRow("SALZ 🧂", "${uiState.calculatedSalt} G")
                        NutritionRow("GESÄTTIGTE FETTE 🧈", "${uiState.calculatedSatFat.roundToInt()} G")
                    }
                }
            }
        }
    }
}

@Composable
fun MacroStatCard(
    label: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AccordionCard(
    title: String,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = 1.sp
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Einklappen" else "Ausklappen",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.padding(top = 12.dp)
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
fun NutritionRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FoodDetailScreenPreview() {
    val sampleFood = FoodItemEntity(
        id = 3,
        name = "Himbeere Raspberry",
        category = "Fruits",
        calories = 52.0,
        proteinGrams = 1.2,
        carbsGrams = 12.0,
        fatGrams = 0.6,
        sugarsGrams = 4.4,
        fiberGrams = 6.5,
        saltGrams = 0.0,
        satFatGrams = 0.0,
        servingName = "Tasse (150 g)",
        servingGrams = 150.0,
        isFavorite = true,
        isVerified = true
    )

    val sampleState = FoodDetailUiState(
        foodId = 3,
        food = sampleFood,
        selectedQuantity = 1.0,
        selectedServingOption = ServingOption("Tasse (150 g)", 150.0),
        selectedMealType = "Frühstück",
        isFavorite = true
    )

    EssmichkiTheme {
        FoodDetailContent(
            uiState = sampleState,
            onBackClick = {},
            onToggleFavorite = {},
            onQuantityChange = {},
            onServingSelected = {},
            onMealTypeSelected = {},
            onToggleNutritionFacts = {},
            onToggleMicronutrients = {},
            onAnalyzeWithCoach = {},
            onAddFoodToMeal = {}
        )
    }
}
