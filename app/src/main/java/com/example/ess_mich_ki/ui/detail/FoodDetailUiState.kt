package com.example.ess_mich_ki.ui.detail

import com.example.ess_mich_ki.data.local.entity.FoodItemEntity

data class ServingOption(
    val label: String,
    val grams: Double
)

data class FoodDetailUiState(
    val foodId: Long = 0,
    val food: FoodItemEntity? = null,
    val selectedQuantity: Double = 1.0,
    val selectedServingOption: ServingOption = ServingOption("Tasse (150 g)", 150.0),
    val availableServingOptions: List<ServingOption> = listOf(
        ServingOption("Tasse (150 g)", 150.0),
        ServingOption("Portion (100 g)", 100.0),
        ServingOption("Gramm (1 g)", 1.0),
        ServingOption("Piece (120 g)", 120.0)
    ),
    val selectedMealType: String = "Breakfast",
    val isFavorite: Boolean = false,
    val isNutritionFactsExpanded: Boolean = true,
    val isMicronutrientsExpanded: Boolean = true,
    val isCoachAnalyzing: Boolean = false,
    val coachAnalysisText: String? = null,
    val coachAnalysisError: String? = null,
    val isAddedSuccess: Boolean = false,
    val apiKey: String = "",
    val baseUrl: String = "",
    val model: String = com.example.ess_mich_ki.data.datastore.SettingsDataStore.DEFAULT_MODEL
) {
    val totalGrams: Double
        get() = selectedQuantity * selectedServingOption.grams

    val factor: Double
        get() {
            val baseGrams = food?.servingGrams ?: 100.0
            return if (baseGrams > 0) totalGrams / baseGrams else 1.0
        }

    val calculatedCalories: Double
        get() = (food?.calories ?: 0.0) * factor

    val calculatedProtein: Double
        get() = (food?.proteinGrams ?: 0.0) * factor

    val calculatedCarbs: Double
        get() = (food?.carbsGrams ?: 0.0) * factor

    val calculatedFat: Double
        get() = (food?.fatGrams ?: 0.0) * factor

    val calculatedSugars: Double
        get() = (food?.sugarsGrams ?: 0.0) * factor

    val calculatedFiber: Double
        get() = (food?.fiberGrams ?: 0.0) * factor

    val calculatedSalt: Double
        get() = (food?.saltGrams ?: 0.0) * factor

    val calculatedSatFat: Double
        get() = (food?.satFatGrams ?: 0.0) * factor
}
