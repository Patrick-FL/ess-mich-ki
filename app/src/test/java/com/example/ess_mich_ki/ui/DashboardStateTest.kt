package com.example.ess_mich_ki.ui

import com.example.ess_mich_ki.data.local.entity.LoggedMealEntity
import com.example.ess_mich_ki.data.local.entity.LoggedMealItemEntity
import com.example.ess_mich_ki.data.local.model.MealWithItems
import com.example.ess_mich_ki.ui.dashboard.DashboardUiState
import com.example.ess_mich_ki.ui.dashboard.DayItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class DashboardStateTest {

    @Test
    fun testDashboardUiStateDefaults() {
        val today = LocalDate.now()
        val state = DashboardUiState(
            selectedDate = today,
            streakCount = 1,
            calorieConsumed = 632,
            calorieTarget = 1581,
            proteinConsumed = 27,
            proteinTarget = 113,
            carbsConsumed = 84,
            carbsTarget = 163,
            fatConsumed = 22,
            fatTarget = 53
        )

        assertEquals(today, state.selectedDate)
        assertEquals(1, state.streakCount)
        assertEquals(632, state.calorieConsumed)
        assertEquals(1581, state.calorieTarget)
        assertEquals(27, state.proteinConsumed)
        assertEquals(84, state.carbsConsumed)
        assertEquals(22, state.fatConsumed)
        assertFalse(state.isDayEnded)
        assertFalse(state.isAiDialogShowing)
        assertFalse(state.isProfileDrawerOpen)
    }

    @Test
    fun testMealWithItemsTotals() {
        val meal = LoggedMealEntity(
            id = 1,
            dateString = "2026-10-22",
            mealType = "Breakfast"
        )

        val items = listOf(
            LoggedMealItemEntity(
                id = 1,
                mealId = 1,
                name = "Soy Yogurt",
                calories = 108.0,
                protein = 8.0,
                carbs = 4.2,
                fat = 4.6,
                isChecked = true
            ),
            LoggedMealItemEntity(
                id = 2,
                mealId = 1,
                name = "Chocolate Without Sugar",
                calories = 120.0,
                protein = 1.8,
                carbs = 7.5,
                fat = 9.5,
                isChecked = true
            ),
            LoggedMealItemEntity(
                id = 3,
                mealId = 1,
                name = "Unchecked Snack",
                calories = 200.0,
                protein = 10.0,
                carbs = 20.0,
                fat = 5.0,
                isChecked = false
            )
        )

        val mealWithItems = MealWithItems(meal = meal, items = items)

        val checkedItems = mealWithItems.items.filter { it.isChecked }
        val totalCalories = checkedItems.sumOf { it.calories }

        assertEquals(2, checkedItems.size)
        assertEquals(228.0, totalCalories, 0.01)
    }

    @Test
    fun testDistinctMealsPreventsDuplicateCalorieAccumulation() {
        val meal1 = LoggedMealEntity(id = 1, dateString = "2026-10-22", mealType = "Breakfast")
        val items1 = listOf(
            LoggedMealItemEntity(id = 1, mealId = 1, name = "Apple", calories = 100.0, isChecked = true)
        )
        val mealWithItems1 = MealWithItems(meal = meal1, items = items1)

        val meal2 = LoggedMealEntity(id = 2, dateString = "2026-10-22", mealType = "Breakfast")
        val items2 = listOf(
            LoggedMealItemEntity(id = 2, mealId = 2, name = "Apple Duplicate", calories = 100.0, isChecked = true)
        )
        val mealWithItems2 = MealWithItems(meal = meal2, items = items2)

        val rawMeals = listOf(mealWithItems1, mealWithItems2)
        val distinctMeals = rawMeals.distinctBy { it.meal.mealType.lowercase() }

        val checkedItems = distinctMeals.flatMap { it.items }.filter { it.isChecked }
        val totalCalories = checkedItems.sumOf { it.calories }

        assertEquals(1, distinctMeals.size)
        assertEquals(100.0, totalCalories, 0.01)
    }

    @Test
    fun testDayItem() {
        val today = LocalDate.now()
        val dayItem = DayItem(
            date = today,
            dayNameLetter = "T",
            dayNumber = today.dayOfMonth,
            isSelected = true,
            isToday = true
        )

        assertTrue(dayItem.isSelected)
        assertTrue(dayItem.isToday)
        assertEquals("T", dayItem.dayNameLetter)
    }
}
