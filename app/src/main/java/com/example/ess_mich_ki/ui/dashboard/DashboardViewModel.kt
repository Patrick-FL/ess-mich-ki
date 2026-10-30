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
import com.example.ess_mich_ki.data.local.entity.UserGoalEntity
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
    private val isNutritionalGoalsDialogShowingState = MutableStateFlow(false)
    private val csvStartDateState = MutableStateFlow(LocalDate.now().minusDays(30))
    private val csvEndDateState = MutableStateFlow(LocalDate.now())
    private val csvStatusMessageState = MutableStateFlow<String?>(null)
    private val isCsvLoadingState = MutableStateFlow(false)

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
        isApiKeyMissingWarningState,
        isNutritionalGoalsDialogShowingState,
        database.loggedMealDao().getAllMeals(),
        csvStartDateState,
        csvEndDateState,
        csvStatusMessageState,
        isCsvLoadingState
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
        val isNutritionalGoalsDialogShowing = arrayOfValues[14] as Boolean
        @Suppress("UNCHECKED_CAST")
        val allMeals = arrayOfValues[15] as List<LoggedMealEntity>
        val csvStartDate = arrayOfValues[16] as LocalDate
        val csvEndDate = arrayOfValues[17] as LocalDate
        val csvStatusMessage = arrayOfValues[18] as String?
        val isCsvLoading = arrayOfValues[19] as Boolean

        val weeklyDays = computeWeeklyDays(selectedDate)

        val canonicalOrder = listOf(
            com.example.ess_mich_ki.util.CanonicalMealType.BREAKFAST,
            com.example.ess_mich_ki.util.CanonicalMealType.LUNCH,
            com.example.ess_mich_ki.util.CanonicalMealType.DINNER,
            com.example.ess_mich_ki.util.CanonicalMealType.SNACK
        )

        val mergedMeals = canonicalOrder.map { canonicalType ->
            val matching = mealsWithItems.filter {
                com.example.ess_mich_ki.util.CanonicalMealType.fromString(it.meal.mealType) == canonicalType
            }
            val mergedItems = matching.flatMap { it.items }
            val representativeMeal = matching.firstOrNull()?.meal ?: com.example.ess_mich_ki.data.local.entity.LoggedMealEntity(
                dateString = selectedDate.format(dateFormatter),
                mealType = canonicalType.germanName,
                totalCalories = mergedItems.sumOf { it.calories },
                totalProtein = mergedItems.sumOf { it.protein },
                totalCarbs = mergedItems.sumOf { it.carbs },
                totalFat = mergedItems.sumOf { it.fat }
            )
            MealWithItems(
                meal = representativeMeal.copy(
                    totalCalories = mergedItems.sumOf { it.calories },
                    totalProtein = mergedItems.sumOf { it.protein },
                    totalCarbs = mergedItems.sumOf { it.carbs },
                    totalFat = mergedItems.sumOf { it.fat }
                ),
                items = mergedItems
            )
        }

        val allLoggedItems = mergedMeals.flatMap { it.items }
        val consumedCalories = allLoggedItems.sumOf { it.calories }.roundToInt()
        val consumedProtein = allLoggedItems.sumOf { it.protein }.roundToInt()
        val consumedCarbs = allLoggedItems.sumOf { it.carbs }.roundToInt()
        val consumedFat = allLoggedItems.sumOf { it.fat }.roundToInt()

        val isFasting = userGoal?.isFastingDay == true
        val factor = if (isFasting) 0.5 else 1.0

        val baseCalorieTarget = userGoal?.calorieTarget ?: 1581
        val baseProteinTarget = userGoal?.proteinTarget ?: 113
        val baseCarbsTarget = userGoal?.carbsTarget ?: 163
        val baseFatTarget = userGoal?.fatTarget ?: 53

        val calorieTarget = (baseCalorieTarget * factor).roundToInt()
        val proteinTarget = (baseProteinTarget * factor).roundToInt()
        val carbsTarget = (baseCarbsTarget * factor).roundToInt()
        val fatTarget = (baseFatTarget * factor).roundToInt()

        val calorieMinRange = (calorieTarget * 0.9).roundToInt()
        val calorieMaxRange = (calorieTarget * 1.1).roundToInt()

        val dailyCalories = allMeals
            .groupBy { meal ->
                runCatching { LocalDate.parse(meal.dateString, dateFormatter) }.getOrNull()
            }
            .filterKeys { it != null }
            .mapKeys { it.key!! }
            .mapValues { (_, meals) -> meals.sumOf { it.totalCalories }.roundToInt() }

        val streakResult = com.example.ess_mich_ki.util.StreakCalculator.calculateStreaks(
            dailyCalories = dailyCalories,
            defaultMinRange = calorieMinRange,
            defaultMaxRange = calorieMaxRange,
            today = LocalDate.now(),
            isTodayEnded = isDayEnded
        )

        val monthlyCalendarData = com.example.ess_mich_ki.util.StreakCalculator.calculateMonthlyCalendar(
            dailyCalories = dailyCalories,
            defaultMinRange = calorieMinRange,
            defaultMaxRange = calorieMaxRange,
            yearMonth = java.time.YearMonth.from(selectedDate),
            today = LocalDate.now(),
            isTodayEnded = isDayEnded
        )

        DashboardUiState(
            selectedDate = selectedDate,
            weeklyDays = weeklyDays,
            streakCount = streakResult.currentLoggedStreak,
            currentLoggedStreak = streakResult.currentLoggedStreak,
            bestLoggedStreak = streakResult.bestLoggedStreak,
            currentPerfectStreak = streakResult.currentPerfectStreak,
            bestPerfectStreak = streakResult.bestPerfectStreak,
            monthlyCalendarData = monthlyCalendarData,
            calorieConsumed = consumedCalories,
            calorieTarget = calorieTarget,
            calorieMinRange = calorieMinRange,
            calorieMaxRange = calorieMaxRange,
            proteinConsumed = consumedProtein,
            proteinTarget = proteinTarget,
            carbsConsumed = consumedCarbs,
            carbsTarget = carbsTarget,
            fatConsumed = consumedFat,
            fatTarget = fatTarget,
            meals = mergedMeals,
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
            isNutritionalGoalsDialogShowing = isNutritionalGoalsDialogShowing,
            isFastingDay = isFasting,
            baseCalorieTarget = baseCalorieTarget,
            baseProteinTarget = baseProteinTarget,
            baseCarbsTarget = baseCarbsTarget,
            baseFatTarget = baseFatTarget,
            apiKey = userSettings.openRouterApiKey,
            baseUrl = userSettings.openRouterBaseUrl,
            model = userSettings.openRouterModel,
            csvStartDate = csvStartDate,
            csvEndDate = csvEndDate,
            csvStatusMessage = csvStatusMessage,
            isCsvLoading = isCsvLoading
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

    fun openNutritionalGoalsDialog() {
        isNutritionalGoalsDialogShowingState.value = true
    }

    fun closeNutritionalGoalsDialog() {
        isNutritionalGoalsDialogShowingState.value = false
    }

    fun updateNutritionalGoals(kcal: Int, protein: Int, carbs: Int, fat: Int, isFastingDay: Boolean) {
        viewModelScope.launch {
            val currentGoal = database.userGoalDao().getUserGoalSync() ?: UserGoalEntity(
                id = 1,
                goalType = "Fat Loss",
                initialWeight = 64.2,
                currentWeight = 64.2,
                goalWeight = 59.0,
                startDateString = LocalDate.now().toString()
            )

            val updated = currentGoal.copy(
                calorieTarget = kcal,
                proteinTarget = protein,
                carbsTarget = carbs,
                fatTarget = fat,
                isFastingDay = isFastingDay
            )

            database.userGoalDao().insertOrUpdateUserGoal(updated)
            closeNutritionalGoalsDialog()
        }
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
        val targetCanonical = com.example.ess_mich_ki.util.CanonicalMealType.fromString(mealType)

        viewModelScope.launch {
            val existingMeals = database.loggedMealDao().getMealsByDateSync(dateString)
            val existingMeal = existingMeals.firstOrNull {
                com.example.ess_mich_ki.util.CanonicalMealType.fromString(it.mealType) == targetCanonical
            }
            val mealId = existingMeal?.id ?: database.loggedMealDao().insertMeal(
                LoggedMealEntity(
                    dateString = dateString,
                    mealType = mealType
                )
            )

            val loggedItems = parsedMeal.items.map { parsed ->
                val cleanUnit = com.example.ess_mich_ki.util.ServingUnitUtils.sanitizeServingName(parsed.servingName)
                LoggedMealItemEntity(
                    mealId = mealId,
                    name = parsed.name,
                    quantity = parsed.quantity,
                    servingName = cleanUnit,
                    servingGrams = if (parsed.servingGrams > 0.0) parsed.servingGrams else com.example.ess_mich_ki.util.ServingUnitUtils.getStandardGramWeightForUnit(cleanUnit),
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

    fun updateCsvStartDate(date: LocalDate) {
        csvStartDateState.value = date
    }

    fun updateCsvEndDate(date: LocalDate) {
        csvEndDateState.value = date
    }

    fun clearCsvStatusMessage() {
        csvStatusMessageState.value = null
    }

    fun exportCsvToUri(context: Context, uri: android.net.Uri) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            isCsvLoadingState.value = true
            csvStatusMessageState.value = null
            try {
                val startStr = csvStartDateState.value.format(dateFormatter)
                val endStr = csvEndDateState.value.format(dateFormatter)
                val mealsWithItems = database.loggedMealDao().getMealsWithItemsInDateRangeSync(startStr, endStr)
                val csvData = com.example.ess_mich_ki.util.CsvExportImportUtil.exportToCsv(mealsWithItems)

                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(csvData.toByteArray(Charsets.UTF_8))
                }
                val itemCount = mealsWithItems.sumOf { it.items.size }
                csvStatusMessageState.value = "Erfolg: $itemCount Einträge exportiert ($startStr bis $endStr)."
            } catch (e: Exception) {
                csvStatusMessageState.value = "Fehler beim Export: ${e.localizedMessage}"
            } finally {
                isCsvLoadingState.value = false
            }
        }
    }

    fun importCsvFromUri(context: Context, uri: android.net.Uri) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            isCsvLoadingState.value = true
            csvStatusMessageState.value = null
            try {
                val csvContent = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    inputStream.bufferedReader(Charsets.UTF_8).readText()
                } ?: ""

                val records = com.example.ess_mich_ki.util.CsvExportImportUtil.parseCsv(csvContent)
                if (records.isEmpty()) {
                    csvStatusMessageState.value = "Fehler: Keine gültigen Daten in der CSV-Datei gefunden."
                    isCsvLoadingState.value = false
                    return@launch
                }

                val affectedMealIds = mutableSetOf<Long>()
                for (record in records) {
                    val existingMeals = database.loggedMealDao().getMealsByDateSync(record.dateString)
                    val canonicalRecordType = com.example.ess_mich_ki.util.CanonicalMealType.fromString(record.mealType)
                    val existingMeal = existingMeals.firstOrNull {
                        com.example.ess_mich_ki.util.CanonicalMealType.fromString(it.mealType) == canonicalRecordType
                    }

                    val mealId = existingMeal?.id ?: database.loggedMealDao().insertMeal(
                        LoggedMealEntity(
                            dateString = record.dateString,
                            mealType = canonicalRecordType.germanName
                        )
                    )
                    affectedMealIds.add(mealId)

                    database.loggedMealItemDao().insertItem(
                        LoggedMealItemEntity(
                            mealId = mealId,
                            name = record.itemName,
                            quantity = record.quantity,
                            servingName = record.servingName,
                            servingGrams = record.servingGrams,
                            calories = record.calories,
                            protein = record.protein,
                            carbs = record.carbs,
                            fat = record.fat,
                            isChecked = record.isChecked
                        )
                    )
                }

                for (mId in affectedMealIds) {
                    recalculateMealTotals(mId)
                }

                csvStatusMessageState.value = "Erfolg: ${records.size} Einträge erfolgreich importiert!"
            } catch (e: Exception) {
                csvStatusMessageState.value = "Fehler beim Import: ${e.localizedMessage}"
            } finally {
                isCsvLoadingState.value = false
            }
        }
    }

    private suspend fun recalculateMealTotals(mealId: Long) {
        val items = database.loggedMealItemDao().getItemsForMealSync(mealId)
        val meal = database.loggedMealDao().getMealById(mealId) ?: return

        val updatedMeal = meal.copy(
            totalCalories = items.sumOf { it.calories },
            totalProtein = items.sumOf { it.protein },
            totalCarbs = items.sumOf { it.carbs },
            totalFat = items.sumOf { it.fat }
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

        database.loggedMealDao().insertMeal(
            LoggedMealEntity(
                dateString = dateString,
                mealType = "Breakfast",
                totalCalories = 0.0,
                totalProtein = 0.0,
                totalCarbs = 0.0,
                totalFat = 0.0
            )
        )

        database.loggedMealDao().insertMeal(
            LoggedMealEntity(
                dateString = dateString,
                mealType = "Lunch",
                totalCalories = 0.0,
                totalProtein = 0.0,
                totalCarbs = 0.0,
                totalFat = 0.0
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
