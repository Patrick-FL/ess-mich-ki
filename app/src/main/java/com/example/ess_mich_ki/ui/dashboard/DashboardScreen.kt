package com.example.ess_mich_ki.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ess_mich_ki.data.local.entity.LoggedMealItemEntity
import com.example.ess_mich_ki.ui.dashboard.components.AiFoodLoggingDialog
import com.example.ess_mich_ki.ui.dashboard.components.CalorieGaugeCard
import com.example.ess_mich_ki.ui.dashboard.components.EditLoggedMealItemDialog
import com.example.ess_mich_ki.ui.dashboard.components.HeaderBar
import com.example.ess_mich_ki.ui.dashboard.components.MealCard
import com.example.ess_mich_ki.ui.dashboard.components.ProfileSettingsSheet
import com.example.ess_mich_ki.ui.progress.components.NutritionalGoalsDialog
import com.example.ess_mich_ki.ui.theme.EssmichkiTheme
import java.time.LocalDate

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onStreakClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    DashboardContent(
        uiState = uiState,
        onDateSelected = viewModel::selectDate,
        onToggleCheck = viewModel::toggleItemCheck,
        onDeleteItem = viewModel::deleteItem,
        onOpenEditItemDialog = viewModel::openEditItemDialog,
        onCloseEditItemDialog = viewModel::closeEditItemDialog,
        onSaveEditItem = viewModel::updateLoggedMealItem,
        onOpenAiDialog = viewModel::openAiDialog,
        onCloseAiDialog = viewModel::closeAiDialog,
        onInputTextChanged = viewModel::updateAiInputText,
        onMealTypeSelected = viewModel::updateAiSelectedMealType,
        onAnalyzeClick = viewModel::analyzeFoodText,
        onUpdateParsedItem = viewModel::updateParsedItem,
        onDeleteParsedItem = viewModel::deleteParsedItem,
        onSaveToMealClick = viewModel::saveParsedItemsToMeal,
        onToggleProfileDrawer = viewModel::toggleProfileDrawer,
        onApiKeyChange = viewModel::updateApiKeyInSettings,
        onBaseUrlChange = viewModel::updateBaseUrlInSettings,
        onModelChange = viewModel::updateModelInSettings,
        onOpenNutritionalGoalsDialog = viewModel::openNutritionalGoalsDialog,
        onCloseNutritionalGoalsDialog = viewModel::closeNutritionalGoalsDialog,
        onSaveNutritionalGoals = viewModel::updateNutritionalGoals,
        onStreakClick = onStreakClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardContent(
    uiState: DashboardUiState,
    onDateSelected: (LocalDate) -> Unit,
    onToggleCheck: (LoggedMealItemEntity, Boolean) -> Unit = { _, _ -> },
    onDeleteItem: (LoggedMealItemEntity) -> Unit,
    onOpenEditItemDialog: (LoggedMealItemEntity) -> Unit = {},
    onCloseEditItemDialog: () -> Unit = {},
    onSaveEditItem: (LoggedMealItemEntity, Double, String, Double) -> Unit = { _, _, _, _ -> },
    onOpenAiDialog: (String) -> Unit,
    onCloseAiDialog: () -> Unit,
    onInputTextChanged: (String) -> Unit,
    onMealTypeSelected: (String) -> Unit,
    onAnalyzeClick: () -> Unit,
    onUpdateParsedItem: (Int, com.example.ess_mich_ki.data.network.openrouter.ParsedFoodItem) -> Unit,
    onDeleteParsedItem: (Int) -> Unit,
    onSaveToMealClick: () -> Unit,
    onToggleProfileDrawer: () -> Unit,
    onApiKeyChange: (String) -> Unit,
    onBaseUrlChange: (String) -> Unit,
    onModelChange: (String) -> Unit,
    onOpenNutritionalGoalsDialog: () -> Unit = {},
    onCloseNutritionalGoalsDialog: () -> Unit = {},
    onSaveNutritionalGoals: (Int, Int, Int, Int, Boolean) -> Unit = { _, _, _, _, _ -> },
    onStreakClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val mealTypes = listOf("Breakfast", "Lunch", "Dinner", "Snack")

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onOpenAiDialog(uiState.aiSelectedMealType) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Log food"
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header Bar
            HeaderBar(
                selectedDate = uiState.selectedDate,
                weeklyDays = uiState.weeklyDays,
                streakCount = uiState.streakCount,
                onDateSelected = onDateSelected,
                onAvatarClick = onToggleProfileDrawer,
                onStreakClick = onStreakClick
            )

            // Main Scrollable Area
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Calorie & Macro Gauge Card
                item {
                    CalorieGaugeCard(
                        calorieConsumed = uiState.calorieConsumed,
                        calorieTarget = uiState.calorieTarget,
                        calorieMinRange = uiState.calorieMinRange,
                        calorieMaxRange = uiState.calorieMaxRange,
                        proteinConsumed = uiState.proteinConsumed,
                        proteinTarget = uiState.proteinTarget,
                        carbsConsumed = uiState.carbsConsumed,
                        carbsTarget = uiState.carbsTarget,
                        fatConsumed = uiState.fatConsumed,
                        fatTarget = uiState.fatTarget,
                        onEditGoalsClick = onOpenNutritionalGoalsDialog
                    )
                }

                // Meals Section Header
                item {
                    Text(
                        text = "Today's Meals",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
                    )
                }

                // Meal Cards (Breakfast, Lunch, Dinner, Snack)
                items(mealTypes) { mealType ->
                    val mealWithItems = uiState.meals.find {
                        it.meal.mealType.equals(mealType, ignoreCase = true)
                    }

                    MealCard(
                        mealType = mealType,
                        mealWithItems = mealWithItems,
                        onDeleteItem = onDeleteItem,
                        onEditItem = onOpenEditItemDialog
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp)) // Padding for FAB
                }
            }
        }

        // Edit Logged Meal Item Dialog
        uiState.editingMealItem?.let { editingItem ->
            EditLoggedMealItemDialog(
                item = editingItem,
                onSave = { itemToSave, newQ, newName, newGrams ->
                    onSaveEditItem(itemToSave, newQ, newName, newGrams)
                },
                onDismiss = onCloseEditItemDialog
            )
        }

        // AI Natural Language Food Logging Dialog
        if (uiState.isAiDialogShowing) {
            AiFoodLoggingDialog(
                selectedMealType = uiState.aiSelectedMealType,
                inputText = uiState.aiInputText,
                isLoading = uiState.isAiLoading,
                parsedResult = uiState.aiParsedResult,
                errorMsg = uiState.aiError,
                isApiKeyMissing = uiState.isApiKeyMissingWarning,
                onMealTypeSelected = onMealTypeSelected,
                onInputTextChanged = onInputTextChanged,
                onAnalyzeClick = onAnalyzeClick,
                onUpdateParsedItem = onUpdateParsedItem,
                onDeleteParsedItem = onDeleteParsedItem,
                onSaveToMealClick = onSaveToMealClick,
                onOpenSettingsClick = onToggleProfileDrawer,
                onDismiss = onCloseAiDialog
            )
        }

        // Profile Drawer & Settings Sheet
        if (uiState.isProfileDrawerOpen) {
            ProfileSettingsSheet(
                apiKey = uiState.apiKey,
                baseUrl = uiState.baseUrl,
                model = uiState.model,
                onApiKeyChange = onApiKeyChange,
                onBaseUrlChange = onBaseUrlChange,
                onModelChange = onModelChange,
                onDismiss = onToggleProfileDrawer
            )
        }

        // Nutritional Macro Goals & Fasting Day Dialog
        if (uiState.isNutritionalGoalsDialogShowing) {
            NutritionalGoalsDialog(
                initialKcal = uiState.baseCalorieTarget,
                initialProtein = uiState.baseProteinTarget,
                initialCarbs = uiState.baseCarbsTarget,
                initialFat = uiState.baseFatTarget,
                initialIsFastingDay = uiState.isFastingDay,
                onSaveGoals = onSaveNutritionalGoals,
                onDismiss = onCloseNutritionalGoalsDialog
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    val today = LocalDate.now()
    val sampleState = DashboardUiState(
        selectedDate = today,
        streakCount = 1,
        calorieConsumed = 632,
        calorieTarget = 1581,
        proteinConsumed = 27,
        proteinTarget = 113,
        carbsConsumed = 84,
        carbsTarget = 163,
        fatConsumed = 22,
        fatTarget = 53
    )

    EssmichkiTheme {
        DashboardContent(
            uiState = sampleState,
            onDateSelected = {},
            onToggleCheck = { _, _ -> },
            onDeleteItem = {},
            onOpenAiDialog = {},
            onCloseAiDialog = {},
            onInputTextChanged = {},
            onMealTypeSelected = {},
            onAnalyzeClick = {},
            onUpdateParsedItem = { _, _ -> },
            onDeleteParsedItem = {},
            onSaveToMealClick = {},
            onToggleProfileDrawer = {},
            onApiKeyChange = {},
            onBaseUrlChange = {},
            onModelChange = {}
        )
    }
}
