package com.example.ess_mich_ki.ui

import com.example.ess_mich_ki.data.local.entity.FoodItemEntity
import com.example.ess_mich_ki.data.local.entity.UserGoalEntity
import com.example.ess_mich_ki.ui.detail.FoodDetailUiState
import com.example.ess_mich_ki.ui.detail.ServingOption
import com.example.ess_mich_ki.ui.progress.DailyCalorieStat
import com.example.ess_mich_ki.ui.progress.ProgressUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class Task3ViewModelsTest {

    @Test
    fun testFoodDetailUiState_MacroCalculations() {
        val food = FoodItemEntity(
            id = 3,
            name = "Himbeere Raspberry",
            category = "Fruits",
            calories = 52.0,
            proteinGrams = 1.2,
            carbsGrams = 12.0,
            fatGrams = 0.6,
            servingName = "1 Portion (100g)",
            servingGrams = 100.0
        )

        val state = FoodDetailUiState(
            foodId = 3,
            food = food,
            selectedQuantity = 1.5,
            selectedServingOption = ServingOption("Tasse (150 g)", 150.0)
        )

        // totalGrams = 1.5 * 150 = 225g
        // factor = 225 / 100 = 2.25
        assertEquals(225.0, state.totalGrams, 0.01)
        assertEquals(2.25, state.factor, 0.01)
        assertEquals(117.0, state.calculatedCalories, 0.01) // 52 * 2.25 = 117.0
        assertEquals(27.0, state.calculatedCarbs, 0.01)    // 12 * 2.25 = 27.0
    }

    @Test
    fun testProgressUiState_WeightProgress() {
        val userGoal = UserGoalEntity(
            id = 1,
            goalType = "Fat Loss",
            initialWeight = 64.2,
            currentWeight = 63.0,
            goalWeight = 59.0,
            paceWeeks = 5,
            calorieTarget = 1284,
            startDateString = "2026-10-01"
        )

        val state = ProgressUiState(
            userGoal = userGoal
        )

        assertEquals(63.0, state.currentWeight, 0.01)
        assertEquals(64.2, state.initialWeight, 0.01)
        assertEquals(59.0, state.targetWeight, 0.01)

        // Total to lose = 64.2 - 59 = 5.2
        // Lost = 64.2 - 63 = 1.2
        // Progress = 1.2 / 5.2 = 0.2307 (23%)
        assertEquals(0.23f, state.progressPercentage, 0.02f)
    }

    @Test
    fun testProgressUiState_HungerCalculationsAndComparisons() {
        val today = java.time.LocalDate.now()
        val day1 = com.example.ess_mich_ki.ui.progress.HungerDayDetail(
            date = today.minusDays(1),
            dateString = today.minusDays(1).toString(),
            dayOfMonth = today.minusDays(1).dayOfMonth,
            dayLabel = "Mo",
            maxHunger = 9,
            avgHunger = 7.5,
            breakfastHunger = 6,
            lunchHunger = 9,
            dinnerHunger = null,
            snackNotes = "Craving chocolate",
            totalCalories = 2200
        )
        val day2 = com.example.ess_mich_ki.ui.progress.HungerDayDetail(
            date = today,
            dateString = today.toString(),
            dayOfMonth = today.dayOfMonth,
            dayLabel = "Di",
            maxHunger = 3,
            avgHunger = 2.5,
            breakfastHunger = 2,
            lunchHunger = 3,
            dinnerHunger = null,
            snackNotes = null,
            totalCalories = 1400
        )

        val stateMax = ProgressUiState(
            hunger30Days = listOf(day1, day2),
            hungerCalcMode = com.example.ess_mich_ki.ui.progress.HungerCalculationMode.MAX
        )

        assertEquals(9.0, day1.displayScore(com.example.ess_mich_ki.ui.progress.HungerCalculationMode.MAX) ?: 0.0, 0.01)
        assertEquals(7.5, day1.displayScore(com.example.ess_mich_ki.ui.progress.HungerCalculationMode.AVERAGE) ?: 0.0, 0.01)

        assertEquals(1, stateMax.highHungerDays.size)
        assertEquals(day1, stateMax.highHungerDays.first())
        assertEquals(2200, stateMax.avgCaloriesHighHunger)

        assertEquals(1, stateMax.lowHungerDays.size)
        assertEquals(day2, stateMax.lowHungerDays.first())
        assertEquals(1400, stateMax.avgCaloriesLowHunger)
    }
}
