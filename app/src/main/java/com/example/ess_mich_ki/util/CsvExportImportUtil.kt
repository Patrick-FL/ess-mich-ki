package com.example.ess_mich_ki.util

import com.example.ess_mich_ki.data.local.model.MealWithItems
import java.util.Locale

data class LoggedMealCsvRecord(
    val dateString: String,
    val mealType: String,
    val itemName: String,
    val quantity: Double,
    val servingName: String,
    val servingGrams: Double,
    val calories: Double,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val isChecked: Boolean = true
)

object CsvExportImportUtil {

    private const val HEADER = "Datum,Mahlzeit,Lebensmittel,Menge,Einheit,Gramm,Kalorien,Protein,Kohlenhydrate,Fett,Erfasst"

    fun exportToCsv(mealsWithItems: List<MealWithItems>): String {
        val sb = StringBuilder()
        sb.append(HEADER).append("\n")

        for (mealWithItems in mealsWithItems) {
            val meal = mealWithItems.meal
            for (item in mealWithItems.items) {
                val line = listOf(
                    escapeCsvField(meal.dateString),
                    escapeCsvField(meal.mealType),
                    escapeCsvField(item.name),
                    formatDouble(item.quantity),
                    escapeCsvField(item.servingName),
                    formatDouble(item.servingGrams),
                    formatDouble(item.calories),
                    formatDouble(item.protein),
                    formatDouble(item.carbs),
                    formatDouble(item.fat),
                    item.isChecked.toString()
                ).joinToString(",")

                sb.append(line).append("\n")
            }
        }

        return sb.toString()
    }

    fun parseCsv(csvContent: String): List<LoggedMealCsvRecord> {
        val lines = csvContent.lines().filter { it.isNotBlank() }
        if (lines.isEmpty()) return emptyList()

        val headerLine = lines.firstOrNull { isHeaderLine(it) } ?: lines.first()
        val delimiter = if (headerLine.contains(";")) ';' else ','

        val records = mutableListOf<LoggedMealCsvRecord>()
        val startIdx = if (isHeaderLine(lines.first())) 1 else 0

        for (i in startIdx until lines.size) {
            val line = lines[i]
            val fields = parseCsvLine(line, delimiter)
            if (fields.size >= 10) {
                val dateString = fields[0].trim()
                val mealType = fields[1].trim()
                val itemName = fields[2].trim()
                val quantity = parseDouble(fields[3], 1.0)
                val servingName = fields[4].trim().ifBlank { "portion" }
                val servingGrams = parseDouble(fields[5], 100.0)
                val calories = parseDouble(fields[6], 0.0)
                val protein = parseDouble(fields[7], 0.0)
                val carbs = parseDouble(fields[8], 0.0)
                val fat = parseDouble(fields[9], 0.0)
                val isChecked = if (fields.size >= 11) fields[10].trim().lowercase().let { it == "true" || it == "1" || it == "ja" } else true

                if (dateString.isNotBlank() && itemName.isNotBlank()) {
                    records.add(
                        LoggedMealCsvRecord(
                            dateString = dateString,
                            mealType = mealType.ifBlank { "Snack" },
                            itemName = itemName,
                            quantity = quantity,
                            servingName = servingName,
                            servingGrams = servingGrams,
                            calories = calories,
                            protein = protein,
                            carbs = carbs,
                            fat = fat,
                            isChecked = isChecked
                        )
                    )
                }
            }
        }

        return records
    }

    private fun isHeaderLine(line: String): Boolean {
        val lower = line.lowercase()
        return lower.contains("datum") || lower.contains("date") || lower.contains("mahlzeit") || lower.contains("meal")
    }

    private fun escapeCsvField(field: String): String {
        return if (field.contains(",") || field.contains("\"") || field.contains("\n") || field.contains(";")) {
            "\"" + field.replace("\"", "\"\"") + "\""
        } else {
            field
        }
    }

    private fun parseCsvLine(line: String, delimiter: Char = ','): List<String> {
        val tokens = mutableListOf<String>()
        val sb = StringBuilder()
        var inQuotes = false

        var i = 0
        while (i < line.length) {
            val c = line[i]
            if (c == '"') {
                if (inQuotes && i + 1 < line.length && line[i + 1] == '"') {
                    sb.append('"')
                    i++
                } else {
                    inQuotes = !inQuotes
                }
            } else if (c == delimiter && !inQuotes) {
                tokens.add(sb.toString())
                sb.clear()
            } else {
                sb.append(c)
            }
            i++
        }
        tokens.add(sb.toString())
        return tokens
    }

    private fun formatDouble(value: Double): String {
        return if (value % 1.0 == 0.0) {
            value.toInt().toString()
        } else {
            String.format(Locale.US, "%.2f", value)
        }
    }

    private fun parseDouble(value: String, default: Double): Double {
        val cleaned = value.trim().replace(",", ".")
        return cleaned.toDoubleOrNull() ?: default
    }
}
