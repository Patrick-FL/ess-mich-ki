package com.example.ess_mich_ki.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

class StreakCalculatorTest {

    private val today = LocalDate.of(2026, 10, 23)
    private val minRange = 1423
    private val maxRange = 1739

    @Test
    fun `empty daily calories produces 0 streaks`() {
        val result = StreakCalculator.calculateStreaks(
            dailyCalories = emptyMap(),
            defaultMinRange = minRange,
            defaultMaxRange = maxRange,
            today = today
        )

        assertEquals(0, result.currentLoggedStreak)
        assertEquals(0, result.bestLoggedStreak)
        assertEquals(0, result.currentPerfectStreak)
        assertEquals(0, result.bestPerfectStreak)
    }

    @Test
    fun `consecutive logged days calculates current and best logged streak correctly`() {
        // Logged: Oct 20, 21, 22, 23 (4 consecutive days up to today)
        val dailyCalories = mapOf(
            today.minusDays(3) to 1500, // Oct 20
            today.minusDays(2) to 1600, // Oct 21
            today.minusDays(1) to 1550, // Oct 22
            today to 1200              // Oct 23
        )

        val result = StreakCalculator.calculateStreaks(
            dailyCalories = dailyCalories,
            defaultMinRange = minRange,
            defaultMaxRange = maxRange,
            today = today
        )

        assertEquals(4, result.currentLoggedStreak)
        assertEquals(4, result.bestLoggedStreak)
    }

    @Test
    fun `streak preserves yesterday when today is not logged yet`() {
        // Logged: Oct 20, 21, 22. Today Oct 23 is NOT logged yet.
        val dailyCalories = mapOf(
            today.minusDays(3) to 1500,
            today.minusDays(2) to 1600,
            today.minusDays(1) to 1550
        )

        val result = StreakCalculator.calculateStreaks(
            dailyCalories = dailyCalories,
            defaultMinRange = minRange,
            defaultMaxRange = maxRange,
            today = today
        )

        assertEquals(3, result.currentLoggedStreak)
        assertEquals(3, result.bestLoggedStreak)
    }

    @Test
    fun `best logged streak tracks longest historical run`() {
        // Logged: Oct 1..Oct 5 (5 days), then gap, then Oct 10..Oct 12 (3 days)
        val dailyCalories = mapOf(
            today.minusDays(22) to 1000,
            today.minusDays(21) to 1000,
            today.minusDays(20) to 1000,
            today.minusDays(19) to 1000,
            today.minusDays(18) to 1000,
            today.minusDays(2) to 1000,
            today.minusDays(1) to 1000,
            today to 1000
        )

        val result = StreakCalculator.calculateStreaks(
            dailyCalories = dailyCalories,
            defaultMinRange = minRange,
            defaultMaxRange = maxRange,
            today = today
        )

        assertEquals(3, result.currentLoggedStreak)
        assertEquals(5, result.bestLoggedStreak)
    }

    @Test
    fun `perfect days count only finished days in target range`() {
        // Oct 21: 1500 (in range [1423..1739]) -> Perfect
        // Oct 22: 1550 (in range [1423..1739]) -> Perfect
        // Oct 23 (today): 1500 (in range, but today is NOT ended yet) -> NOT perfect yet
        val dailyCalories = mapOf(
            today.minusDays(2) to 1500,
            today.minusDays(1) to 1550,
            today to 1500
        )

        val resultNotEnded = StreakCalculator.calculateStreaks(
            dailyCalories = dailyCalories,
            defaultMinRange = minRange,
            defaultMaxRange = maxRange,
            today = today,
            isTodayEnded = false
        )

        assertEquals(2, resultNotEnded.currentPerfectStreak)
        assertEquals(2, resultNotEnded.bestPerfectStreak)

        val resultEnded = StreakCalculator.calculateStreaks(
            dailyCalories = dailyCalories,
            defaultMinRange = minRange,
            defaultMaxRange = maxRange,
            today = today,
            isTodayEnded = true
        )

        assertEquals(3, resultEnded.currentPerfectStreak)
        assertEquals(3, resultEnded.bestPerfectStreak)
    }

    @Test
    fun `calculate monthly calendar formats weeks and averages correctly`() {
        val oct2026 = YearMonth.of(2026, 10)
        val dailyCalories = mapOf(
            LocalDate.of(2026, 10, 1) to 1000,
            LocalDate.of(2026, 10, 2) to 1500,
            LocalDate.of(2026, 10, 3) to 2000
        )

        val monthData = StreakCalculator.calculateMonthlyCalendar(
            dailyCalories = dailyCalories,
            defaultMinRange = minRange,
            defaultMaxRange = maxRange,
            yearMonth = oct2026,
            today = today
        )

        assertEquals("October 2026", monthData.monthTitle)
        assertTrue(monthData.weeks.isNotEmpty())

        // First week containing Oct 1, 2, 3
        val week1 = monthData.weeks.find { week ->
            week.days.any { it.dayOfMonth == 1 }
        }
        assertTrue(week1 != null)
        assertEquals(1500, week1!!.avgCalories) // (1000+1500+2000)/3 = 1500
    }
}
