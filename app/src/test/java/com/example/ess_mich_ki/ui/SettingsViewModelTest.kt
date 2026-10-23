package com.example.ess_mich_ki.ui

import com.example.ess_mich_ki.data.datastore.SettingsDataStore
import com.example.ess_mich_ki.data.datastore.UserSettings
import org.junit.Assert.assertEquals
import org.junit.Test

class SettingsViewModelTest {

    @Test
    fun testUserSettingsModel() {
        val userSettings = UserSettings(
            openRouterApiKey = "sk-or-v1-test-key",
            openRouterBaseUrl = SettingsDataStore.BASE_URL_EU,
            openRouterModel = "openai/gpt-4o-mini"
        )

        assertEquals("sk-or-v1-test-key", userSettings.openRouterApiKey)
        assertEquals("https://eu.openrouter.ai/api/v1/", userSettings.openRouterBaseUrl)
        assertEquals("openai/gpt-4o-mini", userSettings.openRouterModel)
    }

    @Test
    fun testAvailableBaseUrlsAndModels() {
        val availableUrls = SettingsDataStore.AVAILABLE_BASE_URLS
        assertEquals(3, availableUrls.size)
        assertEquals("https://openrouter.ai/api/v1/", availableUrls[0])
        assertEquals("https://eu.openrouter.ai/api/v1/", availableUrls[1])
        assertEquals("https://us.openrouter.ai/api/v1/", availableUrls[2])

        val availableModels = SettingsDataStore.AVAILABLE_MODELS
        assertEquals(4, availableModels.size)
        assertEquals("google/gemini-2.0-flash-001", availableModels[0])
        assertEquals("openai/gpt-4o-mini", availableModels[1])
        assertEquals("deepseek/deepseek-r1", availableModels[2])
        assertEquals("anthropic/claude-3.5-haiku", availableModels[3])
    }
}
