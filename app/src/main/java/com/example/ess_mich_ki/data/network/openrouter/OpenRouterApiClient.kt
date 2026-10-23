package com.example.ess_mich_ki.data.network.openrouter

import com.example.ess_mich_ki.data.datastore.SettingsDataStore
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

open class OpenRouterApiClient(
    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build(),
    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()
) {

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    open suspend fun parseFoodText(
        query: String,
        apiKey: String,
        baseUrl: String = SettingsDataStore.DEFAULT_BASE_URL,
        model: String = DEFAULT_MODEL
    ): Result<ParsedMealAiResult> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext Result.failure(
                IllegalArgumentException("OpenRouter API key is missing. Please configure it in Settings.")
            )
        }

        if (query.isBlank()) {
            return@withContext Result.failure(
                IllegalArgumentException("Food description cannot be empty.")
            )
        }

        val cleanBaseUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        val endpoint = "${cleanBaseUrl}chat/completions"

        val systemPrompt = """
            You are an expert nutritional AI assistant for a food tracker app.
            Analyze the user's natural language input describing food or meals consumed (e.g. "7 strawberries", "500g soy yogurt", "1 tsp hazelnuts").
            Extract individual food items, estimate nutritional values accurately based on the given portion size, and return a single valid JSON object.

            CRITICAL UNIT RULES:
            1. 'quantity': MUST be a pure number representing the count/amount of units (e.g. 7.0 for 7 strawberries, 500.0 for 500g, 1.0 for 1 tsp).
            2. 'servingName': MUST be ONLY the pure unit name (e.g. "piece", "g", "ml", "tsp", "tbsp", "cup", "portion"). DO NOT include numbers or grams in servingName (e.g. write "piece", NOT "7 pieces" or "100g").
            3. 'servingGrams': MUST be the weight in grams for EXACTLY ONE unit (e.g. 1 piece strawberry = 14.3g; 1 g = 1.0g; 1 tsp = 5.0g).
            4. 'calories', 'proteinGrams', 'carbsGrams', 'fatGrams': MUST be the TOTAL nutritional values for the ENTIRE consumed amount (quantity * servingGrams).

            REQUIREMENTS:
            Return ONLY a valid JSON object matching this structure with NO markdown formatting, NO triple backticks, and NO leading/trailing text:
            {
              "items": [
                {
                  "name": "Strawberries",
                  "category": "Fruit",
                  "quantity": 7.0,
                  "servingName": "piece",
                  "servingGrams": 14.3,
                  "calories": 32.0,
                  "proteinGrams": 0.7,
                  "carbsGrams": 7.7,
                  "fatGrams": 0.3,
                  "sugarsGrams": 4.9,
                  "fiberGrams": 2.0,
                  "saltGrams": 0.0,
                  "satFatGrams": 0.0
                }
              ],
              "totalCalories": 32.0,
              "totalProtein": 0.7,
              "totalCarbs": 7.7,
              "totalFat": 0.3,
              "summary": "7 Strawberries"
            }
        """.trimIndent()

        val requestPayload = OpenRouterRequest(
            model = model,
            messages = listOf(
                OpenRouterMessage(role = "system", content = systemPrompt),
                OpenRouterMessage(role = "user", content = query)
            ),
            responseFormat = OpenRouterResponseFormat("json_object")
        )

        val requestAdapter = moshi.adapter(OpenRouterRequest::class.java)
        val jsonPayload = requestAdapter.toJson(requestPayload)

        val httpRequest = Request.Builder()
            .url(endpoint)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .addHeader("HTTP-Referer", "https://github.com/example/ess-mich-ki")
            .addHeader("X-Title", "EssMichKI Food Tracker")
            .post(jsonPayload.toRequestBody(jsonMediaType))
            .build()

        try {
            val response = okHttpClient.newCall(httpRequest).execute()
            val responseBodyString = response.body.string()

            if (!response.isSuccessful) {
                return@withContext Result.failure(
                    IOException("OpenRouter API error (HTTP ${response.code}): $responseBodyString")
                )
            }

            val responseAdapter = moshi.adapter(OpenRouterResponse::class.java)
            val openRouterResponse = responseAdapter.fromJson(responseBodyString)

            if (openRouterResponse?.error != null) {
                return@withContext Result.failure(
                    IOException("OpenRouter Error: ${openRouterResponse.error.message}")
                )
            }

            val choiceContent = openRouterResponse?.choices?.firstOrNull()?.message?.content
                ?: return@withContext Result.failure(
                    IOException("Empty or invalid response from AI model.")
                )

            val cleanedJson = cleanJsonResponse(choiceContent)
            val parsedResultAdapter = moshi.adapter(ParsedMealAiResult::class.java)
            val parsedMeal = parsedResultAdapter.fromJson(cleanedJson)
                ?: return@withContext Result.failure(
                    IOException("Failed to parse AI response into meal data.")
                )

            // Calculate totals if missing or 0
            val items = parsedMeal.items
            val calculatedTotalCalories = if (parsedMeal.totalCalories > 0.0) parsedMeal.totalCalories else items.sumOf { it.calories }
            val calculatedTotalProtein = if (parsedMeal.totalProtein > 0.0) parsedMeal.totalProtein else items.sumOf { it.proteinGrams }
            val calculatedTotalCarbs = if (parsedMeal.totalCarbs > 0.0) parsedMeal.totalCarbs else items.sumOf { it.carbsGrams }
            val calculatedTotalFat = if (parsedMeal.totalFat > 0.0) parsedMeal.totalFat else items.sumOf { it.fatGrams }

            val finalResult = parsedMeal.copy(
                totalCalories = calculatedTotalCalories,
                totalProtein = calculatedTotalProtein,
                totalCarbs = calculatedTotalCarbs,
                totalFat = calculatedTotalFat
            )

            Result.success(finalResult)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun analyzeFoodWithCoach(
        foodName: String,
        calories: Double,
        protein: Double,
        carbs: Double,
        fat: Double,
        apiKey: String,
        baseUrl: String = SettingsDataStore.DEFAULT_BASE_URL,
        model: String = DEFAULT_MODEL
    ): Result<String> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext Result.failure(
                IllegalArgumentException("OpenRouter API key is missing. Please configure it in Settings.")
            )
        }

        val cleanBaseUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        val endpoint = "${cleanBaseUrl}chat/completions"

        val prompt = """
            You are Fitia Coach, a friendly, expert nutrition & fitness coach.
            Analyze the following food item:
            Food: $foodName
            Macros per portion: ${calories.toInt()} kcal, ${protein.toInt()}g Protein, ${carbs.toInt()}g Carbs, ${fat.toInt()}g Fat.

            Provide concise, encouraging coaching feedback:
            1. Health Score (out of 10) & Verdict
            2. Top Nutritional Benefits
            3. Best timing/pairing advice (e.g. ideal for post-workout, breakfast, weight loss, etc.)

            Keep the tone upbeat, professional, concise, and helpful. Use bullet points and emoji.
        """.trimIndent()

        val requestPayload = OpenRouterRequest(
            model = model,
            messages = listOf(
                OpenRouterMessage(role = "system", content = "You are Fitia Coach, an encouraging AI nutrition expert."),
                OpenRouterMessage(role = "user", content = prompt)
            )
        )

        val requestAdapter = moshi.adapter(OpenRouterRequest::class.java)
        val jsonPayload = requestAdapter.toJson(requestPayload)

        val httpRequest = Request.Builder()
            .url(endpoint)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .addHeader("HTTP-Referer", "https://github.com/example/ess-mich-ki")
            .addHeader("X-Title", "EssMichKI Food Tracker")
            .post(jsonPayload.toRequestBody(jsonMediaType))
            .build()

        try {
            val response = okHttpClient.newCall(httpRequest).execute()
            val responseBodyString = response.body.string()

            if (!response.isSuccessful) {
                return@withContext Result.failure(
                    IOException("OpenRouter API error (HTTP ${response.code}): $responseBodyString")
                )
            }

            val responseAdapter = moshi.adapter(OpenRouterResponse::class.java)
            val openRouterResponse = responseAdapter.fromJson(responseBodyString)

            val coachReply = openRouterResponse?.choices?.firstOrNull()?.message?.content
                ?: "Fitia Coach analysis unavailable."

            Result.success(coachReply)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun cleanJsonResponse(content: String): String {
        var trimmed = content.trim()
        if (trimmed.startsWith("```json")) {
            trimmed = trimmed.substringAfter("```json")
        } else if (trimmed.startsWith("```")) {
            trimmed = trimmed.substringAfter("```")
        }
        if (trimmed.endsWith("```")) {
            trimmed = trimmed.substringBeforeLast("```")
        }
        return trimmed.trim()
    }

    companion object {
        const val DEFAULT_MODEL = SettingsDataStore.DEFAULT_MODEL
        const val FALLBACK_MODEL = "openai/gpt-4o-mini"
    }
}
