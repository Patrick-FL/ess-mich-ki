package com.example.ess_mich_ki.ui.search

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ess_mich_ki.data.datastore.SettingsDataStore
import com.example.ess_mich_ki.data.local.database.AppDatabase
import com.example.ess_mich_ki.data.local.entity.FoodItemEntity
import com.example.ess_mich_ki.data.network.openrouter.OpenRouterApiClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class FoodSearchViewModel(
    private val database: AppDatabase,
    private val settingsDataStore: SettingsDataStore,
    private val openRouterApiClient: OpenRouterApiClient = OpenRouterApiClient()
) : ViewModel() {

    private val searchQueryState = MutableStateFlow("")
    private val selectedCountryFlagState = MutableStateFlow("🇩🇪")
    private val selectedTabState = MutableStateFlow(SearchTab.DATABASE)
    private val isScanDialogShowingState = MutableStateFlow(false)
    private val isVoiceDialogShowingState = MutableStateFlow(false)
    private val isAiListDialogShowingState = MutableStateFlow(false)
    private val voiceInputTextState = MutableStateFlow("")
    private val isVoiceListeningState = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val foodsFlow = searchQueryState.flatMapLatest { query ->
        if (query.isBlank()) {
            database.foodItemDao().getAllFoods()
        } else {
            database.foodItemDao().searchFoods(query)
        }
    }

    private val recentMealPresetsState = MutableStateFlow(
        listOf(
            RecentMealPreset("Breakfast Today", LocalDate.now().toString(), 3, 632, "Breakfast"),
            RecentMealPreset("High Protein Lunch", LocalDate.now().minusDays(1).toString(), 2, 450, "Lunch"),
            RecentMealPreset("Evening Snack", LocalDate.now().minusDays(1).toString(), 2, 280, "Snack")
        )
    )

    val uiState: StateFlow<FoodSearchUiState> = combine(
        searchQueryState,
        selectedCountryFlagState,
        selectedTabState,
        foodsFlow,
        recentMealPresetsState,
        isScanDialogShowingState,
        isVoiceDialogShowingState,
        isAiListDialogShowingState,
        voiceInputTextState,
        isVoiceListeningState,
        settingsDataStore.userSettingsFlow
    ) { arrayOfValues ->
        val query = arrayOfValues[0] as String
        val flag = arrayOfValues[1] as String
        val tab = arrayOfValues[2] as SearchTab
        @Suppress("UNCHECKED_CAST")
        val allFoods = arrayOfValues[3] as List<FoodItemEntity>
        @Suppress("UNCHECKED_CAST")
        val recentMeals = arrayOfValues[4] as List<RecentMealPreset>
        val isScanShowing = arrayOfValues[5] as Boolean
        val isVoiceShowing = arrayOfValues[6] as Boolean
        val isAiListShowing = arrayOfValues[7] as Boolean
        val voiceText = arrayOfValues[8] as String
        val isListening = arrayOfValues[9] as Boolean
        val settings = arrayOfValues[10] as com.example.ess_mich_ki.data.datastore.UserSettings

        val filteredFoods = when (tab) {
            SearchTab.DATABASE -> allFoods
            SearchTab.FAVORITES -> allFoods.filter { it.isFavorite }
            SearchTab.CREATED -> allFoods.filter { !it.isVerified }
        }

        FoodSearchUiState(
            searchQuery = query,
            selectedCountryFlag = flag,
            selectedTab = tab,
            searchResults = filteredFoods,
            recentlyEnteredFoods = allFoods.take(5),
            recentMeals = recentMeals,
            isScanDialogShowing = isScanShowing,
            isVoiceDialogShowing = isVoiceShowing,
            isAiListDialogShowing = isAiListShowing,
            voiceInputText = voiceText,
            isVoiceListening = isListening,
            apiKey = settings.openRouterApiKey,
            baseUrl = settings.openRouterBaseUrl,
            model = settings.openRouterModel
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FoodSearchUiState()
    )

    fun updateSearchQuery(query: String) {
        searchQueryState.value = query
    }

    fun toggleCountryFlag() {
        selectedCountryFlagState.update { if (it == "🇩🇪") "🇺🇸" else "🇩🇪" }
    }

    fun selectTab(tab: SearchTab) {
        selectedTabState.value = tab
    }

    fun openScanDialog() {
        isScanDialogShowingState.value = true
    }

    fun closeScanDialog() {
        isScanDialogShowingState.value = false
    }

    fun openVoiceDialog() {
        isVoiceDialogShowingState.value = true
        isVoiceListeningState.value = true
        voiceInputTextState.value = "Listening... Speak your meal (e.g., '1 cup of soy yogurt with raspberries')"
    }

    fun closeVoiceDialog() {
        isVoiceDialogShowingState.value = false
        isVoiceListeningState.value = false
    }

    fun setVoiceText(text: String) {
        voiceInputTextState.value = text
    }

    fun openAiListDialog() {
        isAiListDialogShowingState.value = true
    }

    fun closeAiListDialog() {
        isAiListDialogShowingState.value = false
    }

    fun toggleFavorite(food: FoodItemEntity) {
        viewModelScope.launch {
            val updated = food.copy(isFavorite = !food.isFavorite)
            database.foodItemDao().updateFood(updated)
        }
    }

    fun handleScannedBarcode(barcode: String) {
        viewModelScope.launch {
            // Find or mock food for scanned barcode
            val existing = database.foodItemDao().getFoodById(1)
            if (existing != null) {
                searchQueryState.value = existing.name
            }
            closeScanDialog()
        }
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val db = AppDatabase.getDatabase(context)
            val settingsDataStore = SettingsDataStore(context)
            return FoodSearchViewModel(db, settingsDataStore) as T
        }
    }
}
