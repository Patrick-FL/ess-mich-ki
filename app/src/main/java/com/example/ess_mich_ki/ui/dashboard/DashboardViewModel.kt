package com.example.ess_mich_ki.ui.dashboard

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ess_mich_ki.data.datastore.SettingsDataStore
import com.example.ess_mich_ki.data.local.database.AppDatabase
import com.example.ess_mich_ki.data.local.entity.FoodItemEntity
import com.example.ess_mich_ki.data.local.entity.LoggedMealEntity
import com.example.ess_mich_ki.data.local.entity.LoggedMealItemEntity
import com.example.ess_mich_ki.data.local.model.MealWithItems
import com.example.ess_mich_ki.data.network.openrouter.OpenRouterApiClient
import com.example.ess_mich_ki.data.network.openrouter.ParsedFoodItem
import com.example.ess_mich_ki.data.network.openrouter.ParsedMealAiResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import kotlin.math.roundToInt

class DashboardViewModel(
    private val database: AppDatabase,
    private val settingsDataStore: SettingsDataStore,
    private val openRouterApiClient: OpenRouterApiClient = OpenRouterApiClient()
) : ViewModel() {

    private val selectedDateState = MutableStateFlow(LocalDate.now())
    private val isDayEndedState = MutableStateFlow(false)
    private val isAiDialogShowingState = MutableStateFlow(false)
    private val aiSelectedMealTypeState = MutableStateFlow("Breakfast")
    private val aiInputTextState = MutableStateFlow("")
    private val isAiLoadingState = MutableStateFlow(false)
    private val aiParsedResultState = MutableStateFlow<ParsedMealAiResult?>(null)
    private val aiErrorState = MutableStateFlow<String?>(null)
    private val editingMealItemState = MutableStateFlow<LoggedMealItemEntity?>(null)
    private val isProfileDrawerOpenState = MutableStateFlow(false)
    private val isApiKeyMissingWarningState = MutableStateFlow(false)

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    init {
        viewModelScope.launch {
            seedInitialDataIfNecessary(LocalDate.now())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val mealsFlow = selectedDateState.flatMapLatest { date ->
        val dateString = date.format(dateFormatter)
        database.loggedMealDao().getMealsWithItemsByDate(dateString)
    }

    val uiState: StateFlow<DashboardUiState> = combine(
        selectedDateState,
        mealsFlow,
        database.userGoalDao().getUserGoal(),
        settingsDataStore.userSettingsFlow,
        isDayEndedState,
        isAiDialogShowingState,
        aiSelectedMealTypeState,
        aiInputTextState,
        isAiLoadingState,
        aiParsedResultState,
        aiErrorState,
        editingMealItemState,
        isProfileDrawerOpenState,
        isApiKeyMissingWarningState
    ) { arrayOfValues ->
        val selectedDate = arrayOfValues[0] as LocalDate
        @Suppress("UNCHECKED_CAST")
        val mealsWithItems = arrayOfValues[1] as List<MealWithItems>
        val userGoal = arrayOfValues[2] as? com.example.ess_mich_ki.data.local.entity.UserGoalEntity
        val userSettings = arrayOfValues[3] as com.example.ess_mich_ki.data.datastore.UserSettings
        val isDayEnded = arrayOfValues[4] as Boolean
        val isAiDialogShowing = arrayOfValues[5] as Boolean
        val aiSelectedMealType = arrayOfValues[6] as String
        val aiInputText = arrayOfValues[7] as String
        val isAiLoading = arrayOfValues[8] as Boolean
        val aiParsedResult = arrayOfValues[9] as ParsedMealAiResult?
        val aiError = arrayOfValues[10] as String?
        val editingMealItem = arrayOfValues[11] as LoggedMealItemEntity?
        val isProfileDrawerOpen = arrayOfValues[12] as Boolean
        val isApiKeyMissingWarning = arrayOfValues[13] as Boolean

        val weeklyDays = computeWeeklyDays(selectedDate)

        val distinctMeals = mealsWithItems.distinctBy { it.meal.mealType.lowercase() }
        val checkedItems = distinctMeals.flatMap { it.items }.filter { it.isChecked }
        val consumedCalories = checkedItems.sumOf { it.calories }.roundToInt()
        val consumedProtein = checkedItems.sumOf { it.protein }.roundToInt()
        val consumedCarbs = checkedItems.sumOf { it.carbs }.roundToInt()
        val consumedFat = checkedItems.sumOf { it.fat }.roundToInt()

        val calorieTarget = userGoal?.calorieTarget ?: 1581
        val proteinTarget = 113
        val carbsTarget = 163
        val fatTarget = 53

        DashboardUiState(
            selectedDate = selectedDate,
            weeklyDays = weeklyDays,
            streakCount = 1,
            calorieConsumed = consumedCalories,
            calorieTarget = calorieTarget,
            calorieMinRange = (calorieTarget * 0.9).roundToInt(),
            calorieMaxRange = (calorieTarget * 1.1).roundToInt(),
            proteinConsumed = consumedProtein,
            proteinTarget = proteinTarget,
            carbsConsumed = consumedCarbs,
            carbsTarget = carbsTarget,
            fatConsumed = consumedFat,
            fatTarget = fatTarget,
            meals = distinctMeals,
            isDayEnded = isDayEnded,
            isAiDialogShowing = isAiDialogShowing,
            aiSelectedMealType = aiSelectedMealType,
            aiInputText = aiInputText,
            isAiLoading = isAiLoading,
            aiParsedResult = aiParsedResult,
            aiError = aiError,
            editingMealItem = editingMealItem,
            isProfileDrawerOpen = isProfileDrawerOpen,
            isApiKeyMissingWarning = isApiKeyMissingWarning,
            apiKey = userSettings.openRouterApiKey,
            baseUrl = userSettings.openRouterBaseUrl,
            model = userSettings.openRouterModel
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState()
    )

    fun selectDate(date: LocalDate) {
        selectedDateState.value = date
    }

    fun toggleItemCheck(item: LoggedMealItemEntity, isChecked: Boolean) {
        viewModelScope.launch {
            val updatedItem = item.copy(isChecked = isChecked)
            database.loggedMealItemDao().updateItem(updatedItem)
            recalculateMealTotals(item.mealId)
        }
    }

    fun deleteItem(item: LoggedMealItemEntity) {
        viewModelScope.launch {
            database.loggedMealItemDao().deleteItem(item)
            recalculateMealTotals(item.mealId)
        }
    }

    fun openEditItemDialog(item: LoggedMealItemEntity) {
        editingMealItemState.value = item
    }

    fun closeEditItemDialog() {
        editingMealItemState.value = null
    }

    fun updateLoggedMealItem(
        item: LoggedMealItemEntity,
        newQuantity: Double,
        newServingName: String,
        newServingGrams: Double
    ) {
        viewModelScope.launch {
            val oldTotalGrams = (item.quantity * item.servingGrams).coerceAtLeast(1.0)
            val newTotalGrams = (newQuantity * newServingGrams).coerceAtLeast(0.0)
            val scaleFactor = newTotalGrams / oldTotalGrams

            val updatedItem = item.copy(
                quantity = newQuantity,
                servingName = newServingName,
                servingGrams = newServingGrams,
                calories = item.calories * scaleFactor,
                protein = item.protein * scaleFactor,
                carbs = item.carbs * scaleFactor,
                fat = item.fat * scaleFactor
            )

            database.loggedMealItemDao().updateItem(updatedItem)
            recalculateMealTotals(item.mealId)
            closeEditItemDialog()
        }
    }

    fun endDay() {
        isDayEndedState.value = true
    }

    fun openAiDialog(mealType: String = "Breakfast") {
        val apiKey = uiState.value.apiKey
        if (apiKey.isBlank()) {
            isApiKeyMissingWarningState.value = true
        } else {
            isApiKeyMissingWarningState.value = false
        }
        aiSelectedMealTypeState.value = mealType
        aiInputTextState.value = ""
        aiParsedResultState.value = null
        aiErrorState.value = null
        isAiLoadingState.value = false
        isAiDialogShowingState.value = true
    }

    fun closeAiDialog() {
        isAiDialogShowingState.value = false
        isAiLoadingState.value = false
        aiParsedResultState.value = null
        aiErrorState.value = null
    }

    fun updateAiInputText(text: String) {
        aiInputTextState.value = text
    }

    fun updateAiSelectedMealType(mealType: String) {
        aiSelectedMealTypeState.value = mealType
    }

    fun analyzeFoodText() {
        val text = aiInputTextState.value.trim()
        val apiKey = uiState.value.apiKey
        val baseUrl = uiState.value.baseUrl
        val model = uiState.value.model

        if (text.isBlank()) return

        if (apiKey.isBlank()) {
            isApiKeyMissingWarningState.value = true
            return
        }

        viewModelScope.launch {
            isAiLoadingState.value = true
            aiErrorState.value = null

            val result = openRouterApiClient.parseFoodText(
                query = text,
                apiKey = apiKey,
                baseUrl = baseUrl,
                model = model
            )

            isAiLoadingState.value = false

            result.fold(
                onSuccess = { parsedMeal ->
                    aiParsedResultState.value = parsedMeal
                },
                onFailure = { throwable ->
                    aiErrorState.value = throwable.localizedMessage ?: "Failed to analyze food description."
                }
            )
        }
    }

    fun updateParsedItem(index: Int, updatedItem: ParsedFoodItem) {
        val current = aiParsedResultState.value ?: return
        val updatedList = current.items.toMutableList()
        if (index in updatedList.indices) {
            updatedList[index] = updatedItem
            val newTotalCalories = updatedList.sumOf { it.calories }
            val newTotalProtein = updatedList.sumOf { it.proteinGrams }
            val newTotalCarbs = updatedList.sumOf { it.carbsGrams }
            val newTotalFat = updatedList.sumOf { it.fatGrams }

            aiParsedResultState.value = current.copy(
                items = updatedList,
                totalCalories = newTotalCalories,
                totalProtein = newTotalProtein,
                totalCarbs = newTotalCarbs,
                totalFat = newTotalFat
            )
        }
    }

    fun deleteParsedItem(index: Int) {
        val current = aiParsedResultState.value ?: return
        val updatedList = current.items.toMutableList()
        if (index in updatedList.indices) {
            updatedList.removeAt(index)
            val newTotalCalories = updatedList.sumOf { it.calories }
            val newTotalProtein = updatedList.sumOf { it.proteinGrams }
            val newTotalCarbs = updatedList.sumOf { it.carbsGrams }
            val newTotalFat = updatedList.sumOf { it.fatGrams }

            aiParsedResultState.value = current.copy(
                items = updatedList,
                totalCalories = newTotalCalories,
                totalProtein = newTotalProtein,
                totalCarbs = newTotalCarbs,
                totalFat = newTotalFat
            )
        }
    }

    fun saveParsedItemsToMeal() {
        val parsedMeal = aiParsedResultState.value ?: return
        val dateString = selectedDateState.value.format(dateFormatter)
        val mealType = aiSelectedMealTypeState.value

        viewModelScope.launch {
            var existingMeal = database.loggedMealDao().getMealWithItemsByDateAndType(dateString, mealType)
            val mealId = if (existingMeal != null) {
                existingMeal.meal.id
            } else {
                database.loggedMealDao().insertMeal(
                    LoggedMealEntity(
                        dateString = dateString,
                        mealType = mealType
                    )
                )
            }

            val loggedItems = parsedMeal.items.map { parsed ->
                LoggedMealItemEntity(
                    mealId = mealId,
                    name = parsed.name,
                    quantity = parsed.quantity,
                    servingName = parsed.servingName,
                    servingGrams = parsed.servingGrams,
                    calories = parsed.calories,
                    protein = parsed.proteinGrams,
                    carbs = parsed.carbsGrams,
                    fat = parsed.fatGrams,
                    isChecked = true
                )
            }

            database.loggedMealItemDao().insertItems(loggedItems)
            recalculateMealTotals(mealId)
            closeAiDialog()
        }
    }

    fun toggleProfileDrawer() {
        isProfileDrawerOpenState.update { !it }
    }

    fun updateApiKeyInSettings(apiKey: String) {
        viewModelScope.launch {
            settingsDataStore.updateOpenRouterApiKey(apiKey)
            if (apiKey.isNotBlank()) {
                isApiKeyMissingWarningState.value = false
            }
        }
    }

    fun updateBaseUrlInSettings(baseUrl: String) {
        viewModelScope.launch {
            settingsDataStore.updateOpenRouterBaseUrl(baseUrl)
        }
    }

    fun updateModelInSettings(model: String) {
        viewModelScope.launch {
            settingsDataStore.updateOpenRouterModel(model)
        }
    }

    private suspend fun recalculateMealTotals(mealId: Long) {
        val items = database.loggedMealItemDao().getItemsForMealSync(mealId)
        val meal = database.loggedMealDao().getMealById(mealId) ?: return

        val checkedItems = items.filter { it.isChecked }
        val updatedMeal = meal.copy(
            totalCalories = checkedItems.sumOf { it.calories },
            totalProtein = checkedItems.sumOf { it.protein },
            totalCarbs = checkedItems.sumOf { it.carbs },
            totalFat = checkedItems.sumOf { it.fat }
        )
        database.loggedMealDao().updateMeal(updatedMeal)
    }

    private fun computeWeeklyDays(selectedDate: LocalDate): List<DayItem> {
        val today = LocalDate.now()
        val startOfWeek = selectedDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val days = mutableListOf<DayItem>()

        for (i in 0..6) {
            val date = startOfWeek.plusDays(i.toLong())
            val letter = when (date.dayOfWeek) {
                DayOfWeek.MONDAY -> "M"
                DayOfWeek.TUESDAY -> "T"
                DayOfWeek.WEDNESDAY -> "W"
                DayOfWeek.THURSDAY -> "T"
                DayOfWeek.FRIDAY -> "F"
                DayOfWeek.SATURDAY -> "S"
                DayOfWeek.SUNDAY -> "S"
            }
            days.add(
                DayItem(
                    date = date,
                    dayNameLetter = letter,
                    dayNumber = date.dayOfMonth,
                    isSelected = date == selectedDate,
                    isToday = date == today
                )
            )
        }
        return days
    }

    private suspend fun seedInitialDataIfNecessary(date: LocalDate) {
        val dateString = date.format(dateFormatter)
        val existingMeals = database.loggedMealDao().getMealsByDateSync(dateString)
        if (existingMeals.isNotEmpty()) {
            return
        }
        seedInitialDataForDate(date)
    }

    private suspend fun seedInitialDataForDate(date: LocalDate) {
        val dateString = date.format(dateFormatter)

        val breakfastId = database.loggedMealDao().insertMeal(
            LoggedMealEntity(
                dateString = dateString,
                mealType = "Breakfast",
                totalCalories = 280.0,
                totalProtein = 11.0,
                totalCarbs = 23.0,
                totalFat = 15.0
            )
        )

        val lunchId = database.loggedMealDao().insertMeal(
            LoggedMealEntity(
                dateString = dateString,
                mealType = "Lunch",
                totalCalories = 352.0,
                totalProtein = 16.0,
                totalCarbs = 61.0,
                totalFat = 7.0
            )
        )

        database.loggedMealDao().insertMeal(
            LoggedMealEntity(
                dateString = dateString,
                mealType = "Dinner",
                totalCalories = 0.0,
                totalProtein = 0.0,
                totalCarbs = 0.0,
                totalFat = 0.0
            )
        )

        database.loggedMealDao().insertMeal(
            LoggedMealEntity(
                dateString = dateString,
                mealType = "Snack",
                totalCalories = 0.0,
                totalProtein = 0.0,
                totalCarbs = 0.0,
                totalFat = 0.0
            )
        )

        // Seed Breakfast items
        val breakfastItems = listOf(
            LoggedMealItemEntity(
                mealId = breakfastId,
                foodItemId = 1,
                name = "Soy Yogurt",
                quantity = 1.0,
                servingName = "1 Cup (200g)",
                servingGrams = 200.0,
                calories = 108.0,
                protein = 8.0,
                carbs = 4.2,
                fat = 4.6,
                isChecked = true
            ),
            LoggedMealItemEntity(
                mealId = breakfastId,
                foodItemId = 2,
                name = "Chocolate Without Sugar",
                quantity = 0.25,
                servingName = "25g",
                servingGrams = 25.0,
                calories = 120.0,
                protein = 1.8,
                carbs = 7.5,
                fat = 9.5,
                isChecked = true
            ),
            LoggedMealItemEntity(
                mealId = breakfastId,
                foodItemId = 3,
                name = "Himbeere Raspberry",
                quantity = 1.0,
                servingName = "1 Portion (100g)",
                servingGrams = 100.0,
                calories = 52.0,
                protein = 1.2,
                carbs = 12.0,
                fat = 0.6,
                isChecked = true
            )
        )

        // Seed Lunch items
        val lunchItems = listOf(
            LoggedMealItemEntity(
                mealId = lunchId,
                foodItemId = 5,
                name = "Chicken Breast",
                quantity = 1.0,
                servingName = "1 Piece (150g)",
                servingGrams = 150.0,
                calories = 248.0,
                protein = 46.5,
                carbs = 0.0,
                fat = 5.4,
                isChecked = true
            ),
            LoggedMealItemEntity(
                mealId = lunchId,
                foodItemId = 4,
                name = "Oat Milk",
                quantity = 1.0,
                servingName = "1 Glass (200ml)",
                servingGrams = 200.0,
                calories = 92.0,
                protein = 2.0,
                carbs = 13.0,
                fat = 3.0,
                isChecked = true
            )
        )

        database.loggedMealItemDao().insertItems(breakfastItems)
        database.loggedMealItemDao().insertItems(lunchItems)

        recalculateMealTotals(breakfastId)
        recalculateMealTotals(lunchId)
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
                val db = AppDatabase.getDatabase(context)
                val settings = SettingsDataStore(context)
                return DashboardViewModel(db, settings) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
