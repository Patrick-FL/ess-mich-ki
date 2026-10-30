package com.example.ess_mich_ki.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import kotlin.math.roundToInt

data class StreakResult(
    val currentLoggedStreak: Int = 0,
    val bestLoggedStreak: Int = 0,
    val currentPerfectStreak: Int = 0,
    val bestPerfectStreak: Int = 0
)

data class MonthDayInfo(
    val dayOfMonth: Int,
    val date: LocalDate?,
    val isToday: Boolean,
    val isLogged: Boolean,
    val isPerfect: Boolean,
    val calories: Int
)

data class CalendarWeekInfo(
    val days: List<MonthDayInfo>, // 7 entries (Mon-Sun)
    val avgCalories: Int
)

data class MonthlyCalendarData(
    val yearMonth: YearMonth,
    val monthTitle: String,
    val weeks: List<CalendarWeekInfo>
)

object StreakCalculator {

    fun isDayLogged(calories: Int?): Boolean {
        return calories != null && calories > 0
    }

    fun isDayPerfect(
        date: LocalDate,
        calories: Int?,
        minRange: Int,
        maxRange: Int,
        today: LocalDate,
        isTodayEnded: Boolean
    ): Boolean {
        if (calories == null || calories <= 0) return false
        val isFinished = date.isBefore(today) || (date == today && isTodayEnded)
        if (!isFinished) return false
        return calories in minRange..maxRange
    }

    fun calculateStreaks(
        dailyCalories: Map<LocalDate, Int>,
        dailyTargetRange: Map<LocalDate, IntRange> = emptyMap(),
        defaultMinRange: Int,
        defaultMaxRange: Int,
        today: LocalDate = LocalDate.now(),
        isTodayEnded: Boolean = false
    ): StreakResult {
        if (dailyCalories.isEmpty()) {
            return StreakResult()
        }

        val loggedDates = dailyCalories.filter { (_, calories) -> calories > 0 }.keys.toSet()

        // 1. Current Logged Streak
        var currentLogged = 0
        var checkDate = today
        if (!loggedDates.contains(today)) {
            checkDate = today.minusDays(1)
        }

        while (loggedDates.contains(checkDate)) {
            currentLogged++
            checkDate = checkDate.minusDays(1)
        }

        // 2. Best Logged Streak
        var bestLogged = 0
        if (loggedDates.isNotEmpty()) {
            val sortedLogged = loggedDates.sorted()
            var currentRun = 0
            var prevDate: LocalDate? = null

            for (date in sortedLogged) {
                if (prevDate == null || date == prevDate.plusDays(1)) {
                    currentRun++
                } else {
                    currentRun = 1
                }
                if (currentRun > bestLogged) {
                    bestLogged = currentRun
                }
                prevDate = date
            }
        }

        // 3. Perfect Days Set
        val perfectDates = mutableSetOf<LocalDate>()
        for ((date, calories) in dailyCalories) {
            val range = dailyTargetRange[date] ?: (defaultMinRange..defaultMaxRange)
            if (isDayPerfect(date, calories, range.first, range.last, today, isTodayEnded)) {
                perfectDates.add(date)
            }
        }

        // 4. Current Perfect Streak
        var currentPerfect = 0
        var perfectCheckDate = today
        if (!perfectDates.contains(today)) {
            perfectCheckDate = today.minusDays(1)
        }

        while (perfectDates.contains(perfectCheckDate)) {
            currentPerfect++
            perfectCheckDate = perfectCheckDate.minusDays(1)
        }

        // 5. Best Perfect Streak
        var bestPerfect = 0
        if (perfectDates.isNotEmpty()) {
            val sortedPerfect = perfectDates.sorted()
            var currentRun = 0
            var prevDate: LocalDate? = null

            for (date in sortedPerfect) {
                if (prevDate == null || date == prevDate.plusDays(1)) {
                    currentRun++
                } else {
                    currentRun = 1
                }
                if (currentRun > bestPerfect) {
                    bestPerfect = currentRun
                }
                prevDate = date
            }
        }

        return StreakResult(
            currentLoggedStreak = currentLogged,
            bestLoggedStreak = bestLogged,
            currentPerfectStreak = currentPerfect,
            bestPerfectStreak = bestPerfect
        )
    }

    fun calculateMonthlyCalendar(
        dailyCalories: Map<LocalDate, Int>,
        dailyTargetRange: Map<LocalDate, IntRange> = emptyMap(),
        defaultMinRange: Int,
        defaultMaxRange: Int,
        yearMonth: YearMonth = YearMonth.now(),
        today: LocalDate = LocalDate.now(),
        isTodayEnded: Boolean = false
    ): MonthlyCalendarData {
        val monthTitle = yearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH))
        val firstDayOfMonth = yearMonth.atDay(1)
        val lastDayOfMonth = yearMonth.atEndOfMonth()

        // Month grid starting on Monday
        val gridStartDate = firstDayOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val gridEndDate = lastDayOfMonth.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

        val weeks = mutableListOf<CalendarWeekInfo>()
        var currentDate = gridStartDate

        while (!currentDate.isAfter(gridEndDate)) {
            val weekDays = mutableListOf<MonthDayInfo>()
            var weekCaloriesSum = 0
            var loggedDaysInWeekCount = 0

            repeat(7) {
                val dateInMonth = currentDate.month == yearMonth.month && currentDate.year == yearMonth.year
                val dayNum = if (dateInMonth) currentDate.dayOfMonth else 0
                val calories = if (dateInMonth) dailyCalories[currentDate] ?: 0 else 0
                val range = dailyTargetRange[currentDate] ?: (defaultMinRange..defaultMaxRange)

                val logged = dateInMonth && isDayLogged(calories)
                val perfect = dateInMonth && isDayPerfect(currentDate, calories, range.first, range.last, today, isTodayEnded)

                weekDays.add(
                    MonthDayInfo(
                        dayOfMonth = dayNum,
                        date = if (dateInMonth) currentDate else null,
                        isToday = dateInMonth && currentDate == today,
                        isLogged = logged,
                        isPerfect = perfect,
                        calories = calories
                    )
                )

                if (logged) {
                    weekCaloriesSum += calories
                    loggedDaysInWeekCount++
                }

                currentDate = currentDate.plusDays(1)
            }

            val avgKcal = if (loggedDaysInWeekCount > 0) {
                (weekCaloriesSum.toDouble() / loggedDaysInWeekCount).roundToInt()
            } else 0

            weeks.add(CalendarWeekInfo(days = weekDays, avgCalories = avgKcal))
        }

        return MonthlyCalendarData(
            yearMonth = yearMonth,
            monthTitle = monthTitle,
            weeks = weeks
        )
    }
}
