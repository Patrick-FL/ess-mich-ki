package com.example.ess_mich_ki.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

data class UserSettings(
    val openRouterApiKey: String = "",
    val openRouterBaseUrl: String = SettingsDataStore.DEFAULT_BASE_URL,
    val openRouterModel: String = SettingsDataStore.DEFAULT_MODEL
)

class SettingsDataStore(private val context: Context) {

    companion object {
        val KEY_OPENROUTER_API_KEY = stringPreferencesKey("openrouter_api_key")
        val KEY_OPENROUTER_BASE_URL = stringPreferencesKey("openrouter_base_url")
        val KEY_OPENROUTER_MODEL = stringPreferencesKey("openrouter_model")

        const val DEFAULT_BASE_URL = "https://openrouter.ai/api/v1/"
        const val BASE_URL_STANDARD = "https://openrouter.ai/api/v1/"
        const val BASE_URL_EU = "https://eu.openrouter.ai/api/v1/"
        const val BASE_URL_US = "https://us.openrouter.ai/api/v1/"

        const val DEFAULT_MODEL = "google/gemini-2.0-flash-001"

        val AVAILABLE_BASE_URLS = listOf(
            BASE_URL_STANDARD,
            BASE_URL_EU,
            BASE_URL_US
        )

        val AVAILABLE_MODELS = listOf(
            "google/gemini-2.0-flash-001",
            "openai/gpt-4o-mini",
            "deepseek/deepseek-r1",
            "anthropic/claude-3.5-haiku"
        )
    }

    val userSettingsFlow: Flow<UserSettings> = context.dataStore.data.map { preferences ->
        UserSettings(
            openRouterApiKey = preferences[KEY_OPENROUTER_API_KEY] ?: "",
            openRouterBaseUrl = preferences[KEY_OPENROUTER_BASE_URL] ?: DEFAULT_BASE_URL,
            openRouterModel = preferences[KEY_OPENROUTER_MODEL] ?: DEFAULT_MODEL
        )
    }

    val openRouterApiKeyFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_OPENROUTER_API_KEY] ?: ""
    }

    val openRouterBaseUrlFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_OPENROUTER_BASE_URL] ?: DEFAULT_BASE_URL
    }

    val openRouterModelFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_OPENROUTER_MODEL] ?: DEFAULT_MODEL
    }

    suspend fun updateOpenRouterApiKey(apiKey: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_OPENROUTER_API_KEY] = apiKey.trim()
        }
    }

    suspend fun updateOpenRouterBaseUrl(baseUrl: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_OPENROUTER_BASE_URL] = baseUrl.trim()
        }
    }

    suspend fun updateOpenRouterModel(model: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_OPENROUTER_MODEL] = model.trim()
        }
    }
}
