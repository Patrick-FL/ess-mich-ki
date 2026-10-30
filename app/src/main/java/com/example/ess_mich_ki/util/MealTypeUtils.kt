package com.example.ess_mich_ki.util

enum class CanonicalMealType(val germanName: String, val englishName: String) {
    BREAKFAST("Frühstück", "Breakfast"),
    LUNCH("Mittagessen", "Lunch"),
    DINNER("Abendessen", "Dinner"),
    SNACK("Snack", "Snack");

    companion object {
        fun fromString(raw: String): CanonicalMealType {
            return when (raw.trim().lowercase()) {
                "breakfast", "frühstück" -> BREAKFAST
                "lunch", "mittagessen", "mittag" -> LUNCH
                "dinner", "abendessen", "abendbrot", "abend" -> DINNER
                "snack", "zwischenmahlzeit" -> SNACK
                else -> SNACK
            }
        }
    }
}
