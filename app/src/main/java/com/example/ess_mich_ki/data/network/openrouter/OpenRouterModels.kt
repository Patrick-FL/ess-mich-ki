package com.example.ess_mich_ki.data.network.openrouter

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OpenRouterMessage(
    @Json(name = "role") val role: String,
    @Json(name = "content") val content: String
)

@JsonClass(generateAdapter = true)
data class OpenRouterResponseFormat(
    @Json(name = "type") val type: String = "json_object"
)

@JsonClass(generateAdapter = true)
data class OpenRouterRequest(
    @Json(name = "model") val model: String = "google/gemini-2.0-flash-001",
    @Json(name = "messages") val messages: List<OpenRouterMessage>,
    @Json(name = "response_format") val responseFormat: OpenRouterResponseFormat? = OpenRouterResponseFormat("json_object"),
    @Json(name = "temperature") val temperature: Double = 0.2
)

@JsonClass(generateAdapter = true)
data class OpenRouterChoice(
    @Json(name = "message") val message: OpenRouterMessage
)

@JsonClass(generateAdapter = true)
data class OpenRouterResponse(
    @Json(name = "id") val id: String? = null,
    @Json(name = "choices") val choices: List<OpenRouterChoice>? = null,
    @Json(name = "error") val error: OpenRouterError? = null
)

@JsonClass(generateAdapter = true)
data class OpenRouterError(
    @Json(name = "message") val message: String? = null,
    @Json(name = "code") val code: Any? = null
)

// Domain model for parsed AI food structure
@JsonClass(generateAdapter = true)
data class ParsedFoodItem(
    @Json(name = "name") val name: String,
    @Json(name = "category") val category: String = "General",
    @Json(name = "quantity") val quantity: Double = 1.0,
    @Json(name = "servingName") val servingName: String = "1 serving",
    @Json(name = "servingGrams") val servingGrams: Double = 100.0,
    @Json(name = "calories") val calories: Double = 0.0,
    @Json(name = "proteinGrams") val proteinGrams: Double = 0.0,
    @Json(name = "carbsGrams") val carbsGrams: Double = 0.0,
    @Json(name = "fatGrams") val fatGrams: Double = 0.0,
    @Json(name = "sugarsGrams") val sugarsGrams: Double = 0.0,
    @Json(name = "fiberGrams") val fiberGrams: Double = 0.0,
    @Json(name = "saltGrams") val saltGrams: Double = 0.0,
    @Json(name = "satFatGrams") val satFatGrams: Double = 0.0
)

@JsonClass(generateAdapter = true)
data class ParsedMealAiResult(
    @Json(name = "items") val items: List<ParsedFoodItem> = emptyList(),
    @Json(name = "totalCalories") val totalCalories: Double = 0.0,
    @Json(name = "totalProtein") val totalProtein: Double = 0.0,
    @Json(name = "totalCarbs") val totalCarbs: Double = 0.0,
    @Json(name = "totalFat") val totalFat: Double = 0.0,
    @Json(name = "summary") val summary: String? = null
)
