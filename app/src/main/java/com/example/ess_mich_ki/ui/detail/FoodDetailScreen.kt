package com.example.ess_mich_ki.ui.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ess_mich_ki.data.local.entity.FoodItemEntity
import com.example.ess_mich_ki.ui.theme.CarbsColor
import com.example.ess_mich_ki.ui.theme.CheckedGreen
import com.example.ess_mich_ki.ui.theme.EssmichkiTheme
import com.example.ess_mich_ki.ui.theme.FatColor
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
    val mealTypes = listOf("Breakfast", "Lunch", "Dinner", "Snack")

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (uiState.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (uiState.isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = { /* Share */ }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share"
                        )
                    }
                    IconButton(onClick = { /* Menu */ }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More options"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            // Yellow / Primary Action Button Bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
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
                            .height(54.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFC107), // Vibrant Yellow from design
                            contentColor = Color.Black
                        )
                    ) {
                        Text(
                            text = "Add to ${uiState.selectedMealType}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
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
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Food Icon Badge
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when {
                                    food?.category?.contains("Fruit", true) == true -> "🫐"
                                    food?.category?.contains("Sweets", true) == true -> "🍫"
                                    food?.category?.contains("Dairy", true) == true -> "🥛"
                                    else -> "🥗"
                                },
                                fontSize = 36.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = food?.name ?: "Loading...",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            if (food?.isVerified == true) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Verified Food",
                                    tint = CheckedGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = food?.category ?: "Food Item",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Data per ${uiState.selectedServingOption.label}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
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
                        label = "Calories",
                        value = "${uiState.calculatedCalories.roundToInt()} kcal",
                        accentColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    MacroStatCard(
                        label = "Protein",
                        value = "${uiState.calculatedProtein.roundToInt()} g",
                        accentColor = ProteinColor,
                        modifier = Modifier.weight(1f)
                    )
                    MacroStatCard(
                        label = "Carbs",
                        value = "${uiState.calculatedCarbs.roundToInt()} g",
                        accentColor = CarbsColor,
                        modifier = Modifier.weight(1f)
                    )
                    MacroStatCard(
                        label = "Fat",
                        value = "${uiState.calculatedFat.roundToInt()} g",
                        accentColor = FatColor,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // "★ Analyze with Fitia Coach" AI Button
            item {
                OutlinedButton(
                    onClick = onAnalyzeWithCoach,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    if (uiState.isCoachAnalyzing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Fitia Coach is analyzing...")
                    } else {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Fitia Coach",
                            tint = Color(0xFFFFB300)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Analyze with Fitia Coach",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Fitia Coach Analysis Result Card
            if (uiState.coachAnalysisText != null) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
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
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Fitia Coach Insights",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
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
                    shape = RoundedCornerShape(16.dp),
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
                                text = "Carbs Macro Ratio",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Carbs $carbsPct%",
                                style = MaterialTheme.typography.bodyMedium,
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
                                .clip(RoundedCornerShape(4.dp)),
                            color = CarbsColor,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }

            // Serving & Quantity Selection Section
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
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
                            text = "Portion & Meal Type",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        // Quantity Stepper Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Quantity:",
                                style = MaterialTheme.typography.bodyMedium
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
                                    Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease")
                                }

                                Text(
                                    text = if (uiState.selectedQuantity % 1.0 == 0.0) "${uiState.selectedQuantity.toInt()}" else "${uiState.selectedQuantity}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )

                                IconButton(
                                    onClick = { onQuantityChange(uiState.selectedQuantity + 0.5) },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = "Increase")
                                }
                            }
                        }

                        // Serving Selector Dropdown
                        Box {
                            OutlinedButton(
                                onClick = { showServingDropdown = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Serving: ${uiState.selectedServingOption.label}")
                                    Icon(Icons.Default.ExpandMore, contentDescription = "Select serving")
                                }
                            }

                            DropdownMenu(
                                expanded = showServingDropdown,
                                onDismissRequest = { showServingDropdown = false }
                            ) {
                                uiState.availableServingOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option.label) },
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
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Meal: ${uiState.selectedMealType}")
                                    Icon(Icons.Default.ExpandMore, contentDescription = "Select meal")
                                }
                            }

                            DropdownMenu(
                                expanded = showMealDropdown,
                                onDismissRequest = { showMealDropdown = false }
                            ) {
                                mealTypes.forEach { type ->
                                    DropdownMenuItem(
                                        text = { Text(type) },
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
                    title = "Nutrition Facts",
                    isExpanded = uiState.isNutritionFactsExpanded,
                    onToggle = onToggleNutritionFacts
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        NutritionRow("Calories", "${uiState.calculatedCalories.roundToInt()} kcal")
                        NutritionRow("Protein", "${uiState.calculatedProtein.roundToInt()} g")
                        NutritionRow("Carbohydrates", "${uiState.calculatedCarbs.roundToInt()} g")
                        NutritionRow("Fat", "${uiState.calculatedFat.roundToInt()} g")
                    }
                }
            }

            // Accordion Section: Micronutrients
            item {
                AccordionCard(
                    title = "Micronutrients",
                    isExpanded = uiState.isMicronutrientsExpanded,
                    onToggle = onToggleMicronutrients
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        NutritionRow("Sugars 🍬", "${uiState.calculatedSugars.roundToInt()} g")
                        NutritionRow("Fiber 🌾", "${uiState.calculatedFiber.roundToInt()} g")
                        NutritionRow("Salt 🧂", "${uiState.calculatedSalt} g")
                        NutritionRow("Saturated Fat 🧈", "${uiState.calculatedSatFat.roundToInt()} g")
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
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
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
        shape = RoundedCornerShape(16.dp),
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
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand"
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
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
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
        selectedMealType = "Breakfast",
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
