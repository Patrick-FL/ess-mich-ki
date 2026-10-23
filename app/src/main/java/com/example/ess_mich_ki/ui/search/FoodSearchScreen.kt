package com.example.ess_mich_ki.ui.search

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ess_mich_ki.data.local.entity.FoodItemEntity
import com.example.ess_mich_ki.ui.search.components.BarcodeScannerDialog
import com.example.ess_mich_ki.ui.search.components.VoiceLoggingDialog
import com.example.ess_mich_ki.ui.theme.CheckedGreen
import com.example.ess_mich_ki.ui.theme.EssmichkiTheme
import kotlin.math.roundToInt

@Composable
fun FoodSearchScreen(
    viewModel: FoodSearchViewModel,
    onFoodClick: (Long) -> Unit,
    onOpenAiLogger: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    FoodSearchContent(
        uiState = uiState,
        onSearchQueryChange = viewModel::updateSearchQuery,
        onToggleFlag = viewModel::toggleCountryFlag,
        onTabSelected = viewModel::selectTab,
        onFoodClick = onFoodClick,
        onToggleFavorite = viewModel::toggleFavorite,
        onOpenScan = viewModel::openScanDialog,
        onCloseScan = viewModel::closeScanDialog,
        onBarcodeScanned = viewModel::handleScannedBarcode,
        onOpenVoice = viewModel::openVoiceDialog,
        onCloseVoice = viewModel::closeVoiceDialog,
        onVoiceTextChange = viewModel::setVoiceText,
        onSubmitVoiceText = { query ->
            viewModel.updateSearchQuery(query)
        },
        onOpenAiLogger = onOpenAiLogger,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodSearchContent(
    uiState: FoodSearchUiState,
    onSearchQueryChange: (String) -> Unit,
    onToggleFlag: () -> Unit,
    onTabSelected: (SearchTab) -> Unit,
    onFoodClick: (Long) -> Unit,
    onToggleFavorite: (FoodItemEntity) -> Unit,
    onOpenScan: () -> Unit,
    onCloseScan: () -> Unit,
    onBarcodeScanned: (String) -> Unit,
    onOpenVoice: () -> Unit,
    onCloseVoice: () -> Unit,
    onVoiceTextChange: (String) -> Unit,
    onSubmitVoiceText: (String) -> Unit,
    onOpenAiLogger: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            BottomLoggingBar(
                onOpenRecipes = { /* Open Recipes preset */ },
                onOpenList = onOpenAiLogger,
                onOpenSearch = { /* Active */ },
                onOpenScan = onOpenScan,
                onOpenVoice = onOpenVoice
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Header Bar with Flag Selector
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = uiState.searchQuery,
                            onValueChange = onSearchQueryChange,
                            placeholder = { Text("Search foods...") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search"
                                )
                            },
                            trailingIcon = {
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .clickable { onToggleFlag() }
                                        .padding(6.dp)
                                ) {
                                    Text(
                                        text = uiState.selectedCountryFlag,
                                        fontSize = 20.sp
                                    )
                                }
                            },
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // TabBar: Database | Favorites | Created
                    TabRow(
                        selectedTabIndex = uiState.selectedTab.ordinal,
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.primary
                    ) {
                        SearchTab.entries.forEach { tab ->
                            Tab(
                                selected = uiState.selectedTab == tab,
                                onClick = { onTabSelected(tab) },
                                text = {
                                    Text(
                                        text = when (tab) {
                                            SearchTab.DATABASE -> "Database"
                                            SearchTab.FAVORITES -> "Favorites"
                                            SearchTab.CREATED -> "Created"
                                        },
                                        fontWeight = if (uiState.selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            )
                        }
                    }
                }
            }

            // Results / Lists
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section: RECENTLY ENTERED
                item {
                    Text(
                        text = if (uiState.searchQuery.isBlank()) "RECENTLY ENTERED" else "SEARCH RESULTS",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                if (uiState.searchResults.isEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No foods found. Try another search or log with AI List!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(uiState.searchResults) { food ->
                        FoodSearchItemRow(
                            food = food,
                            onFoodClick = { onFoodClick(food.id) },
                            onToggleFavorite = { onToggleFavorite(food) }
                        )
                    }
                }

                // Section: RECENT MEALS (Presets)
                if (uiState.searchQuery.isBlank() && uiState.recentMeals.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "RECENT MEALS",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    items(uiState.recentMeals) { mealPreset ->
                        RecentMealPresetRow(
                            mealPreset = mealPreset,
                            onClick = { onOpenAiLogger() }
                        )
                    }
                }
            }
        }

        // Dialog Mocks
        if (uiState.isScanDialogShowing) {
            BarcodeScannerDialog(
                onDismiss = onCloseScan,
                onBarcodeScanned = onBarcodeScanned
            )
        }

        if (uiState.isVoiceDialogShowing) {
            VoiceLoggingDialog(
                voiceText = uiState.voiceInputText,
                isListening = uiState.isVoiceListening,
                onVoiceTextChange = onVoiceTextChange,
                onSubmitVoiceText = onSubmitVoiceText,
                onDismiss = onCloseVoice
            )
        }
    }
}

@Composable
fun FoodSearchItemRow(
    food: FoodItemEntity,
    onFoodClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onFoodClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Food Badge Icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when {
                        food.category.contains("Fruit", ignoreCase = true) -> "🫐"
                        food.category.contains("Sweets", ignoreCase = true) -> "🍫"
                        food.category.contains("Dairy", ignoreCase = true) -> "🥛"
                        food.category.contains("Meat", ignoreCase = true) -> "🍗"
                        else -> "🥑"
                    },
                    fontSize = 22.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Food Name & Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = food.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (food.isVerified) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Verified Food",
                            tint = CheckedGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${food.calories.roundToInt()} kcal • ${food.servingName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Favorite Button
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (food.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (food.isFavorite) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Add Plus Arrow
            IconButton(onClick = onFoodClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Select Food",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun RecentMealPresetRow(
    mealPreset: RecentMealPreset,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.RestaurantMenu,
                    contentDescription = "Meal Preset",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = mealPreset.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${mealPreset.foodCount} foods, ${mealPreset.totalCalories} kcal",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Details",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun BottomLoggingBar(
    onOpenRecipes: () -> Unit,
    onOpenList: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenScan: () -> Unit,
    onOpenVoice: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LoggingBarOption(
                icon = Icons.Default.RestaurantMenu,
                label = "Recipes",
                isSelected = false,
                onClick = onOpenRecipes
            )
            LoggingBarOption(
                icon = Icons.AutoMirrored.Filled.FormatListBulleted,
                label = "List",
                isSelected = false,
                onClick = onOpenList
            )
            LoggingBarOption(
                icon = Icons.Default.Search,
                label = "Search",
                isSelected = true,
                onClick = onOpenSearch
            )
            LoggingBarOption(
                icon = Icons.Default.QrCodeScanner,
                label = "Scan",
                isSelected = false,
                onClick = onOpenScan
            )
            LoggingBarOption(
                icon = Icons.Default.Mic,
                label = "Voice",
                isSelected = false,
                onClick = onOpenVoice
            )
        }
    }
}

@Composable
fun LoggingBarOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = tint
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FoodSearchScreenPreview() {
    val sampleFoods = listOf(
        FoodItemEntity(
            id = 3,
            name = "Himbeere Raspberry",
            category = "Fruits",
            calories = 52.0,
            proteinGrams = 1.2,
            carbsGrams = 12.0,
            fatGrams = 0.6,
            servingName = "1 Portion (100g)",
            servingGrams = 100.0,
            isFavorite = true,
            isVerified = true
        ),
        FoodItemEntity(
            id = 2,
            name = "Chocolate Without Sugar",
            category = "Sweets",
            calories = 480.0,
            proteinGrams = 7.0,
            carbsGrams = 30.0,
            fatGrams = 38.0,
            servingName = "1 Bar (100g)",
            servingGrams = 100.0,
            isFavorite = true,
            isVerified = true
        )
    )

    val sampleState = FoodSearchUiState(
        searchQuery = "",
        selectedCountryFlag = "🇩🇪",
        selectedTab = SearchTab.DATABASE,
        searchResults = sampleFoods,
        recentMeals = listOf(
            RecentMealPreset("Breakfast Today", "2026-10-22", 3, 632, "Breakfast")
        )
    )

    EssmichkiTheme {
        FoodSearchContent(
            uiState = sampleState,
            onSearchQueryChange = {},
            onToggleFlag = {},
            onTabSelected = {},
            onFoodClick = {},
            onToggleFavorite = {},
            onOpenScan = {},
            onCloseScan = {},
            onBarcodeScanned = {},
            onOpenVoice = {},
            onCloseVoice = {},
            onVoiceTextChange = {},
            onSubmitVoiceText = {},
            onOpenAiLogger = {}
        )
    }
}
