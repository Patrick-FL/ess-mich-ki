package com.example.ess_mich_ki.ui.dashboard

import com.example.ess_mich_ki.data.local.entity.LoggedMealItemEntity
import com.example.ess_mich_ki.data.local.model.MealWithItems
import com.example.ess_mich_ki.data.network.openrouter.ParsedMealAiResult
import java.time.LocalDate

data class DayItem(
    val date: LocalDate,
    val dayNameLetter: String, // e.g., "M", "T", "W"
    val dayNumber: Int,       // e.g., 22
    val isSelected: Boolean,
    val isToday: Boolean
)

data class DashboardUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val weeklyDays: List<DayItem> = emptyList(),
    val streakCount: Int = 0,
    val currentLoggedStreak: Int = 0,
    val bestLoggedStreak: Int = 0,
    val currentPerfectStreak: Int = 0,
    val bestPerfectStreak: Int = 0,
    val monthlyCalendarData: com.example.ess_mich_ki.util.MonthlyCalendarData? = null,
    val calorieConsumed: Int = 0,
    val calorieTarget: Int = 1581,
    val calorieMinRange: Int = 1423,
    val calorieMaxRange: Int = 1739,
    val proteinConsumed: Int = 0,
    val proteinTarget: Int = 113,
    val carbsConsumed: Int = 0,
    val carbsTarget: Int = 163,
    val fatConsumed: Int = 0,
    val fatTarget: Int = 53,
    val meals: List<MealWithItems> = emptyList(),
    val isDayEnded: Boolean = false,
    val isAiDialogShowing: Boolean = false,
    val aiSelectedMealType: String = "Breakfast",
    val aiInputText: String = "",
    val isAiLoading: Boolean = false,
    val aiParsedResult: ParsedMealAiResult? = null,
    val aiError: String? = null,
    val editingMealItem: LoggedMealItemEntity? = null,
    val isProfileDrawerOpen: Boolean = false,
    val isApiKeyMissingWarning: Boolean = false,
    val isNutritionalGoalsDialogShowing: Boolean = false,
    val isFastingDay: Boolean = false,
    val baseCalorieTarget: Int = 1581,
    val baseProteinTarget: Int = 113,
    val baseCarbsTarget: Int = 163,
    val baseFatTarget: Int = 53,
    val apiKey: String = "",
    val baseUrl: String = "https://openrouter.ai/api/v1/",
    val model: String = com.example.ess_mich_ki.data.datastore.SettingsDataStore.DEFAULT_MODEL
)
