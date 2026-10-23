package com.example.ess_mich_ki.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ess_mich_ki.data.datastore.SettingsDataStore
import com.example.ess_mich_ki.data.datastore.UserSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    val userSettings: StateFlow<UserSettings> = settingsDataStore.userSettingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserSettings()
        )

    val openRouterModel: StateFlow<String> = settingsDataStore.openRouterModelFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SettingsDataStore.DEFAULT_MODEL
        )

    fun updateApiKey(apiKey: String) {
        viewModelScope.launch {
            settingsDataStore.updateOpenRouterApiKey(apiKey)
        }
    }

    fun updateBaseUrl(baseUrl: String) {
        viewModelScope.launch {
            settingsDataStore.updateOpenRouterBaseUrl(baseUrl)
        }
    }

    fun setOpenRouterModel(model: String) {
        viewModelScope.launch {
            settingsDataStore.updateOpenRouterModel(model)
        }
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
                return SettingsViewModel(SettingsDataStore(context.applicationContext)) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
