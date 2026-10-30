package com.example.ess_mich_ki.util

import com.example.ess_mich_ki.data.local.entity.LoggedMealEntity
import com.example.ess_mich_ki.data.local.entity.LoggedMealItemEntity
import com.example.ess_mich_ki.data.local.model.MealWithItems
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CsvExportImportUtilTest {

    @Test
    fun exportToCsv_generatesValidCsvHeaderAndRows() {
        val meal = LoggedMealEntity(
            id = 1L,
            dateString = "2026-10-24",
            mealType = "Frühstück",
            totalCalories = 300.0,
            totalProtein = 10.0,
            totalCarbs = 40.0,
            totalFat = 5.0
        )
        val item = LoggedMealItemEntity(
            id = 10L,
            mealId = 1L,
            name = "Haferflocken mit Milch",
            quantity = 1.0,
            servingName = "Schüssel",
            servingGrams = 250.0,
            calories = 300.0,
            protein = 10.0,
            carbs = 40.0,
            fat = 5.0,
            isChecked = true
        )

        val mealsWithItems = listOf(MealWithItems(meal, listOf(item)))

        val csv = CsvExportImportUtil.exportToCsv(mealsWithItems)

        assertTrue(csv.contains("Datum,Mahlzeit,Lebensmittel,Menge,Einheit,Gramm,Kalorien,Protein,Kohlenhydrate,Fett,Erfasst"))
        assertTrue(csv.contains("2026-10-24,Frühstück,Haferflocken mit Milch,1,Schüssel,250,300,10,40,5,true"))
    }

    @Test
    fun parseCsv_parsesValidCsvContentCorrectly() {
        val csvContent = """
            Datum,Mahlzeit,Lebensmittel,Menge,Einheit,Gramm,Kalorien,Protein,Kohlenhydrate,Fett,Erfasst
            2026-10-24,Mittagessen,Apfel,2,Stück,150,104,0.5,28,0.3,true
            2026-10-25,Abendessen,"Salat mit Öl, Essig",1,Portion,200,150,2,5,12,false
        """.trimIndent()

        val records = CsvExportImportUtil.parseCsv(csvContent)

        assertEquals(2, records.size)

        val rec1 = records[0]
        assertEquals("2026-10-24", rec1.dateString)
        assertEquals("Mittagessen", rec1.mealType)
        assertEquals("Apfel", rec1.itemName)
        assertEquals(2.0, rec1.quantity, 0.001)
        assertEquals("Stück", rec1.servingName)
        assertEquals(150.0, rec1.servingGrams, 0.001)
        assertEquals(104.0, rec1.calories, 0.001)
        assertEquals(0.5, rec1.protein, 0.001)
        assertEquals(28.0, rec1.carbs, 0.001)
        assertEquals(0.3, rec1.fat, 0.001)
        assertTrue(rec1.isChecked)

        val rec2 = records[1]
        assertEquals("2026-10-25", rec2.dateString)
        assertEquals("Salat mit Öl, Essig", rec2.itemName)
        assertEquals(false, rec2.isChecked)
    }

    @Test
    fun parseCsv_handlesSemicolonDelimiterAndCommaDecimals() {
        val csvContent = """
            Datum;Mahlzeit;Lebensmittel;Menge;Einheit;Gramm;Kalorien;Protein;Kohlenhydrate;Fett;Erfasst
            2026-10-24;Abendessen;Brot;1,5;Scheibe;50,0;120,5;4,2;20,0;1,5;ja
        """.trimIndent()

        val records = CsvExportImportUtil.parseCsv(csvContent)

        assertEquals(1, records.size)
        val rec = records[0]
        assertEquals("2026-10-24", rec.dateString)
        assertEquals("Brot", rec.itemName)
        assertEquals(1.5, rec.quantity, 0.001)
        assertEquals(120.5, rec.calories, 0.001)
        assertEquals(4.2, rec.protein, 0.001)
        assertTrue(rec.isChecked)
    }
}
