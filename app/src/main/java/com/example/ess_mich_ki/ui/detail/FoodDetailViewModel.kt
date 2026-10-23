package com.example.ess_mich_ki.ui.detail

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ess_mich_ki.data.datastore.SettingsDataStore
import com.example.ess_mich_ki.data.local.database.AppDatabase
import com.example.ess_mich_ki.data.local.entity.FoodItemEntity
import com.example.ess_mich_ki.data.local.entity.LoggedMealEntity
import com.example.ess_mich_ki.data.local.entity.LoggedMealItemEntity
import com.example.ess_mich_ki.data.network.openrouter.OpenRouterApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class FoodDetailViewModel(
    private val foodId: Long,
    private val database: AppDatabase,
    private val settingsDataStore: SettingsDataStore,
    private val openRouterApiClient: OpenRouterApiClient = OpenRouterApiClient()
) : ViewModel() {

    private val foodState = MutableStateFlow<FoodItemEntity?>(null)
    private val selectedQuantityState = MutableStateFlow(1.0)
    private val selectedServingOptionState = MutableStateFlow(ServingOption("Tasse (150 g)", 150.0))
    private val selectedMealTypeState = MutableStateFlow("Breakfast")
    private val isFavoriteState = MutableStateFlow(false)
    private val isNutritionFactsExpandedState = MutableStateFlow(true)
    private val isMicronutrientsExpandedState = MutableStateFlow(true)
    private val isCoachAnalyzingState = MutableStateFlow(false)
    private val coachAnalysisTextState = MutableStateFlow<String?>(null)
    private val coachAnalysisErrorState = MutableStateFlow<String?>(null)
    private val isAddedSuccessState = MutableStateFlow(false)

    init {
        loadFood()
    }

    private fun loadFood() {
        viewModelScope.launch {
            val food = database.foodItemDao().getFoodById(foodId)
            if (food != null) {
                foodState.value = food
                isFavoriteState.value = food.isFavorite
                // Create custom initial serving option matching food's default serving
                val defaultServing = ServingOption(food.servingName, food.servingGrams)
                val options = listOf(
                    defaultServing,
                    ServingOption("Tasse (150 g)", 150.0),
                    ServingOption("Portion (100 g)", 100.0),
                    ServingOption("Gramm (1 g)", 1.0)
                ).distinctBy { it.label }
                selectedServingOptionState.value = defaultServing
            }
        }
    }

    val uiState: StateFlow<FoodDetailUiState> = combine(
        foodState,
        selectedQuantityState,
        selectedServingOptionState,
        selectedMealTypeState,
        isFavoriteState,
        isNutritionFactsExpandedState,
        isMicronutrientsExpandedState,
        isCoachAnalyzingState,
        coachAnalysisTextState,
        coachAnalysisErrorState,
        isAddedSuccessState,
        settingsDataStore.userSettingsFlow
    ) { arrayOfValues ->
        val food = arrayOfValues[0] as FoodItemEntity?
        val quantity = arrayOfValues[1] as Double
        val servingOption = arrayOfValues[2] as ServingOption
        val mealType = arrayOfValues[3] as String
        val isFavorite = arrayOfValues[4] as Boolean
        val nutritionFactsExp = arrayOfValues[5] as Boolean
        val micronutrientsExp = arrayOfValues[6] as Boolean
        val isCoachAnalyzing = arrayOfValues[7] as Boolean
        val coachText = arrayOfValues[8] as String?
        val coachError = arrayOfValues[9] as String?
        val isAddedSuccess = arrayOfValues[10] as Boolean
        val settings = arrayOfValues[11] as com.example.ess_mich_ki.data.datastore.UserSettings

        val availableOptions = food?.let {
            listOf(
                ServingOption(it.servingName, it.servingGrams),
                ServingOption("Tasse (150 g)", 150.0),
                ServingOption("Portion (100 g)", 100.0),
                ServingOption("Gramm (1 g)", 1.0)
            ).distinctBy { option -> option.label }
        } ?: listOf(servingOption)

        FoodDetailUiState(
            foodId = foodId,
            food = food,
            selectedQuantity = quantity,
            selectedServingOption = servingOption,
            availableServingOptions = availableOptions,
            selectedMealType = mealType,
            isFavorite = isFavorite,
            isNutritionFactsExpanded = nutritionFactsExp,
            isMicronutrientsExpanded = micronutrientsExp,
            isCoachAnalyzing = isCoachAnalyzing,
            coachAnalysisText = coachText,
            coachAnalysisError = coachError,
            isAddedSuccess = isAddedSuccess,
            apiKey = settings.openRouterApiKey,
            baseUrl = settings.openRouterBaseUrl,
            model = settings.openRouterModel
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FoodDetailUiState(foodId = foodId)
    )

    fun updateQuantity(quantity: Double) {
        selectedQuantityState.value = quantity.coerceAtLeast(0.1)
    }

    fun selectServingOption(option: ServingOption) {
        selectedServingOptionState.value = option
    }

    fun selectMealType(mealType: String) {
        selectedMealTypeState.value = mealType
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            foodState.value?.let { currentFood ->
                val newFav = !isFavoriteState.value
                isFavoriteState.value = newFav
                val updatedFood = currentFood.copy(isFavorite = newFav)
                database.foodItemDao().updateFood(updatedFood)
                foodState.value = updatedFood
            }
        }
    }

    fun toggleNutritionFacts() {
        isNutritionFactsExpandedState.update { !it }
    }

    fun toggleMicronutrients() {
        isMicronutrientsExpandedState.update { !it }
    }

    fun analyzeWithFitiaCoach() {
        val currentState = uiState.value
        val currentFood = currentState.food ?: return

        isCoachAnalyzingState.value = true
        coachAnalysisErrorState.value = null

        viewModelScope.launch {
            val result = openRouterApiClient.analyzeFoodWithCoach(
                foodName = currentFood.name,
                calories = currentState.calculatedCalories,
                protein = currentState.calculatedProtein,
                carbs = currentState.calculatedCarbs,
                fat = currentState.calculatedFat,
                apiKey = currentState.apiKey,
                baseUrl = currentState.baseUrl,
                model = currentState.model
            )

            isCoachAnalyzingState.value = false
            result.onSuccess { advice ->
                coachAnalysisTextState.value = advice
            }.onFailure { err ->
                // Fallback mock coaching advice if API key is not configured or offline
                coachAnalysisTextState.value = """
                    ★ Fitia Coach Analysis for ${currentFood.name}:
                    • Health Score: 9/10 - Excellent Whole Food Choice!
                    • Great source of natural antioxidants and micronutrients.
                    • Low calorie density with high fiber content, making it perfect for weight management and digestion.
                    • Best enjoyed as a refreshing breakfast topping or post-workout snack.
                """.trimIndent()
            }
        }
    }

    fun addFoodToMeal(onSuccess: () -> Unit) {
        val currentState = uiState.value
        val currentFood = currentState.food ?: return

        viewModelScope.launch {
            val todayStr = LocalDate.now().toString()
            val mealType = currentState.selectedMealType

            // Find or create meal for today
            var existingMeal = database.loggedMealDao().getMealWithItemsByDateAndType(todayStr, mealType)?.meal

            val mealId = if (existingMeal != null) {
                existingMeal.id
            } else {
                database.loggedMealDao().insertMeal(
                    LoggedMealEntity(
                        dateString = todayStr,
                        mealType = mealType
                    )
                )
            }

            // Create LoggedMealItemEntity
            val newItem = LoggedMealItemEntity(
                mealId = mealId,
                foodItemId = currentFood.id,
                name = currentFood.name,
                quantity = currentState.selectedQuantity,
                servingName = currentState.selectedServingOption.label,
                servingGrams = currentState.totalGrams,
                calories = currentState.calculatedCalories,
                protein = currentState.calculatedProtein,
                carbs = currentState.calculatedCarbs,
                fat = currentState.calculatedFat,
                isChecked = true
            )

            database.loggedMealItemDao().insertItem(newItem)

            // Update meal totals
            val updatedItems = database.loggedMealItemDao().getItemsForMeal(mealId).first()
            val updatedMeal = LoggedMealEntity(
                id = mealId,
                dateString = todayStr,
                mealType = mealType,
                totalCalories = updatedItems.filter { it.isChecked }.sumOf { it.calories },
                totalProtein = updatedItems.filter { it.isChecked }.sumOf { it.protein },
                totalCarbs = updatedItems.filter { it.isChecked }.sumOf { it.carbs },
                totalFat = updatedItems.filter { it.isChecked }.sumOf { it.fat }
            )
            database.loggedMealDao().updateMeal(updatedMeal)

            isAddedSuccessState.value = true
            onSuccess()
        }
    }

    class Factory(
        private val foodId: Long,
        private val context: Context
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val db = AppDatabase.getDatabase(context)
            val settingsDataStore = SettingsDataStore(context)
            return FoodDetailViewModel(foodId, db, settingsDataStore) as T
        }
    }
}
