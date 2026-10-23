package com.example.ess_mich_ki.ui.search

import com.example.ess_mich_ki.data.local.entity.FoodItemEntity
import com.example.ess_mich_ki.data.local.entity.LoggedMealEntity

enum class SearchTab {
    DATABASE,
    FAVORITES,
    CREATED
}

data class RecentMealPreset(
    val title: String,
    val dateString: String,
    val foodCount: Int,
    val totalCalories: Int,
    val mealType: String
)

data class FoodSearchUiState(
    val searchQuery: String = "",
    val selectedCountryFlag: String = "🇩🇪", // "🇩🇪" or "🇺🇸"
    val selectedTab: SearchTab = SearchTab.DATABASE,
    val searchResults: List<FoodItemEntity> = emptyList(),
    val recentlyEnteredFoods: List<FoodItemEntity> = emptyList(),
    val recentMeals: List<RecentMealPreset> = emptyList(),
    val isScanDialogShowing: Boolean = false,
    val isVoiceDialogShowing: Boolean = false,
    val isAiListDialogShowing: Boolean = false,
    val voiceInputText: String = "",
    val isVoiceListening: Boolean = false,
    val apiKey: String = "",
    val baseUrl: String = "",
    val model: String = com.example.ess_mich_ki.data.datastore.SettingsDataStore.DEFAULT_MODEL
)
