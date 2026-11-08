package com.example.ess_mich_ki.ui.progress

import com.example.ess_mich_ki.data.local.entity.UserGoalEntity
import com.example.ess_mich_ki.data.local.entity.WeightLogEntity

import java.time.LocalDate
import kotlin.math.roundToInt

enum class HungerCalculationMode {
    MAX,
    AVERAGE
}

data class HungerDayDetail(
    val date: LocalDate,
    val dateString: String,
    val dayOfMonth: Int,
    val dayLabel: String,
    val maxHunger: Int?,
    val avgHunger: Double?,
    val breakfastHunger: Int?,
    val lunchHunger: Int?,
    val dinnerHunger: Int?,
    val snackNotes: String?,
    val totalCalories: Int
) {
    fun displayScore(mode: HungerCalculationMode): Double? {
        return when (mode) {
            HungerCalculationMode.MAX -> maxHunger?.toDouble()
            HungerCalculationMode.AVERAGE -> avgHunger
        }
    }
}

data class DailyCalorieStat(
    val dayLabel: String,  // "M", "T", "W", "T", "F", "S", "S"
    val dateString: String,
    val calories: Int,
    val targetCalories: Int,
    val isCompliant: Boolean
)

data class ProgressUiState(
    val userGoal: UserGoalEntity? = null,
    val weeklyCalorieStats: List<DailyCalorieStat> = emptyList(),
    val averageWeeklyCalories: Int = 0,
    val weightLogs: List<WeightLogEntity> = emptyList(),
    val isEditGoalShowing: Boolean = false,
    val isAddWeightShowing: Boolean = false,
    val isManageWeightShowing: Boolean = false,
    val hunger30Days: List<HungerDayDetail> = emptyList(),
    val selectedHungerDay: HungerDayDetail? = null,
    val hungerCalcMode: HungerCalculationMode = HungerCalculationMode.MAX
) {
    val currentWeight: Double
        get() = weightLogs.lastOrNull()?.weightKg ?: userGoal?.currentWeight ?: 63.0

    val initialWeight: Double
        get() = userGoal?.initialWeight ?: 64.2

    val targetWeight: Double
        get() = userGoal?.goalWeight ?: 59.0

    val progressPercentage: Float
        get() {
            val totalToLose = initialWeight - targetWeight
            if (totalToLose <= 0) return 1f
            val currentLost = initialWeight - currentWeight
            return (currentLost / totalToLose).toFloat().coerceIn(0f, 1f)
        }

    val highHungerDays: List<HungerDayDetail>
        get() = hunger30Days.filter { (it.displayScore(hungerCalcMode) ?: 0.0) >= 7.0 }

    val lowHungerDays: List<HungerDayDetail>
        get() = hunger30Days.filter {
            val score = it.displayScore(hungerCalcMode)
            score != null && score <= 4.0
        }

    val avgCaloriesHighHunger: Int
        get() = if (highHungerDays.isNotEmpty()) {
            highHungerDays.map { it.totalCalories }.average().roundToInt()
        } else 0

    val avgCaloriesLowHunger: Int
        get() = if (lowHungerDays.isNotEmpty()) {
            lowHungerDays.map { it.totalCalories }.average().roundToInt()
        } else 0
}
