package com.example.ess_mich_ki.data

import com.example.ess_mich_ki.data.network.openrouter.OpenRouterApiClient
import com.example.ess_mich_ki.data.network.openrouter.ParsedMealAiResult
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OpenRouterApiClientTest {

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    @Test
    fun testParsedMealAiResultJsonDeserialization() {
        val jsonString = """
            {
              "items": [
                {
                  "name": "Soy Yogurt",
                  "category": "Dairy & Alternatives",
                  "quantity": 500.0,
                  "servingName": "500g",
                  "servingGrams": 500.0,
                  "calories": 270.0,
                  "proteinGrams": 20.0,
                  "carbsGrams": 10.5,
                  "fatGrams": 11.5,
                  "sugarsGrams": 2.5,
                  "fiberGrams": 5.0,
                  "saltGrams": 0.5,
                  "satFatGrams": 2.0
                },
                {
                  "name": "Raspberry",
                  "category": "Fruits",
                  "quantity": 100.0,
                  "servingName": "100g",
                  "servingGrams": 100.0,
                  "calories": 52.0,
                  "proteinGrams": 1.2,
                  "carbsGrams": 12.0,
                  "fatGrams": 0.6,
                  "sugarsGrams": 4.4,
                  "fiberGrams": 6.5,
                  "saltGrams": 0.0,
                  "satFatGrams": 0.0
                }
              ],
              "totalCalories": 322.0,
              "totalProtein": 21.2,
              "totalCarbs": 22.5,
              "totalFat": 12.1,
              "summary": "500g Soy Yogurt with Raspberries"
            }
        """.trimIndent()

        val adapter = moshi.adapter(ParsedMealAiResult::class.java)
        val result = adapter.fromJson(jsonString)

        assertTrue(result != null)
        assertEquals(2, result?.items?.size)
        assertEquals("Soy Yogurt", result?.items?.get(0)?.name)
        assertEquals(270.0, result?.items?.get(0)?.calories ?: 0.0, 0.01)
        assertEquals(322.0, result?.totalCalories ?: 0.0, 0.01)
    }

    @Test
    fun testParseFoodTextMissingApiKeyFails() = runBlocking {
        val client = OpenRouterApiClient()
        val result = client.parseFoodText(
            query = "500g soy yogurt with raspberries and chocolate",
            apiKey = ""
        )

        assertTrue(result.isFailure)
        assertEquals(
            "OpenRouter API key is missing. Please configure it in Settings.",
            result.exceptionOrNull()?.message
        )
    }

    @Test
    fun testParseFoodTextEmptyQueryFails() = runBlocking {
        val client = OpenRouterApiClient()
        val result = client.parseFoodText(
            query = "",
            apiKey = "sk-or-v1-test-key"
        )

        assertTrue(result.isFailure)
        assertEquals(
            "Food description cannot be empty.",
            result.exceptionOrNull()?.message
        )
    }
}
