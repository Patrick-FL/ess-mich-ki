package com.example.ess_mich_ki.util

import com.example.ess_mich_ki.util.NutritionalGoalMath.NutritionalGoalState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NutritionalGoalMathTest {

    @Test
    fun testInitialMacroPercentCalculations() {
        // 113g Protein = 452 kcal
        // 163g Carbs = 652 kcal
        // 53g Fat = 477 kcal
        // Total = 1581 kcal
        val state = NutritionalGoalState(
            totalKcal = 1581,
            proteinGrams = 113,
            carbsGrams = 163,
            fatGrams = 53
        )

        assertEquals(1581, state.calculatedTotalKcalFromMacros)
        assertEquals(29, state.proteinKcalPercentage) // ~28.6%
        assertEquals(41, state.carbsKcalPercentage)   // ~41.2%
        assertEquals(30, state.fatKcalPercentage)     // ~30.2%
    }

    @Test
    fun testUpdateTotalKcal_ProportionalScaleUnlockedMacros() {
        val state = NutritionalGoalState(
            totalKcal = 2000,
            proteinGrams = 150, // 600 kcal (30%)
            carbsGrams = 200,   // 800 kcal (40%)
            fatGrams = 67,      // 603 kcal (30%)
            isKcalLocked = false,
            isProteinLocked = false,
            isCarbsLocked = false,
            isFatLocked = false
        )

        // Scale total kcal from 2003 to 1000 kcal
        val updated = NutritionalGoalMath.updateTotalKcal(state, 1000)

        assertEquals(1000, updated.totalKcal)
        assertTrue(updated.proteinGrams in 74..76) // ~75g
        assertTrue(updated.carbsGrams in 99..101)  // ~100g
        assertTrue(updated.fatGrams in 32..34)     // ~33g
    }

    @Test
    fun testUpdateTotalKcal_WithLockedProtein() {
        val state = NutritionalGoalState(
            totalKcal = 2000,
            proteinGrams = 150, // 600 kcal (LOCKED)
            carbsGrams = 200,   // 800 kcal
            fatGrams = 67,      // 603 kcal
            isProteinLocked = true
        )

        // Scale total kcal to 1500 kcal. Locked protein (150g = 600 kcal) stays fixed.
        // Remaining 900 kcal split proportionally between Carbs and Fat.
        val updated = NutritionalGoalMath.updateTotalKcal(state, 1500)

        assertEquals(1500, updated.totalKcal)
        assertEquals(150, updated.proteinGrams) // Locked!
        assertTrue(updated.carbsGrams < 200)
        assertTrue(updated.fatGrams < 67)
    }

    @Test
    fun testUpdateProtein_WhenTotalKcalUnlocked() {
        val state = NutritionalGoalState(
            totalKcal = 2000,
            proteinGrams = 100, // 400 kcal
            carbsGrams = 200,   // 800 kcal
            fatGrams = 80,      // 720 kcal
            isKcalLocked = false
        )

        // Increase protein from 100g to 150g (+200 kcal)
        val updated = NutritionalGoalMath.updateProtein(state, 150)

        assertEquals(150, updated.proteinGrams)
        assertEquals(200, updated.carbsGrams) // unchanged
        assertEquals(80, updated.fatGrams)    // unchanged
        assertEquals(2120, updated.totalKcal)  // recalculated (1920 + 200 = 2120)
    }

    @Test
    fun testUpdateProtein_WhenTotalKcalLocked_RedistributesDeltaToOtherUnlocked() {
        val state = NutritionalGoalState(
            totalKcal = 2000,
            proteinGrams = 100, // 400 kcal
            carbsGrams = 200,   // 800 kcal
            fatGrams = 89,      // 801 kcal
            isKcalLocked = true,
            isProteinLocked = false,
            isCarbsLocked = false,
            isFatLocked = false
        )

        // Increase protein from 100g to 150g (+200 kcal delta).
        // Total Kcal must stay at 2000. Carbs and Fat must decrease proportionally to absorb -200 kcal.
        val updated = NutritionalGoalMath.updateProtein(state, 150)

        assertEquals(2000, updated.totalKcal) // Total kcal locked
        assertEquals(150, updated.proteinGrams)
        assertTrue(updated.carbsGrams < 200)
        assertTrue(updated.fatGrams < 89)

        // Sum of macros should equal ~2000 kcal
        val recalculatedKcal = (updated.proteinGrams * 4) + (updated.carbsGrams * 4) + (updated.fatGrams * 9)
        assertTrue(recalculatedKcal in 1990..2010)
    }

    @Test
    fun testFastingDayToggle_HalvesEffectiveValuesWithoutDrift() {
        val state = NutritionalGoalState(
            totalKcal = 2000,
            proteinGrams = 150,
            carbsGrams = 200,
            fatGrams = 60,
            isFastingDay = false
        )

        // Activate Fasting Day
        val fastingState = NutritionalGoalMath.toggleFastingDay(state)

        assertTrue(fastingState.isFastingDay)
        assertEquals(1000, fastingState.effectiveKcal)
        assertEquals(75, fastingState.effectiveProteinGrams)
        assertEquals(100, fastingState.effectiveCarbsGrams)
        assertEquals(30, fastingState.effectiveFatGrams)

        // Baseline values in state remain 100% untouched
        assertEquals(2000, fastingState.totalKcal)
        assertEquals(150, fastingState.proteinGrams)
        assertEquals(200, fastingState.carbsGrams)
        assertEquals(60, fastingState.fatGrams)

        // Deactivate Fasting Day
        val restoredState = NutritionalGoalMath.toggleFastingDay(fastingState)

        assertFalse(restoredState.isFastingDay)
        assertEquals(2000, restoredState.effectiveKcal)
        assertEquals(150, restoredState.effectiveProteinGrams)
        assertEquals(200, restoredState.effectiveCarbsGrams)
        assertEquals(60, restoredState.effectiveFatGrams)
    }

    @Test
    fun testAutoLockingConstraint_3LockRule() {
        val state = NutritionalGoalState(
            totalKcal = 2000,
            proteinGrams = 100,
            carbsGrams = 200,
            fatGrams = 89,
            isKcalLocked = true,
            isProteinLocked = true,
            isCarbsLocked = true,
            isFatLocked = false
        )

        // Since Kcal, Protein, and Carbs are locked, Fat MUST be auto-locked because its value is fixed
        assertTrue(state.isFatAutoLocked)
    }
}
