package com.example.ess_mich_ki.data

import com.example.ess_mich_ki.data.local.database.AppDatabase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SampleDataTest {

    @Test
    fun testPrepopulatedSampleFoodsContainRequiredItems() {
        val sampleFoods = AppDatabase.getSampleFoods()

        assertTrue(sampleFoods.isNotEmpty())

        val soyYogurt = sampleFoods.find { it.name.contains("Soy Yogurt", ignoreCase = true) }
        assertNotNull("Soy Yogurt must be present in reference foods", soyYogurt)
        assertEquals("Dairy & Alternatives", soyYogurt?.category)
        assertTrue((soyYogurt?.calories ?: 0.0) > 0.0)

        val chocolate = sampleFoods.find { it.name.contains("Chocolate Without Sugar", ignoreCase = true) }
        assertNotNull("Chocolate Without Sugar must be present in reference foods", chocolate)
        assertEquals("Sweets & Snacks", chocolate?.category)

        val raspberry = sampleFoods.find { it.name.contains("Himbeere", ignoreCase = true) || it.name.contains("Raspberry", ignoreCase = true) }
        assertNotNull("Himbeere Raspberry must be present in reference foods", raspberry)
        assertEquals("Fruits", raspberry?.category)
    }
}
