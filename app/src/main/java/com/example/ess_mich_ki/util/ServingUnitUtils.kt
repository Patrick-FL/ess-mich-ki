package com.example.ess_mich_ki.util

import kotlin.math.roundToInt

object ServingUnitUtils {

    fun sanitizeServingName(rawName: String): String {
        val lower = rawName.lowercase().trim()
        return when {
            lower.contains("piece") || lower.contains("stück") -> "piece"
            lower.contains("tsp") || lower.contains("teelöffel") || lower.contains("tl") -> "tsp"
            lower.contains("tbsp") || lower.contains("esslöffel") || lower.contains("el") -> "tbsp"
            lower.contains("cup") || lower.contains("tasse") -> "cup"
            lower.contains("portion") || lower.contains("serving") -> "portion"
            lower.contains("ml") || lower.contains("milliliter") -> "ml"
            lower.contains("gram") || lower.contains("gramm") || lower == "g" -> "g"
            else -> {
                // Strip numbers and parens
                val clean = rawName.replace(Regex("[0-9~()g]"), "").trim()
                if (clean.isBlank()) "portion" else clean
            }
        }
    }

    fun formatServingDisplay(quantity: Double, servingName: String, servingGrams: Double): String {
        val cleanUnit = sanitizeServingName(servingName)
        val qStr = formatQuantity(quantity)
        val totalGrams = (quantity * servingGrams).roundToInt()

        return when (cleanUnit) {
            "g" -> "$qStr g"
            "ml" -> "$qStr ml"
            "piece" -> "$qStr pieces (~${totalGrams}g)"
            "tsp" -> "$qStr tsp (~${totalGrams}g)"
            "tbsp" -> "$qStr tbsp (~${totalGrams}g)"
            "cup" -> "$qStr cup (~${totalGrams}g)"
            "portion" -> "$qStr portion (~${totalGrams}g)"
            else -> "$qStr $cleanUnit (~${totalGrams}g)"
        }
    }

    fun formatQuantity(quantity: Double): String {
        return if (quantity % 1.0 == 0.0) {
            quantity.toInt().toString()
        } else {
            "%.1f".format(quantity)
        }
    }

    fun getStandardGramWeightForUnit(unit: String): Double {
        return when (sanitizeServingName(unit)) {
            "g" -> 1.0
            "ml" -> 1.0
            "tsp" -> 5.0
            "tbsp" -> 15.0
            "cup" -> 200.0
            "piece" -> 15.0
            "portion" -> 100.0
            else -> 100.0
        }
    }
}
